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

        // ... Implement other interface methods (onActivityResult, etc.) ...
        
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

        // Register the plugin with the manager
        PluginManager.registerPlugin(new MyCustomPlugin(), defaultConfig);
    }
}
```

## 3. Implement Plugin Logic

Fill in the methods from the `PluginInterface` to add your native functionality. You can start activities, request permissions, handle URL loading, and communicate with the WebView.

## 4. Communicate with JavaScript

You have two primary ways to trigger native code from your web content:

*   **Custom URL Schemes:** Your web page navigates to `myplugin://action`. Intercept this in `shouldOverrideUrlLoading`.
*   **JavaScript Interface:** Add a class annotated with `@JavascriptInterface` and attach it to the WebView in your `initialize` method. This allows calls like `window.MyPlugin.performAction()`.

## 5. Enable and Test

1.  **Enable the Plugin:** Open `app/src/main/assets/swv.properties`. Add your plugin's name (from `getPluginName()`) to the `plugins.enabled` list.
    ```bash
    # swv.properties
    plugins.enabled=...,MyCustomPlugin
    ```
2.  **Test with Playground:** Use the `Playground.java` class to test your plugin in a sandboxed environment. You can run diagnostic checks and add buttons to the demo UI to trigger your plugin's features.

::: card
[Read more about the Playground](/Android-SmartWebView/documentation/plugins/playground)
:::