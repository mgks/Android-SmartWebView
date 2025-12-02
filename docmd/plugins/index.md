---
title: 'Plugin Architecture'
description: 'Understanding the Smart WebView plugin system.'
icon: 'puzzle'
---

Smart WebView features a powerful plugin architecture, allowing you to extend native functionalities without altering the core project code.

::: callout tip
All Premium Plugins are now available for free and open source to developers. Consider becoming **[Project Sponsor](https://github.com/sponsors/mgks/sponsorships?sponsor=mgks&tier_id=468838)**.
:::

## Core Concepts

*   **Self-Contained:** Plugins are designed as independent, modular classes.
*   **Self-Registration:** Plugins register themselves with the `PluginManager` when their class is first loaded by the application.
*   **Standardized Interface:** All plugins implement the `PluginInterface`, which defines essential lifecycle methods (`initialize`, `onDestroy`, `onActivityResult`, etc.).
*   **Central Management:** The `PluginManager` class handles plugin registration, initialization, and routing of lifecycle events.
*   **Configuration:** Plugin behavior can be configured externally through the `Playground` class or disabled entirely in `SmartWebView.java`, preventing the need to modify the plugin's source code.

## Benefits

*   **Modularity:** Keeps custom features separate and organized.
*   **Extensibility:** Easily add new native capabilities.
*   **Simplified Updates:** Core project updates are easier when custom code is isolated.

## Key Components

*   **`PluginInterface.java`:** The contract that all plugins must implement. It defines the methods the `PluginManager` will call.
*   **`PluginManager.java`:** The central hub for managing all registered plugins. It's a singleton accessed via `SmartWebView.getPluginManager()`.
*   **`Playground.java`:** A dedicated class for configuring and testing plugins during development. See the [Playground](/smart-webview/plugins/playground) documentation.
*   **`plugins/` directory:** The conventional location for plugin source files.
*   **Static Initializer Block:** The common pattern used for self-registration within a plugin class.
    ```java
    // Inside YourPlugin.java
    static {
        // Provide a default configuration for the plugin
        Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put("some_key", "default_value");

        // Register an instance of the plugin with the PluginManager
        PluginManager.registerPlugin(new YourPlugin(), defaultConfig);
    }
    ```

Ready to build your own? Check out the [Creating Plugins](/smart-webview/plugins/creating-plugins) guide.