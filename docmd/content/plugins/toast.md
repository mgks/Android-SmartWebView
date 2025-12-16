---
title: 'Toast Plugin'
description: 'Displaying native Toast messages from native code or JavaScript.'
icon: 'bread-slice'
---

The `ToastPlugin` is included as a basic example of how the plugin architecture works. It provides a simple way to display native "Toast" messages (short, non-blocking pop-ups).

---

## Features

*   Display toasts from native Java code.
*   Display toasts triggered from JavaScript in the WebView.
*   Configurable default duration (short or long).

---

## Setup & Configuration

1.  **Enable Plugin:** Ensure `ToastPlugin` is listed in the `plugins.enabled` property in `app/src/main/assets/swv.properties`.
    ```bash
    plugins.enabled=...,ToastPlugin
    ```

2.  **Internal Logic:** The `ToastPlugin.java` class uses a static initializer block to automatically register itself. During initialization, it adds a JavaScript interface named `ToastInterface` to the WebView.

---

## Usage

### From Native Code

1.  Get the plugin instance from the `PluginManager`.
2.  Call its `showToast` method.

```java
// Example from another class, like Playground.java
PluginInterface plugin = SWVContext.getPluginManager().getPluginInstance("ToastPlugin");
if (plugin instanceof mgks.os.swv.plugins.ToastPlugin) {
    ((mgks.os.swv.plugins.ToastPlugin) plugin).showToast("Hello from Native!");
}
```

### From JavaScript

After the page has loaded, you can call the methods of the injected `window.Toast` helper object.

```javascript
// Check if the interface is ready
if (window.Toast) {
  // Show a toast with the default duration
  window.Toast.show("Hello from JavaScript!");

  // Show a toast with a long duration
  window.Toast.showLong("This JavaScript toast stays for longer.");
}
```