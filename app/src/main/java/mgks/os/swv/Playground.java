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

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Consumer;

import mgks.os.swv.plugins.AdMobPlugin;
import mgks.os.swv.plugins.BiometricPlugin;
import mgks.os.swv.plugins.ImageCompressionPlugin;
import mgks.os.swv.plugins.JSInterfacePlugin;
import mgks.os.swv.plugins.QRScannerPlugin;
import mgks.os.swv.plugins.ToastPlugin;

/**
 * Playground is the central hub for configuring and managing plugins in Smart WebView.
 * Developers can use this class to:
 * 1. Configure plugin settings without modifying plugin source code
 * 2. Test plugin functionality during development
 * 3. Manage plugin lifecycle
 *
 * This class is part of the open-source Smart WebView project, while individual plugins
 * may be premium components requiring a license.
 */
public class Playground {

    private final Activity activity;
    private final WebView webView;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final String TAG = "Playground";

    public Playground(Activity activity, WebView webView, Functions functions) {
        this.activity = activity;
        this.webView = webView;
        SmartWebView.onPluginsInitialized(this::onPluginsReady);
    }

    /**
     * This method is called once all plugins have been registered and initialized.
     */
    private void onPluginsReady() {
        // Run diagnostics to check if plugins are loaded and functional.
        runAllDiagnostics();

        // Add demo UI for testing plugins after a short delay to allow the page to load.
        mainHandler.postDelayed(this::setupPluginDemoUI, 3000);
    }

    /**
     * Set up UI elements for demonstrating plugins by injecting JavaScript.
     */
    private void setupPluginDemoUI() {
        // Inject JavaScript to demonstrate plugin functionality
        String demoJs =
                "// Create demo UI in web pages\n" +
                        "function createDemoUI() {\n" +
                        "  if (document.getElementById('plugin-demo-ui')) return;\n" +
                        "  \n" +
                        "  const container = document.createElement('div');\n" +
                        "  container.id = 'plugin-demo-ui';\n" +
                        "  container.style.position = 'fixed';\n" +
                        "  container.style.bottom = '60px';\n" + // Position above banner ad
                        "  container.style.left = '10px';\n" +
                        "  container.style.right = '10px';\n" +
                        "  container.style.padding = '10px';\n" +
                        "  container.style.backgroundColor = 'rgba(0,0,0,0.7)';\n" +
                        "  container.style.borderRadius = '8px';\n" +
                        "  container.style.zIndex = '10000';\n" +
                        "  container.style.color = 'white';\n" +
                        "  container.style.fontFamily = 'monospace';\n" +
                        "  container.innerHTML = '<b>Plugin Test Playground</b><br><br>';\n" +
                        "  \n" +
                        "  // Add buttons\n" +
                        "  const buttons = [\n" +
                        "    { text: 'Show Toast', action: \"window.Toast && window.Toast.show('Hello from the web!')\" },\n" +
                        "    { text: 'Get Device Info', action: \"alert(window.JSBridge ? window.JSBridge.getDeviceInfo() : 'JSBridge not found')\" },\n" +
                        "    { text: 'Scan QR Code', action: \"if(window.QRScanner){window.QRScanner.onScanSuccess=function(c){alert('Scanned: '+c)};window.QRScanner.scan();}\" },\n" +
                        "    { text: 'Biometric Auth', action: \"if(window.Biometric){window.Biometric.onAuthSuccess=function(){alert('Auth OK!')};window.Biometric.authenticate();}\" },\n" +
                        "    { text: 'Show Banner Ad', action: \"window.AdMob && window.AdMob.showBanner()\" },\n" +
                        "    { text: 'Hide Banner Ad', action: \"window.AdMob && window.AdMob.hideBanner()\" },\n" +
                        "    { text: 'Show Interstitial Ad', action: \"window.AdMob && window.AdMob.showInterstitial()\" },\n" +
                        "    { text: 'Show Rewarded Ad', action: \"window.AdMob && window.AdMob.showRewarded()\" }\n" +
                        "  ];\n" +
                        "  \n" +
                        "  buttons.forEach(btn => {\n" +
                        "    const button = document.createElement('button');\n" +
                        "    button.innerText = btn.text;\n" +
                        "    button.style.display = 'block';\n" +
                        "    button.style.width = '100%';\n" +
                        "    button.style.padding = '10px';\n" +
                        "    button.style.margin = '5px 0';\n" +
                        "    button.style.backgroundColor = '#4285f4';\n" +
                        "    button.style.color = 'white';\n" +
                        "    button.style.border = 'none';\n" +
                        "    button.style.borderRadius = '4px';\n" +
                        "    button.onclick = function() { eval(btn.action); };\n" +
                        "    container.appendChild(button);\n" +
                        "  });\n" +
                        "  \n" +
                        "  document.body.appendChild(container);\n" +
                        "}\n" +
                        "\n" +
                        "// Wait for page to be fully loaded before creating UI\n" +
                        "if (document.readyState === 'complete') {\n" +
                        "  createDemoUI();\n" +
                        "} else {\n" +
                        "  window.addEventListener('load', createDemoUI);\n" +
                        "}\n";

        if (webView != null) {
            webView.evaluateJavascript(demoJs, null);
        }
    }

    /**
     * A generic, reusable method to find a plugin and execute a test function if it exists.
     * This is the core of the new, improved fail-safe system.
     *
     * @param pluginName The name of the plugin (e.g., "ToastPlugin").
     * @param pluginClass The class of the plugin (e.g., ToastPlugin.class).
     * @param testFunction A lambda expression containing the test logic to run if the plugin is found.
     * @param <T> The type of the plugin.
     */
    private <T extends PluginInterface> void runPluginDiagnostic(String pluginName, Class<T> pluginClass, Consumer<T> testFunction) {
        try {
            PluginInterface pluginInstance = SmartWebView.getPluginManager().getPluginInstance(pluginName);

            if (pluginInstance != null && pluginClass.isInstance(pluginInstance)) {
                // Plugin exists and is of the correct type, run the test.
                testFunction.accept(pluginClass.cast(pluginInstance));
                Log.i(TAG, "SUCCESS: " + pluginName + " is available and functional.");
            } else {
                // This is the fail-safe "not found" path.
                Log.w(TAG, "INFO: " + pluginName + " not found or is of the wrong type. Skipping test.");
            }
        } catch (Exception e) {
            // This is the final safety net to prevent any test from crashing the app.
            Log.e(TAG, "ERROR: Diagnostic for " + pluginName + " failed with an exception.", e);
        }
    }

    /**
     * Runs all diagnostic tests for the plugins.
     * To add a test for a new plugin, simply add another call to runPluginDiagnostic here.
     */
    private void runAllDiagnostics() {
        mainHandler.post(() -> {
            Log.d(TAG, "--- Running All Plugin Diagnostics ---");

            // Test for ToastPlugin
            runPluginDiagnostic("ToastPlugin", ToastPlugin.class, plugin -> {
                plugin.showToast("ToastPlugin is Active!");
            });

            // Test for JSInterfacePlugin
            runPluginDiagnostic("JSInterfacePlugin", JSInterfacePlugin.class, plugin -> {
                try {
                    JSONObject testData = new JSONObject();
                    testData.put("status", "ready");
                    plugin.triggerJsCallback("systemReady", testData);
                } catch (JSONException e) {
                    throw new RuntimeException("Failed to create test JSON for JSInterfacePlugin", e);
                }
            });

            // Test for AdMobPlugin
            runPluginDiagnostic("AdMobPlugin", AdMobPlugin.class, plugin -> {
                LinearLayout adContainer = activity.findViewById(R.id.msw_ad_container);
                if (adContainer != null) {
                    adContainer.setVisibility(View.VISIBLE);
                }
            });

            // Test for QRScannerPlugin
            runPluginDiagnostic("QRScannerPlugin", QRScannerPlugin.class, plugin -> {
                // The plugin initializes its own launcher, so just logging its presence is enough.
                Log.d(TAG, "QRScannerPlugin diagnostic check passed (presence confirmed).");
            });

            // Test for BiometricPlugin
            runPluginDiagnostic("BiometricPlugin", BiometricPlugin.class, plugin -> {
                Log.d(TAG, "BiometricPlugin diagnostic check passed (presence confirmed).");
            });

            // Test for ImageCompressionPlugin
            runPluginDiagnostic("ImageCompressionPlugin", ImageCompressionPlugin.class, plugin -> {
                Log.d(TAG, "ImageCompressionPlugin diagnostic check passed (presence confirmed).");
            });

            Log.d(TAG, "--- Plugin Diagnostics Complete ---");
        });
    }
}