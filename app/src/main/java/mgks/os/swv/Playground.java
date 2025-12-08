package mgks.os.swv;

/*
  Smart WebView v8
  https://github.com/mgks/Android-SmartWebView

  A modern, open-source WebView wrapper for building advanced hybrid Android apps.
  Native features, modular plugins, and full customisation—built for developers.

  - Documentation: https://mgks.github.io/Android-SmartWebView/documentation
  - Plugins: https://mgks.github.io/Android-SmartWebView/documentation/plugins
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

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Playground is the central hub for configuring and testing plugins in Smart WebView.
 * It is designed to be fail-safe and does not have static dependencies on any specific plugin.
 */
public class Playground {

    private final Activity activity;
    private final WebView webView;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final String TAG = "Playground";

    public Playground(Activity activity, WebView webView, Functions functions) {
        this.activity = activity;
        this.webView = webView;
    }

    private void onPluginsReady() {
        configurePlugins();
        handleLaunchActions();
        if (SWVContext.SWV_DEBUGMODE) {
            runAllDiagnostics();
        }
    }

    /**
     * Central hub for setting all plugin configurations. This method is fail-safe.
     */
    private void configurePlugins() {
        Log.d(TAG, "--- Applying Plugin Configurations ---");

        // BiometricPlugin Configuration
        runPluginAction("BiometricPlugin", plugin -> {
            Map<String, Object> config = SWVContext.getPluginManager().getPluginConfig("BiometricPlugin");
            if (config != null) {
                config.put("authOnAppLaunch", false);
                Log.d(TAG, "BiometricPlugin configured for launch auth. The plugin will handle triggering it.");
            }
        });

        // AdMobPlugin Configuration
        runPluginAction("AdMobPlugin", plugin -> {
            Map<String, Object> config = SWVContext.getPluginManager().getPluginConfig("AdMobPlugin");
            if (config != null) {
                config.put("bannerAdUnitId", "ca-app-pub-3940256099942544/6300978111");
                config.put("interstitialAdUnitId", "ca-app-pub-3940256099942544/1033173712");
                config.put("rewardedAdUnitId", "ca-app-pub-3940256099942544/5224354917");
            }
        });

        // ... (add other plugin configurations here)
    }

    /**
     * Handles actions that should run once on app launch.
     */
    private void handleLaunchActions() {}


    private void runAllDiagnostics() {
        mainHandler.post(() -> {
            Log.d(TAG, "--- Running All Plugin Diagnostics ---");

            // Test for ToastPlugin
            runPluginDiagnostic("ToastPlugin", plugin -> {
                if(SWVContext.SWV_DEBUGMODE) {
                    webView.evaluateJavascript("window.Toast && window.Toast.show('ToastPlugin is Active!')", null);
                }
                Log.i(TAG, "SUCCESS: ToastPlugin is available.");
            });

            // Test for DialogPlugin
            runPluginDiagnostic("DialogPlugin", plugin -> {
                // This is a passive plugin, best tested with the UI.
                Log.i(TAG, "SUCCESS: DialogPlugin is available.");
            });

            // Test for LocationPlugin
            runPluginDiagnostic("LocationPlugin", plugin -> {
                // Trigger a location fetch on launch for debugging purposes.
                // The result will appear in the device's Logcat.
                webView.evaluateJavascript("window.Location && window.Location.getCurrentPosition(function(lat,lng,err){ console.log('Playground Location Test:', {lat:lat, lng:lng, err:err}); })", null);
                Log.i(TAG, "SUCCESS: LocationPlugin is available. Sent test request.");
            });

            // Test for RatingPlugin
            runPluginDiagnostic("RatingPlugin", plugin -> {
                // This is a passive plugin, we just confirm it's loaded.
                Log.i(TAG, "SUCCESS: RatingPlugin is available.");
            });

            // Test for JSInterfacePlugin
            runPluginDiagnostic("JSInterfacePlugin", plugin -> {
                // CORRECTED: Use console.log for a silent, non-disruptive diagnostic test.
                webView.evaluateJavascript("console.log('Device Info: ' + (window.JSBridge ? window.JSBridge.getDeviceInfo() : 'JSBridge not found'))", null);
                Log.i(TAG, "SUCCESS: JSInterfacePlugin is available.");
            });

            // Test for AdMobPlugin
            runPluginDiagnostic("AdMobPlugin", plugin -> {
                LinearLayout adContainer = activity.findViewById(R.id.msw_ad_container);
                if (adContainer != null) {
                    adContainer.setVisibility(View.VISIBLE);
                    Log.i(TAG, "SUCCESS: AdMobPlugin is available, showing ad container.");
                }
            });

            // Generic presence check for other plugins
            runPluginDiagnostic("QRScannerPlugin", plugin -> Log.i(TAG, "SUCCESS: QRScannerPlugin is available."));
            runPluginDiagnostic("BiometricPlugin", plugin -> Log.i(TAG, "SUCCESS: BiometricPlugin is available."));
            runPluginDiagnostic("ImageCompressionPlugin", plugin -> Log.i(TAG, "SUCCESS: ImageCompressionPlugin is available."));

            Log.d(TAG, "--- Plugin Diagnostics Complete ---");
        });
    }

    public void onPageFinished(String url) {
        if (SWVContext.SWV_PLAYGROUND && SWVContext.SWV_DEBUGMODE) {
            mainHandler.postDelayed(() -> {
                Log.d(TAG, "Attempting to inject Playground UI...");
                setupPluginDemoUI();
            }, 500);
        }
    }

    private void setupPluginDemoUI() {
        JSONObject pluginStatus = new JSONObject();
        PluginManager manager = SWVContext.getPluginManager();
        try {
            pluginStatus.put("ToastPlugin", manager.getPluginInstance("ToastPlugin") != null);
            pluginStatus.put("JSInterfacePlugin", manager.getPluginInstance("JSInterfacePlugin") != null);
            pluginStatus.put("QRScannerPlugin", manager.getPluginInstance("QRScannerPlugin") != null);
            pluginStatus.put("BiometricPlugin", manager.getPluginInstance("BiometricPlugin") != null);
            pluginStatus.put("AdMobPlugin", manager.getPluginInstance("AdMobPlugin") != null);
            pluginStatus.put("DialogPlugin", manager.getPluginInstance("DialogPlugin") != null);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating plugin status JSON", e);
        }

        String demoJs =
                "// Create demo UI in web pages\n" +
                "function createDemoUI(pluginStatus) {\n" +
                "  if (document.getElementById('swv-pg-container-999')) return;\n" +
                "  \n" +
                "  const css = `\n" +
                "    #swv-pg-container-999 { all: initial; position: fixed; bottom: 15px; right: 15px; z-index: 2147483647; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }\n" +
                "    #swv-pg-toggle-999 { all: initial; width: 60px; height: 60px; background-color: #4285f4; color: white; border-radius: 50%; border: none; font-size: 28px; line-height: 60px; text-align: center; box-shadow: 0 4px 12px rgba(0,0,0,0.2); cursor: pointer; }\n" +
                "    #swv-pg-panel-999 { all: initial; display: none; position: absolute; bottom: 75px; right: 0; width: 280px; background-color: rgba(20,20,20,0.9); backdrop-filter: blur(8px); -webkit-backdrop-filter: blur(8px); color: white; border-radius: 12px; padding: 15px; box-shadow: 0 4px 12px rgba(0,0,0,0.2); }\n" +
                "    #swv-pg-panel-999.visible { display: block; }\n" +
                "    #swv-pg-panel-999 h4 { all: initial; margin: 5px 0 15px; text-align: center; font-weight: bold; font-size: 16px; color: white; display: block; }\n" +
                "    .swv-pg-btn-999 { all: initial; display: block; width: 94%; padding: 12px 3%; margin: 6px 0; background-color: #555; color: white; border: none; border-radius: 6px; text-align: left; cursor: pointer; font-size: 14px; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }\n" +
                "    .swv-pg-btn-999:disabled { background-color: #444; color: #888; cursor: not-allowed; }\n" +
                "    #swv-pg-panel-999 a { all: initial; text-decoration: none; display: block; }\n" +
                "  `;\n" +
                "  \n" +
                "  const style = document.createElement('style');\n" +
                "  style.id = 'swv-pg-styles-999';\n" +
                "  style.textContent = css;\n" +
                "  document.head.appendChild(style);\n" +
                "  \n" +
                "  const container = document.createElement('div');\n" +
                "  container.id = 'swv-pg-container-999';\n" +
                "  \n" +
                "  const panel = document.createElement('div');\n" +
                "  panel.id = 'swv-pg-panel-999';\n" +
                "  panel.innerHTML = '<h4>Plugin Playground</h4>';\n" +
                "  \n" +
                "  const toggleBtn = document.createElement('button');\n" +
                "  toggleBtn.id = 'swv-pg-toggle-999';\n" +
                "  toggleBtn.innerHTML = '⚙';\n" +
                "  toggleBtn.onclick = () => { panel.classList.toggle('visible'); };\n" +
                "  \n" +
                "  const buttons = [\n" +
                "    { text: 'Show Toast', action: `window.Toast && window.Toast.show('Hello from the web!')`, plugin: 'ToastPlugin' },\n" +
                "    { text: 'Get Device Info', action: `alert(window.JSBridge ? window.JSBridge.getDeviceInfo() : 'JSBridge not found')`, plugin: 'JSInterfacePlugin' },\n" +
                "    { text: 'Show Banner Ad', action: `window.AdMob && window.AdMob.showBanner()`, plugin: 'AdMobPlugin' },\n" +
                "    { text: 'Show Interstitial Ad', action: `window.AdMob && window.AdMob.showInterstitial()`, plugin: 'AdMobPlugin' },\n" +
                "    { text: 'Show Rewarded Ad', action: `window.AdMob && window.AdMob.showRewarded()`, plugin: 'AdMobPlugin' },\n" +
                "    { text: 'Scan QR Code', action: `if(window.QRScanner){window.QRScanner.onScanSuccess=function(c){alert('Scanned: '+c)};window.QRScanner.scan();}`, plugin: 'QRScannerPlugin' },\n" +
                "    { text: 'Biometric Auth', action: `if(window.Biometric){window.Biometric.onAuthSuccess=function(){alert('Auth OK!')};window.Biometric.authenticate();}`, plugin: 'BiometricPlugin' },\n" +
                "    { text: 'Show Native Dialog', action: `window.Dialog && window.Dialog.show({ title: 'Native Dialog', message: 'This was triggered from the web.', positiveText: 'Awesome!' }, (result) => alert('Dialog closed with: ' + result))`, plugin: 'DialogPlugin' }\n" +
                "  ];\n" +
                "  \n" +
                "  buttons.forEach(btn => {\n" +
                "    const button = document.createElement('button');\n" +
                "    button.className = 'swv-pg-btn-999';\n" +
                "    if (pluginStatus[btn.plugin]) {\n" +
                "      button.innerText = btn.text;\n" +
                "      button.onclick = () => { try { eval(btn.action); } catch(e) { alert('Error: ' + e.message); } };\n" +
                "    } else {\n" +
                "      button.innerText = btn.text + ' (Not Enabled)';\n" +
                "      button.disabled = true;\n" +
                "    }\n" +
                "    panel.appendChild(button);\n" +
                "  });\n" +
                "  \n" +
                "  container.appendChild(panel);\n" +
                "  container.appendChild(toggleBtn);\n" +
                "  document.body.appendChild(container);\n" +
                "}\n" +
                "\n" +
                "createDemoUI(" + pluginStatus.toString() + ");\n";

        if (webView != null) {
            webView.evaluateJavascript(demoJs, null);
        }
    }

    /**
     * A generic, fail-safe method to find a plugin and execute a test function if it exists.
     * This version is fully decoupled and does not require a Class parameter.
     *
     * @param pluginName   The name of the plugin (e.g., "ToastPlugin").
     * @param testFunction A lambda expression containing the test logic to run if the plugin is found.
     */
    private void runPluginDiagnostic(String pluginName, Consumer<PluginInterface> testFunction) {
        try {
            PluginInterface pluginInstance = SWVContext.getPluginManager().getPluginInstance(pluginName);

            if (pluginInstance != null) {
                // Plugin exists, run the test.
                testFunction.accept(pluginInstance);
            } else {
                // This is the fail-safe "not found" path.
                Log.w(TAG, "INFO: " + pluginName + " not found. Skipping diagnostic test.");
            }
        } catch (Exception e) {
            // This is the final safety net to prevent any test from crashing the app.
            Log.e(TAG, "ERROR: Diagnostic for " + pluginName + " failed with an exception.", e);
        }
    }

    /**
     * A generic, fail-safe method to find a plugin and execute an action if it exists.
     *
     * @param pluginName The name of the plugin (e.g., "ToastPlugin").
     * @param action     A lambda expression containing the logic to run if the plugin is found.
     */
    private void runPluginAction(String pluginName, Consumer<PluginInterface> action) {
        PluginInterface pluginInstance = SWVContext.getPluginManager().getPluginInstance(pluginName);
        if (pluginInstance != null) {
            action.accept(pluginInstance);
        } else {
            Log.w(TAG, "Skipping action for missing plugin: " + pluginName);
        }
    }
}