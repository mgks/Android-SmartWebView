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

You can configure how links that point to domains outside your main website are handled in `app/src/main/assets/swv.properties`.

*   `feature.open.external.urls=true`: If `true`, external links are opened outside the app. If `false`, all links are loaded inside the WebView.
*   `feature.chrome.tabs=true`: If external URLs are enabled, this determines whether to use integrated Chrome Custom Tabs (`true`) or the device's default browser (`false`).
*   `external.url.exception.list`: A comma-separated list of domains that should be treated as internal, even if they don't match your main host.

**Special URL Schemes:**

The app intercepts URLs with specific prefixes to trigger native actions:

*   `tel:*`: Opens the default phone dialer.
*   `rate:*`: Opens the app's page on the Google Play Store.
*   `share:*`: Opens the native sharing dialog.
*   `exit:*`: Closes the application.
*   `print:*`: Opens the native print dialog.

These are handled in the `url_actions` method in `Functions.java`.

---

## UI Layout Modes

Configure the app's main layout via the `ui.layout` property in `app/src/main/assets/swv.properties`.

### Mode 0: Fullscreen Layout

*   **Description:** The WebView occupies the entire screen. This is ideal for a simple, immersive web wrapper.
*   **Property:** `ui.layout=0`

### Mode 1: Drawer Layout (Default)

*   **Description:** Implements a standard Android navigation drawer with a side menu and a top action bar.
*   **Property:** `ui.layout=1`