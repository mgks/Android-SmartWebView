---
title: 'URL Handling & Navigation'
description: 'Managing internal/external links and UI layouts.'
icon: 'compass'
---

Smart WebView provides flexible options for handling URL navigation and choosing the app's primary UI layout.

---

## URL Handling

This controls how the app treats different types of links.

**External Links:**

You can configure how links that point to domains outside your main website are handled.

*   `ASWP_EXTURL` (`true`/`false`): If `true`, external links are opened outside the app. If `false`, all links are loaded inside the WebView.
*   `ASWP_TAB` (`true`/`false`): If `ASWP_EXTURL` is `true`, this determines whether to use integrated Chrome Custom Tabs (`true`) or the device's default browser (`false`).
*   `ASWV_EXC_LIST` (String): A comma-separated list of domains that should be treated as internal, even if they don't match your main `ASWV_HOST`.

**Special URL Schemes:**

The app intercepts URLs with specific prefixes to trigger native actions:

*   `tel:*`: Opens the default phone dialer.
*   `rate:*`: Opens the app's page on the Google Play Store.
*   `share:*`: Opens the native sharing dialog to share content from the app.
*   `exit:*`: Closes the application.
*   `print:*`: Opens the native print dialog.
*   `getloc:*`: (For offline page) Fetches and displays GPS coordinates.
*   `fcm:*`: (For offline page) Triggers a test Firebase notification.

These are handled in the `url_actions` method in `Functions.java`.

---

## UI Layout Modes

Configure the app's main layout via the `ASWV_LAYOUT` variable in `SmartWebView.java`.

### Mode 0: Fullscreen Layout (Default)

*   **Description:** The WebView occupies the entire screen. This is ideal for a simple, immersive web wrapper.
*   **Layout File:** `app/src/main/res/layout/activity_main.xml`

### Mode 1: Drawer Layout

*   **Description:** Implements a standard Android navigation drawer with a side menu and a top action bar.
*   **Layout Files:** `drawer_main.xml`, `drawer_main_bar.xml`, etc.
*   **Menu Definition:** `app/src/main/res/menu/activity_main_drawer.xml`
*   **Menu Handling:** `onNavigationItemSelected` in `Functions.java`.