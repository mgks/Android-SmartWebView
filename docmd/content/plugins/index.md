---
title: 'Plugin Architecture'
description: 'Understanding the Smart WebView plugin system.'
icon: 'puzzle'
---

Smart WebView features a powerful plugin architecture, allowing you to extend native functionalities without altering the core project code.

## Core Concepts

*   **Self-Contained:** Plugins are designed as independent, modular classes.
*   **Self-Registration:** Plugins register themselves with the `PluginManager` when their class is first loaded.
*   **Standardized Interface:** All plugins implement the `PluginInterface`, which defines essential lifecycle methods (`initialize`, `onDestroy`, `onActivityResult`, etc.).
*   **Central Management:** The `PluginManager` class handles plugin registration, initialization, and routing of lifecycle events.
*   **Configuration:** Plugin activation is controlled via `swv.properties`. This prevents the need to modify Java code just to enable or disable a standard feature.

## Benefits

*   **Modularity:** Keeps custom features separate and organized.
*   **Extensibility:** Easily add new native capabilities.
*   **Simplified Updates:** Core project updates are easier when custom code is isolated.

## Key Components

*   **`PluginInterface.java`:** The contract that all plugins must implement.
*   **`PluginManager.java`:** The central hub for managing all registered plugins. Accessed via `SWVContext.getPluginManager()`.
*   **`swv.properties`:** The single configuration file where you define which plugins are enabled (`plugins.enabled`) and set their configurable properties.
*   **`Playground.java`:** A dedicated class for advanced plugin configuration (like API keys) and testing during development.
*   **`plugins/` directory:** The conventional location for plugin source files.

Ready to build your own? Check out the [Creating Plugins](/Android-SmartWebView/documentation/plugins/creating-plugins) guide.