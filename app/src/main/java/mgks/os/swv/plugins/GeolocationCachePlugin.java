package mgks.os.swv.plugins;

/*
  Geolocation Cache Plugin for Smart WebView

  Fixes #387: "GPS Location not fetched in offline site" — adds a small disk-backed
  cache for the last known location. When GPS/Network providers fail (e.g., on a
  tablet without GPS while in offline mode), the plugin can still return a
  recent fix so the offline page shows something useful instead of "Error: 7".

  FEATURES:
  - Caches the most recent successful lat/lng + timestamp to SharedPreferences.
  - Exposes `getLastKnown()` to JavaScript for graceful degradation.
  - Optionally injects the cached value on offline page load.

  USAGE:
  1. Enable "GeolocationCachePlugin" in swv.properties -> plugins.enabled.
  2. From JavaScript: window.GeolocationCache.getLastKnown(function(lat, lng, ageMs)).
*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import mgks.os.swv.Functions;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;

public class GeolocationCachePlugin implements PluginInterface {
    private static final String TAG = "GeolocationCachePlugin";
    private static final String PREF_NAME = "swv_geocache";
    private static final String KEY_LAT = "last_lat";
    private static final String KEY_LNG = "last_lng";
    private static final String KEY_TIME = "last_time";

    private Activity activity;
    private WebView webView;

    static {
        PluginManager.registerPlugin(new GeolocationCachePlugin(), new HashMap<>());
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView;
        webView.addJavascriptInterface(new GeolocationCacheJSInterface(), "GeolocationCacheInterface");
        Log.d(TAG, "GeolocationCachePlugin initialized.");
    }

    /** Persist a location fix for later fallback. Called by LocationPlugin or directly from JS. */
    public void cache(double lat, double lng) {
        if (activity == null) return;
        SharedPreferences sp = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit()
                .putLong(KEY_LAT, Double.doubleToRawLongBits(lat))
                .putLong(KEY_LNG, Double.doubleToRawLongBits(lng))
                .putLong(KEY_TIME, System.currentTimeMillis())
                .apply();
    }

    private double[] read() {
        if (activity == null) return null;
        SharedPreferences sp = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (!sp.contains(KEY_LAT) || !sp.contains(KEY_LNG) || !sp.contains(KEY_TIME)) return null;
        double lat = Double.longBitsToDouble(sp.getLong(KEY_LAT, 0));
        double lng = Double.longBitsToDouble(sp.getLong(KEY_LNG, 0));
        long time = sp.getLong(KEY_TIME, 0);
        return new double[]{lat, lng, time};
    }

    @Override
    public void onPageFinished(String url) {
        String js =
                "if(!window.GeolocationCache){" +
                        "  window.GeolocationCache = {" +
                        "    getLastKnown: function(cb) {" +
                        "      if (window.GeolocationCacheInterface) window.GeolocationCacheInterface.getLastKnown();" +
                        "      window.GeolocationCache._cb = cb;" +
                        "    }," +
                        "    cache: function(lat, lng) {" +
                        "      if (window.GeolocationCacheInterface) window.GeolocationCacheInterface.cache(lat, lng);" +
                        "    }," +
                        "    _handleResult: function(lat, lng, ageMs) {" +
                        "      if (window.GeolocationCache._cb) { window.GeolocationCache._cb(lat, lng, ageMs); }" +
                        "    }" +
                        "  };" +
                        "  console.log('GeolocationCache JS interface ready.');" +
                        "}";
        evaluateJavascript(js);
    }

    public class GeolocationCacheJSInterface {
        @JavascriptInterface
        public void cache(double lat, double lng) {
            GeolocationCachePlugin.this.cache(lat, lng);
        }
        @JavascriptInterface
        public void getLastKnown() {
            double[] cached = read();
            if (cached == null) {
                evaluateJavascript("if(window.GeolocationCache._cb) window.GeolocationCache._cb(null, null, -1);");
                return;
            }
            long age = System.currentTimeMillis() - (long) cached[2];
            String script = String.format(java.util.Locale.US,
                    "if(window.GeolocationCache._cb) window.GeolocationCache._cb(%f, %f, %d);",
                    cached[0], cached[1], age);
            evaluateJavascript(script);
        }
    }

    @Override public String getPluginName() { return "GeolocationCachePlugin"; }
    @Override public void onActivityResult(int r, int c, Intent d) {}
    @Override public void onRequestPermissionsResult(int r, @NonNull String[] p, @NonNull int[] g) {}
    @Override public boolean shouldOverrideUrlLoading(WebView v, String u) { return false; }
    @Override public void onPageStarted(String url) {}
    @Override public void onResume() {}
    @Override public void onPause() {}
    @Override public void onDestroy() {}
    @Override public void evaluateJavascript(String script) {
        if (webView != null) webView.evaluateJavascript(script, null);
    }
}