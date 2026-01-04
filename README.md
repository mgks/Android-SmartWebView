<!--
  Smart WebView v8 - The Complete Open Source Edition
  https://github.com/mgks/Android-SmartWebView

  A modern, open-source WebView wrapper for building advanced hybrid Android apps.
  Native features, a powerful plugin architecture, and full customisation—built for developers.

  - Documentation: https://mgks.github.io/Android-SmartWebView/documentation
  - Discussions: https://github.com/mgks/Android-SmartWebView/discussions

  MIT License — https://opensource.org/licenses/MIT
-->

<div align="center">

  <!-- PROJECT TITLE -->
  <img src="https://raw.githubusercontent.com/mgks/Android-SmartWebView/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.webp" width="150" alt="Smart WebView Logo">

  <h1>Android Smart WebView</h1>
  
  <!-- ONE LINE SUMMARY -->
  <p>
    <b>A clean and modern, open-source solution for building advanced hybrid android apps.</b>
  </p>
  
  <!-- BADGES -->
  <p>
    <img alt="Language" src="https://img.shields.io/badge/language-Java-red.svg?style=flat-square">
    <img src="https://img.shields.io/github/v/release/mgks/android-smartwebview?style=flat-square&color=38bd24" alt="release version">
    <!--<img src="https://img.shields.io/github/stars/mgks/android-smartwebview?style=flat-square&logo=github&color=blue" alt="stars">
    <img src="https://img.shields.io/github/forks/mgks/android-smartwebview?style=flat-square&color=blue" alt="stars">-->
    <img src="https://img.shields.io/github/license/mgks/android-smartwebview.svg?style=flat-square&color=blue" alt="license">
  </p>

  <!-- MENU -->
  <p>
    <h4>
      <a href="http://mgks.github.io/Android-SmartWebView/documentation/">Documentation</a> • 
      <a href="https://github.com/mgks/Android-SmartWebView/discussions">Discussions</a> • 
      <a href="https://github.com/mgks/Android-SmartWebView/issues">Issues</a>
    </h4>
  </p>

  <!-- PREVIEW -->
  <p>
    <img width="850" alt="cover-swv" src="https://github.com/user-attachments/assets/615e82f1-18fe-42a7-bf98-0a4c53660995" />
  </p>

</div>

**Android Smart WebView** provides a robust foundation for converting any website into a feature-rich mobile application, complete with a powerful plugin system for extending native functionality.

## Features

*   **Plugin Architecture:** Extend app functionality with self-registering, modular plugins.
*   **File Uploads & Camera Access:** Full support for `<input type="file">`, including direct camera capture.
*   **Push Notifications:** Integrated with Firebase Cloud Messaging (requires `google-services.json`).
*   **Google Analytics:** Built-in support for usage tracking (configure GTAG ID in `swv.properties`).
*   **Custom UI Modes:** Choose between a fullscreen immersive layout or a standard drawer navigation layout.
*   **Location Services:** Access device GPS for location-aware web applications.
*   **Content Sharing:** Natively receive and handle content shared from other apps.
*   **Downloads & Printing:** Handle file downloads and print web content using native services.
*   **Modern & Secure:** Built with up-to-date libraries, security best practices, and highly configurable via `swv.properties`.

## Quick Start

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/mgks/Android-SmartWebView.git
    ```
2.  **Open in Android Studio:**
    *   `File > Open > Select the cloned project folder`
3.  **Configure `swv.properties`:**
    *   Open `app/src/main/assets/swv.properties`.
    *   Change `app.url` to your website's URL and adjust other settings as needed. This is the main configuration file for the app.
4.  **Add `google-services.json` (Optional):**
    *   If you plan to use Firebase services (like FCM for push notifications), place your `google-services.json` file from the Firebase console into the `app/` directory.
5.  **Build & Run:**
    *   `Build > Clean Project` then `Build > Rebuild Project`.

## Basic Configuration

All primary configuration is done within `app/src/main/assets/swv.properties`:

*   **Main Application URL:**
    *   Set `app.url` to your web application's address.
    *   `offline.url` (`file:///android_asset/web/offline.html`) is used if no internet is detected.
*   **Feature Toggles:**
    *   Enable or disable core features (file uploads, camera access, location services, pull-to-refresh, etc.) by modifying the `feature.*` boolean properties.
*   **Permissions:**
    *   Review and adjust permissions in `AndroidManifest.xml` based on the features you enable. For example, `CAMERA` for camera uploads, `ACCESS_FINE_LOCATION` for GPS.
*   **Plugin Configuration:**
    *   Plugin-specific behavior (like AdMob IDs or Biometric Auth on launch) is configured in `Playground.java`. This allows you to change settings without modifying the plugin source code itself.

## Included Plugins

Smart WebView now includes all plugins for free, providing a comprehensive toolkit to build powerful hybrid apps out of the box.

*   **AdMob:** Integrate Google AdMob banner, interstitial, and rewarded ads.
*   **Biometric Authentication:** Secure your app with fingerprint or face unlock.
*   **QR & Barcode Reader:** Natively scan QR codes and barcodes using the device camera.
*   **Image Compression:** Automatically compress images before uploading to save bandwidth and improve performance.
*   **JS Interface:** A powerful two-way bridge for seamless communication between your web app's JavaScript and native Android code.
*   **Location:** On-demand access to the device's GPS location.
*   **Native Dialogs:** Display native alert and confirmation dialogs from your JavaScript.
*   **Toast Messages:** Show short, non-blocking native toast notifications.
*   **In-App Review:** Prompt users to rate your app on the Google Play Store based on usage triggers.

## Standalone Libraries

We are modularizing the best parts of Smart WebView into lightweight, standalone libraries that you can use in **any** Android project (Native or Hybrid).

| Library | Description |
| :--- | :--- |
| **[Biometric Gate](https://github.com/mgks/android-biometric-gate)** | Secure, lifecycle-aware lock screen for any Activity. |
| **[File Handler](https://github.com/mgks/android-webview-file-handler)** | Painless file uploads & camera captures for WebViews. |
| **[JS Bridge](https://github.com/mgks/android-webview-js-bridge)** | Two-way, Promise-based communication between Kotlin & JS. |
| **[Print Helper](https://github.com/mgks/android-webview-print-helper)** | One-line printing for Android WebViews. |
| **[FCM Sync](https://github.com/mgks/android-webview-fcm-sync)** | Sync Firebase Push Tokens to WebView Cookies. |

## Further Information

The best way to understand the project in depth is to explore the source code:
*   **`app/src/main/assets/swv.properties`**: Contains all global configurations.
*   **`SWVContext.java`**: The central class that loads the configuration and holds app state.
*   **`MainActivity.java`**: The main entry point that handles WebView setup and integrates core features.
*   **`PluginInterface.java` & `PluginManager.java`**: Key components of the plugin architecture.
*   **`Playground.java`**: The central place for configuring and testing plugins.
*   The `plugins/` directory: Contains the full source code for all included plugins.

## Contributing & Community

*   Found a bug or have an idea? [Open an issue](https://github.com/mgks/Android-SmartWebView/issues) or [create a pull request](https://github.com/mgks/Android-SmartWebView/pulls).
*   Have questions or want to share what you've built? [Join the discussion](https://github.com/mgks/Android-SmartWebView/discussions).

## License

Distributed under the MIT License. See `LICENSE` for more information.

> **{ github.com/mgks }**
> 
> ![Website Badge](https://img.shields.io/badge/Visit-mgks.dev-blue?style=flat&link=https%3A%2F%2Fmgks.dev) ![Sponsor Badge](https://img.shields.io/badge/%20%20Become%20a%20Sponsor%20%20-red?style=flat&logo=github&link=https%3A%2F%2Fgithub.com%2Fsponsors%2Fmgks)
