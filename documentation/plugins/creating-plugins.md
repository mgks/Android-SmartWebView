---
title: 'Creating Plugins'
description: 'A guide to building your own custom plugins for Smart WebView.'
icon: 'plug'
---

The Smart WebView plugin architecture allows you to extend the application's native capabilities. Follow these steps to create your own plugin.

## 1. Create the Plugin Class

1.  Create a new Java class inside the `app/src/main/java/mgks/os/swv/plugins/` directory.
2.  Make your class implement the `PluginInterface`.

    ```java
    package mgks.os.swv.plugins;

    import android.app.Activity;
    import android.content.Intent;
    import android.webkit.WebView;
    import androidx.annotation.NonNull;
    import java.util.Map;

    import mgks.os.swv.Functions;
    import mgks.os.swv.PluginInterface;

    public class MyCustomPlugin implements PluginInterface {

        private Activity activity;
        private WebView webView;

        @Override
        public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
            this.activity = activity;
            this.webView = webView;
            // Initialization logic here...
        }

        @Override
        public String getPluginName() {
            return "MyCustomPlugin"; // Must be a unique name
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            // Handle results from activities started by this plugin
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            // Handle results from permission requests
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Example: Handle a custom URL scheme
            if (url.startsWith("myplugin://dosomething")) {
                // Perform a native action
                return true; // We've handled the URL
            }
            return false; // Let other plugins or the default handler process it
        }

        @Override
        public void onPageStarted(String url) { }

        @Override
        public void onPageFinished(String url) {
            // Logic to run after a page loads, like injecting JS
        }

        @Override
        public void onDestroy() {
            // Clean up resources
        }

        @Override
        public void evaluateJavascript(String script) {
            if (webView != null) {
                webView.evaluateJavascript(script, null);
            }
        }
    }
    ```

## 2. Implement Self-Registration

Add a `static` initializer block to your plugin class. This automatically registers an instance of your plugin with the `PluginManager` when the app starts.

```java
public class MyCustomPlugin implements PluginInterface {

    // ... (existing methods) ...

    // Static initializer block for self-registration
    static {
        // Provide a default configuration for your plugin
        Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put("apiKey", "DEFAULT_KEY");
        defaultConfig.put("enabled", true);

        // Register the plugin with the manager
        PluginManager.registerPlugin(new MyCustomPlugin(), defaultConfig);
    }

    // ... (rest of the class) ...
}
```

## 3. Implement Plugin Logic

Fill in the methods from the `PluginInterface` to add your native functionality. You can start activities, request permissions, handle URL loading, and communicate with the WebView.

## 4. Communicate with JavaScript

You have two primary ways to trigger native code from your web content:

*   **Custom URL Schemes:** Your web page can navigate to a unique URL like `myplugin://show-dialog?title=Hello`. Your plugin intercepts this in `shouldOverrideUrlLoading`, parses the URL, and performs the native action. This is simple but less flexible.
*   **JavaScript Interface:** Add a dedicated class with methods annotated with `@JavascriptInterface`. Add an instance of this interface to the WebView in your plugin's `initialize` method (e.g., `webView.addJavascriptInterface(new MyJSInterface(), "MyPluginAndroid")`). This allows your JavaScript to directly call native methods (e.g., `window.MyPluginAndroid.performAction("data")`), which is more powerful and flexible.

To send data from native back to JavaScript, use the `evaluateJavascript()` method.

## 5. Enable and Test

1.  **Enable the Plugin:** Ensure your plugin is enabled in the `ASWP_PLUGIN_SETTINGS` map in `SmartWebView.java`.
    ```java
    put("MyCustomPlugin", true);
    ```
2.  **Test with Playground:** Use the `Playground.java` class to test your plugin in a sandboxed environment. You can run diagnostic checks and add buttons to the demo UI to trigger your plugin's features.

::: card
[Read more about the Playground](/smart-webview/plugins/playground)
:::