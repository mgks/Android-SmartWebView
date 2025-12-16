---
title: 'Dark Mode & Theming'
description: 'Implementing dynamic light and dark themes based on system settings.'
icon: 'moon'
---

Smart WebView includes a robust system for handling light and dark themes, allowing the app to automatically adapt to the user's device settings.

---

## How It Works

The theming system operates on multiple levels to ensure a seamless experience:

1.  **Native Android Theme:** The app uses different resource files for light (`res/values/`) and dark (`res/values-night/`) modes. Android automatically applies the correct theme when the app starts, based on the device's system setting.
2.  **Initial State Detection:** When the app launches, it detects the current system theme.
3.  **JavaScript Injection:** On page load, the native app injects the initial theme preference into the web page's JavaScript context. This allows your web content to match the native UI.
4.  **CSS Styling:** The web page's `style.css` uses CSS variables and a `.dark-mode` class on the `<body>` tag to switch between light and dark styles.
5.  **Web-to-Native Sync:** A JavaScript interface is provided (`window.AndroidInterface.setNativeTheme()`) that allows your web UI (e.g., a theme toggle button on your website) to change the native Android theme.

::: callout warning
The native theme toggle switch in the navigation drawer has been temporarily disabled in v7.5 to ensure stability. Theming is currently driven by the system setting and can be controlled by your web page's JavaScript.
:::

---

## Configuration

### Native Theme Colors

You can customize the colors for both light and dark modes in their respective files:

-   **Light Mode:** `app/src/main/res/values/colors.xml`
-   **Dark Mode:** `app/src/main/res/values-night/colors.xml`

### Web Page Control

Your web page can control the native theme using the provided JavaScript interface.

```javascript
// In your website's script.js

function changeTheme(theme) { // theme can be 'light', 'dark', or 'system'
  // 1. Change the web page's CSS
  document.body.classList.toggle('dark-mode', theme === 'dark');

  // 2. Tell the native app to change its theme
  if (window.AndroidInterface && typeof window.AndroidInterface.setNativeTheme === 'function') {
    window.AndroidInterface.setNativeTheme(theme);
  }
}
```