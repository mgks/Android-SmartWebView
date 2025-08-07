package mgks.os.swv.plugins;

/*
  Toast Plugin for Smart WebView

  This is a core plugin and is covered by the MIT license of the Smart WebView project.

  This plugin provides a simple way to display toast messages.

  FEATURES:
  - Display toast messages from native code
  - Display toast messages from JavaScript
  - Configurable duration

  USAGE:
  1. Get the plugin instance: ToastPlugin plugin = (ToastPlugin) SmartWebView.getPluginManager().getPluginInstance("ToastPlugin");
  2. Show a toast: plugin.showToast("Hello World!");
  3. From JavaScript: window.ToastInterface.showToast("Hello from JavaScript!");
*/

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import mgks.os.swv.Functions;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;
// Removed R import as it's not directly used for findViewById anymore for the webview.
// If other R references are needed, it should be mgks.os.swv.R

public class ToastPlugin implements PluginInterface {
    private static final String TAG = "ToastPlugin";
    private Activity activity;
    private WebView webView; // Added
    private int defaultDuration = Toast.LENGTH_SHORT;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Static initializer block for self-registration
    static {
        Map<String, Object> config = new HashMap<>();
        config.put("defaultDuration", Toast.LENGTH_SHORT);
        PluginManager.registerPlugin(new ToastPlugin(), config);
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView; // Assigned

        // Get configuration
        if (config.containsKey("defaultDuration")) {
            Object duration = config.get("defaultDuration");
            if (duration instanceof Integer) {
                this.defaultDuration = (Integer) duration;
            }
        }

        // Add JavaScript interface
        // Ensure webView is not null before adding JavascriptInterface, though it should be guaranteed by PluginManager
        if (this.webView != null) {
            this.webView.addJavascriptInterface(new ToastJSInterface(), "ToastInterface");
        } else {
            Log.e(TAG, "WebView is null during ToastPlugin initialization. JS Interface not added.");
        }

        Log.d(TAG, "ToastPlugin initialized with config: " + config);
    }

    @Override
    public String getPluginName() {
        return "ToastPlugin";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        // Not used in this plugin
    }

    @Override
    @NonNull
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Not used in this plugin
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Not used in this plugin
        return false;
    }

    @Override
    public void onPageStarted(String url) {
        // Not used in this plugin
    }

    @Override
    public void onPageFinished(String url) {
        // Inject Toast-related JavaScript
        injectToastSupportJs();
    }

    private void injectToastSupportJs() {
        String toastSupportJs =
                "if (!window.Toast) {\n" +
                        "    window.Toast = {\n" +
                        "        show: function(message) { return window.ToastInterface.showToast(message); },\n" +
                        "        showLong: function(message) { return window.ToastInterface.showLongToast(message); }\n" +
                        "    };\n" +
                        "    console.log('Toast JavaScript interface initialized');\n" +
                        "}\n";

        evaluateJavascript(toastSupportJs);
    }

    @Override public void onResume() {}
    @Override public void onPause() {}

    @Override
    public void onDestroy() {
        this.activity = null;
        this.webView = null; // Nullified
    }

    @Override
    public void evaluateJavascript(String script) {
        // Modified to use the stored webView instance
        if (this.webView != null && this.activity != null && !this.activity.isFinishing()) {
            try {
                this.webView.evaluateJavascript(script, null);
            } catch (Exception e) {
                Log.e(TAG, "Error evaluating JavaScript: " + e.getMessage());
            }
        } else {
            Log.w(TAG, "evaluateJavascript called but webView is null or activity is finishing.");
        }
    }

    /**
     * Shows a toast message with default duration
     *
     * @param message The message to display
     */
    public void showToast(String message) {
        showToast(message, defaultDuration);
    }

    /**
     * Shows a toast message with specified duration
     *
     * @param message The message to display
     * @param duration The duration (Toast.LENGTH_SHORT or Toast.LENGTH_LONG)
     */
    public void showToast(String message, int duration) {
        if (activity != null && !activity.isFinishing()) {
            mainHandler.post(() -> Toast.makeText(activity, message, duration).show());
        }
    }

    /**
     * JavaScript interface for displaying toasts from web content
     */
    public class ToastJSInterface {

        @JavascriptInterface
        public void showToast(String message) {
            ToastPlugin.this.showToast(message);
        }

        @JavascriptInterface
        public void showLongToast(String message) {
            ToastPlugin.this.showToast(message, Toast.LENGTH_LONG);
        }
    }
}