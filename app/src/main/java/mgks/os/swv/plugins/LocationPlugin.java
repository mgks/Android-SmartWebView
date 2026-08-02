package mgks.os.swv.plugins;

/*
  Location Plugin for Smart WebView

  This plugin provides access to the device's GPS location services, allowing the
  web application to retrieve the current latitude and longitude.

  FEATURES:
  - Fetches location using both GPS and Network providers.
  - Provides a clean JavaScript interface for on-demand location requests.
  - Handles runtime permission requests.
  - Includes a legacy cookie-based method for backward compatibility.
  - Automatically stops listening for updates to conserve battery.

  USAGE:
  1. Enable the `ASWP_LOCATION` flag in SWVContext.java.
  2. From JavaScript, call `window.Location.getCurrentPosition(callback)`.
  3. The callback function will receive `(latitude, longitude, error)`.

  // Example JavaScript:
  window.Location.getCurrentPosition(function(lat, lng, error) {
    if (error) {
      console.error("Location Error:", error);
      return;
    }
    alert("Latitude: " + lat + ", Longitude: " + lng);
  });
*/

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.Map;

import mgks.os.swv.Functions;
import mgks.os.swv.PermissionManager;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;
import mgks.os.swv.SWVContext;

public class LocationPlugin implements PluginInterface, LocationListener {
    private static final String TAG = "LocationPlugin";
    private Activity activity;
    private WebView webView;
    private LocationManager locationManager;
    private PermissionManager permissionManager;
    private Functions functions;

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    private String pendingJsCallback = null;

    static {
        PluginManager.registerPlugin(new LocationPlugin(), new HashMap<>());
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView;
        this.functions = functions;
        this.permissionManager = new PermissionManager(activity);
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        webView.addJavascriptInterface(new LocationJSInterface(), "LocationInterface");
        Log.d(TAG, "LocationPlugin initialized.");

        // Optionally fetch location on launch if permissions are already granted
        if (permissionManager.isLocationPermissionGranted()) {
            getLocation(null); // Fetch once for cookie setting on launch
        }
    }

    public void getLocation(String jsCallbackName) {
        pendingJsCallback = jsCallbackName;
        if (!permissionManager.isLocationPermissionGranted()) {
            permissionManager.requestInitialPermissions();
            return;
        }

        try {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.w(TAG, "No location provider is enabled.");
                sendLocationError("Location services disabled.");
                return;
            }

            Location location = null;

            // Prioritize GPS
            if (isGPSEnabled) {
                // Try-catch for the security exception here
                try {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this, Looper.getMainLooper());
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                } catch (SecurityException se) {
                    Log.e(TAG, "SecurityException while accessing GPS. Mock locations might be enabled.", se);
                    sendLocationError("Security error: Mock locations may be enabled in Developer Options.");
                    return; // Stop further execution
                }
            }

            // Fallback to Network if GPS location is null
            if (location == null && isNetworkEnabled) {
                try {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this, Looper.getMainLooper());
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                } catch (SecurityException se) {
                    Log.e(TAG, "SecurityException while accessing Network location. Mock locations might be enabled.", se);
                    sendLocationError("Security error: Mock locations may be enabled in Developer Options.");
                    return; // Stop further execution
                }
            }

            if (location != null) {
                handleNewLocation(location);
            } else {
                Log.d(TAG, "No fresh fix; trying GeolocationCachePlugin fallback for #387.");
                // Fix for issue #387: on offline / GPS-less tablets there is no fix.
                // Fall back to the last cached location so the offline page shows
                // something useful instead of "Error: 7".
                GeolocationCachePlugin cachePlugin = (GeolocationCachePlugin)
                        SWVContext.getPluginManager().getPluginInstance("GeolocationCachePlugin");
                if (cachePlugin != null) {
                    // The cache plugin persists the previous fix and we can synthesise
                    // a Location from it. We only do this as a last resort.
                    android.location.Location cachedLoc = readCachedLocation(cachePlugin);
                    if (cachedLoc != null) {
                        Log.d(TAG, "Using cached location as fallback for offline page.");
                        handleNewLocation(cachedLoc);
                        return;
                    }
                }
                Log.d(TAG, "Last known location not available, waiting for updates...");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting location", e);
            sendLocationError("Error fetching location: " + e.getMessage());
        }
    }

    private void handleNewLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Persist the fresh fix so the offline page has a fallback next time (#387).
        try {
            GeolocationCachePlugin cachePlugin = (GeolocationCachePlugin)
                    SWVContext.getPluginManager().getPluginInstance("GeolocationCachePlugin");
            if (cachePlugin != null) {
                cachePlugin.cache(latitude, longitude);
            }
        } catch (Exception ignored) {}

        // Set Cookies (for legacy support)
        if (SWVContext.true_online) {
            functions.set_cookie("lat=" + latitude);
            functions.set_cookie("long=" + longitude);
        }

        // Send via JS Callback if requested
        if (pendingJsCallback != null) {
            sendLocationToJs(pendingJsCallback, location, null);
            pendingJsCallback = null;
        }

        // Stop listening after getting a fix if only requested once
        stopListening();
    }

    private void sendLocationError(String message) {
        sendLocationToJs(pendingJsCallback, null, message);
        pendingJsCallback = null;
        stopListening();
    }

    private void stopListening() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    /**
     * Read the last cached location from the GeolocationCachePlugin (if enabled)
     * and return it as a fresh Location object, or null if no cache exists.
     * Used as a fallback when GPS/Network providers fail — fixes issue #387
     * ("GPS Location not fetched in offline site").
     */
    private android.location.Location readCachedLocation(GeolocationCachePlugin cachePlugin) {
        try {
            android.content.Context ctx = activity.getApplicationContext();
            android.content.SharedPreferences sp = ctx.getSharedPreferences("swv_geocache", android.content.Context.MODE_PRIVATE);
            if (!sp.contains("last_lat")) return null;
            double lat = Double.longBitsToDouble(sp.getLong("last_lat", 0));
            double lng = Double.longBitsToDouble(sp.getLong("last_lng", 0));
            long time = sp.getLong("last_time", 0);
            android.location.Location l = new android.location.Location("swv_cache");
            l.setLatitude(lat);
            l.setLongitude(lng);
            if (time != 0) l.setTime(time);
            return l;
        } catch (Exception e) {
            Log.w(TAG, "Failed to read cached location", e);
            return null;
        }
    }

    // --- LocationListener Implementation ---
    @Override
    public void onLocationChanged(@NonNull Location location) {
        handleNewLocation(location);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(@NonNull String provider) {}
    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    // --- JavaScript Interface ---
    public class LocationJSInterface {
        @JavascriptInterface
        public void getCurrentPosition(String callbackId) { // Now receives a callback ID
            new Handler(Looper.getMainLooper()).post(() -> getLocation(callbackId));
        }
    }

    // --- Plugin Interface Methods ---
    @Override
    public void onPageFinished(String url) {
        String js =
                "if (!window.SWVLocation) {" + // Renamed to SWVLocation
                        "  window.SWVLocation = {" +
                        "    _callbacks: {}," +
                        "    getCurrentPosition: function(callback) {" +
                        "      var callbackId = 'loc_cb_' + Date.now() + Math.random();" +
                        "      this._callbacks[callbackId] = callback;" +
                        "      if (window.LocationInterface) {" +
                        "        window.LocationInterface.getCurrentPosition(callbackId);" +
                        "      }" +
                        "    }," +
                        "    _handleCallback: function(callbackId, lat, lng, error) {" +
                        "      if (this._callbacks[callbackId]) {" +
                        "        this._callbacks[callbackId](lat, lng, error);" +
                        "        delete this._callbacks[callbackId];" +
                        "      }" +
                        "    }" +
                        "  };" +
                        "  console.log('SWVLocation JS interface ready.');" +
                        "}";
        evaluateJavascript(js);
    }

    @SuppressLint("DefaultLocale")
    private void sendLocationToJs(String callbackId, Location location, String errorMessage) {
        if (callbackId == null) return;

        String script;
        if (location != null) {
            script = String.format("javascript:window.SWVLocation._handleCallback('%s', %f, %f, null);",
                    callbackId, location.getLatitude(), location.getLongitude());
        } else {
            script = String.format("javascript:window.SWVLocation._handleCallback('%s', null, null, '%s');",
                    callbackId, errorMessage);
        }
        evaluateJavascript(script);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionManager.INITIAL_REQUEST_CODE) {
            boolean locationGranted = false;
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    locationGranted = true;
                    break;
                }
            }

            if (locationGranted) {
                // If a JS request was waiting for permission, fulfill it now
                if (pendingJsCallback != null) {
                    getLocation(pendingJsCallback);
                } else {
                    getLocation(null); // Fetch for cookies if no JS request was pending
                }
            } else if (pendingJsCallback != null) {
                sendLocationError("Location permission denied.");
            }
        }
    }

    @Override public String getPluginName() { return "LocationPlugin"; }
    @Override public void onActivityResult(int r, int c, Intent d) {}
    @Override public boolean shouldOverrideUrlLoading(WebView v, String u) { return false; }
    @Override public void onResume() {}
    @Override public void onPause() {}
    @Override public void onPageStarted(String url) {}
    @Override public void onDestroy() { stopListening(); }
    @Override public void evaluateJavascript(String script) {
        if (webView != null) webView.evaluateJavascript(script, null);
    }
}