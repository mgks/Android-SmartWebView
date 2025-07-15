package mgks.os.swv;

/*
  Smart WebView v7
  https://github.com/mgks/Android-SmartWebView

  A modern, open-source WebView wrapper for building advanced hybrid Android apps.
  Native features, modular plugins, and full customisation—built for developers.

  - Documentation: https://docs.mgks.dev/smart-webview
  - Plugins: https://docs.mgks.dev/smart-webview/plugins
  - Discussions: https://github.com/mgks/Android-SmartWebView/discussions
  - Sponsor the Project: https://github.com/sponsors/mgks

  MIT License — https://opensource.org/licenses/MIT

  Mentioning Smart WebView in your project helps others find it and keeps the dev loop alive.
*/

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class GPSTrack extends Service implements LocationListener {

    private final Context mContext;

    boolean isGPSEnabled = false; // flag for GPS status
    boolean isNetworkEnabled = false; // flag for network status
    boolean canGetLocation = false; // flag for GPS status

    Location location; // location
    public static double latitude; // latitude
    public static double longitude; // longitude

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // minimum distance to change updates in meters

    private static final long MIN_TIME_BW_UPDATES = 1000 * 5; // minimum time between updates in milliseconds

    protected LocationManager locationManager; // declaring location manager

    public GPSTrack(Context context) {
        this.mContext = context;
        getLocation();
    }

    @SuppressLint("MissingPermission")
	public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.w("Location GPS:","DEAD");
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("LOC-TP", "GPS");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }else{
                                if (isNetworkEnabled) {
                                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                                    Log.d("LOC-TP", "Network");
                                    if (locationManager != null) {
                                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                        if (location != null) {
                                            latitude = location.getLatitude();
                                            longitude = location.getLongitude();
                                        }
                                    }
                                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        return location ;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e("GPS LOG","GPS ERROR",e);
        }
        return location;
    }

	// to stop GPS usage
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(GPSTrack.this);
        }
    }

	// to get latitude
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

	// to get longitude
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

	// checking if GPS/WIFI is enabled
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

	// showing settings alert dialog
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS is disabled");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
		// opening location settings on device
        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			mContext.startActivity(intent);
		});

        // cancel dialog button
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // showing alert dialog
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location loc){
		//MainActivity mn = new MainActivity();
		//mn.updateL(false);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
		return null;
    }
}
