package mgks.os.swv.plugins;

/*
  Network Info Plugin for Smart WebView

  Exposes the device's current network state to the WebView, so web content
  (especially the offline/error pages) can adapt its UI accordingly.

  FEATURES:
  - Reports whether the device is online (wifi / cellular / ethernet / vpn).
  - Reports the connection type as a string ("wifi", "cellular", etc.).
  - Fires a JS callback whenever connectivity changes while the app is running.

  USAGE:
  1. Enable "NetworkInfoPlugin" in swv.properties -> plugins.enabled.
  2. From JavaScript:
       window.NetworkInfo.getStatus(function(state) { console.log(state); });
       window.NetworkInfo.onChange(function(state) { /* live updates *\/ });
*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import mgks.os.swv.Functions;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;

public class NetworkInfoPlugin implements PluginInterface {
    private static final String TAG = "NetworkInfoPlugin";
    private Activity activity;
    private WebView webView;
    private ConnectivityManager.NetworkCallback networkCallback;
    private final AtomicBoolean callbackRegistered = new AtomicBoolean(false);

    static {
        PluginManager.registerPlugin(new NetworkInfoPlugin(), new HashMap<>());
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView;
        webView.addJavascriptInterface(new NetworkInfoJSInterface(), "NetworkInfoInterface");
        registerCallback();
        Log.d(TAG, "NetworkInfoPlugin initialized.");
    }

    /** Returns "online" / "offline" plus type "wifi" / "cellular" / "ethernet" / "vpn" / "none". */
    public String currentStatusJson() {
        if (activity == null) return "{\"online\":false,\"type\":\"none\"}";
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return "{\"online\":false,\"type\":\"none\"}";
        Network network = cm.getActiveNetwork();
        if (network == null) return "{\"online\":false,\"type\":\"none\"}";
        NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        if (caps == null) return "{\"online\":false,\"type\":\"none\"}";
        boolean online = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                || caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        String type = "none";
        if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) type = "wifi";
        else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) type = "cellular";
        else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) type = "ethernet";
        else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) type = "vpn";
        return "{\"online\":" + online + ",\"type\":\"" + type + "\"}";
    }

    private void registerCallback() {
        if (callbackRegistered.get() || activity == null) return;
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;
        NetworkRequest req = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                pushUpdate();
            }
            @Override
            public void onLost(@NonNull Network network) {
                pushUpdate();
            }
        };
        try {
            cm.registerNetworkCallback(req, networkCallback);
            callbackRegistered.set(true);
        } catch (Exception e) {
            Log.w(TAG, "Failed to register network callback", e);
        }
    }

    private void pushUpdate() {
        String json = currentStatusJson().replace("'", "\\'");
        String script = "if(window.NetworkInfo && window.NetworkInfo._handleChange) window.NetworkInfo._handleChange('" + json + "');";
        evaluateJavascript(script);
    }

    @Override
    public void onPageFinished(String url) {
        String json = currentStatusJson().replace("'", "\\'");
        String js =
                "if(!window.NetworkInfo){" +
                        "  window.NetworkInfo = {" +
                        "    getStatus: function(cb) {" +
                        "      window.NetworkInfo._cb = cb;" +
                        "      if (window.NetworkInfoInterface) window.NetworkInfoInterface.getStatus();" +
                        "    }," +
                        "    onChange: function(cb) { window.NetworkInfo._onChange = cb; }," +
                        "    _handleStatus: function(json) {" +
                        "      if (window.NetworkInfo._cb) { window.NetworkInfo._cb(JSON.parse(json)); }" +
                        "    }," +
                        "    _handleChange: function(json) {" +
                        "      if (window.NetworkInfo._onChange) { window.NetworkInfo._onChange(JSON.parse(json)); }" +
                        "    }" +
                        "  };" +
                        "  console.log('NetworkInfo JS interface ready.');" +
                        "}" +
                        // Auto-push current status into the page.
                        "if (window.NetworkInfo && window.NetworkInfo._handleChange) window.NetworkInfo._handleChange('" + json + "');";
        evaluateJavascript(js);
    }

    public class NetworkInfoJSInterface {
        @JavascriptInterface
        public void getStatus() {
            String json = currentStatusJson().replace("'", "\\'");
            String script = "if(window.NetworkInfo && window.NetworkInfo._handleStatus) window.NetworkInfo._handleStatus('" + json + "');";
            evaluateJavascript(script);
        }
    }

    @Override public String getPluginName() { return "NetworkInfoPlugin"; }
    @Override public void onActivityResult(int r, int c, Intent d) {}
    @Override public void onRequestPermissionsResult(int r, @NonNull String[] p, @NonNull int[] g) {}
    @Override public boolean shouldOverrideUrlLoading(WebView v, String u) { return false; }
    @Override public void onPageStarted(String url) {}
    @Override public void onResume() {}
    @Override public void onPause() {}
    @Override public void onDestroy() {
        if (callbackRegistered.get() && activity != null && networkCallback != null) {
            try {
                ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) cm.unregisterNetworkCallback(networkCallback);
            } catch (Exception ignored) {}
        }
    }
    @Override public void evaluateJavascript(String script) {
        if (webView != null) webView.evaluateJavascript(script, null);
    }
}