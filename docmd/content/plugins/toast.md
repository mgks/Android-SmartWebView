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

## Setup & Initialization

The `ToastPlugin.java` class uses a static initializer block to automatically register itself with the `PluginManager`.

During initialization, the plugin:
1.  Stores the `Activity` and `WebView` context.
2.  Reads its configuration (e.g., `defaultDuration`).
3.  Adds a JavaScript interface named `ToastInterface` to the WebView.
4.  After a page loads, it injects a helper JavaScript object, `window.Toast`, which makes calling the native code more convenient.

---

## Usage

### From Native Code

1.  Get the plugin instance from the `PluginManager`.
2.  Call its `showToast` method.

```java
// Example from another class, like Playground.java
Object plugin = SmartWebView.getPluginManager().getPluginInstance("ToastPlugin");
if (plugin instanceof mgks.os.swv.plugins.ToastPlugin toastPlugin) {
    // Show a toast with the default duration
    toastPlugin.showToast("Hello from Native!");

    // Show a toast with a specific duration
    toastPlugin.showToast("This is a long toast", Toast.LENGTH_LONG);
}
```

### From JavaScript

After the page has loaded, you can call the methods of the injected `window.Toast` helper object. This object provides a convenient wrapper around the actual native JavaScript interface, which is named `ToastInterface`.

```javascript
// Check if the interface is ready
if (window.Toast) {
  // Show a toast with the default duration
  window.Toast.show("Hello from JavaScript!");

  // Show a toast with a long duration
  window.Toast.showLong("This JavaScript toast stays for longer.");
}
```

This plugin serves as a great starting point for understanding how to create your own custom plugins.