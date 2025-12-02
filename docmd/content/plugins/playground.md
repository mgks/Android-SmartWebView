---
title: 'Playground'
description: 'Configuring and testing plugins using Playground.java.'
icon: 'flask'
---

The `Playground.java` class is a dedicated component designed to facilitate plugin configuration, development, and testing. It acts as a sandbox where you can set plugin-specific options and experiment with features without modifying the core plugin source code.

---

## Purpose

*   **Plugin Configuration:** The primary place to set runtime options for plugins (e.g., providing AdMob ad unit IDs, enabling biometric authentication on launch).
*   **Centralized Testing:** Provides a single place to run diagnostic checks and add UI elements to the web page for manually testing plugin functionality.
*   **Initialization Hook:** Ensures that plugin configurations and tests only run *after* the core plugin system is fully initialized and ready.
*   **Fail-Safe Diagnostics:** Contains a robust system (`runPluginDiagnostic`) to test plugins without crashing the app if a plugin is missing or fails.
*   **Example Implementation:** Serves as a clear example of how to get a plugin instance from the `PluginManager` and interact with it.

---

## How It Works

The `Playground` is initialized in `MainActivity`. The `PluginManager` calls its `onPageFinished` method after a page loads, which triggers two main actions if the playground is enabled in `swv.properties`:

1.  **`configurePlugins()`:** This method applies configurations to any enabled plugins. For example, it sets the ad unit IDs for the `AdMobPlugin`.
2.  **`runAllDiagnostics()` and `setupPluginDemoUI()`:** These methods inject a floating panel with buttons into the web page, allowing you to manually trigger and test each plugin's features.

---

## Configuring a Plugin

To configure a plugin, modify the `configurePlugins` method in `Playground.java`.

```java
// Inside configurePlugins() in Playground.java

// BiometricPlugin Configuration
runPluginAction("BiometricPlugin", plugin -> {
    Map<String, Object> config = SWVContext.getPluginManager().getPluginConfig("BiometricPlugin");
    if (config != null) {
        // Set to true to require auth every time the app starts
        config.put("authOnAppLaunch", true); 
    }
});

// AdMobPlugin Configuration
runPluginAction("AdMobPlugin", plugin -> {
    Map<String, Object> config = SWVContext.getPluginManager().getPluginConfig("AdMobPlugin");
    if (config != null) {
        // Replace with your real AdMob unit IDs for production
        config.put("bannerAdUnitId", "ca-app-pub-3940256099942544/6300978111");
        config.put("interstitialAdUnitId", "ca-app-pub-3940256099942544/1033173712");
    }
});
```
By using the `Playground`, you can effectively develop, configure, and debug your plugins in an isolated and controlled manner.