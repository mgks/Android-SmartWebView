<!--
  Smart WebView v8 - The Complete Open Source Edition
  https://github.com/mgks/Android-SmartWebView

  A modern, open-source WebView wrapper for building advanced hybrid Android apps.
  Native features, a powerful plugin architecture, and full customisation—built for developers.

  - Documentation: https://mgks.github.io/Android-SmartWebView/documentation
  - Discussions: https://github.com/mgks/Android-SmartWebView/discussions

  MIT License — https://opensource.org/licenses/MIT
-->

# Android Smart WebView

<a href="https://github.com/mgks/Android-SmartWebView/">
  <img align="right" src="https://raw.githubusercontent.com/mgks/Android-SmartWebView/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.webp" width="175" alt="Smart WebView Logo">
</a>

<p>
  <a href="#features"><img alt="Language" src="https://img.shields.io/badge/language-Java-red.svg"></a>
  <a href="https://github.com/mgks/Android-SmartWebView/releases"><img alt="GitHub Release" src="https://img.shields.io/github/v/release/mgks/android-smartwebview"></a>
  <a href="https://github.com/mgks/Android-SmartWebView/blob/master/LICENSE"><img alt="License" src="https://img.shields.io/github/license/mgks/android-smartwebview"></a>
</p>

**Android Smart WebView** is a modern, open-source solution for building advanced hybrid Android apps. It provides a robust foundation for converting any website into a feature-rich mobile application, complete with a powerful plugin system for extending native functionality.

**[DOCUMENTATION](http://mgks.github.io/Android-SmartWebView/documentation/)** | **[DISCUSSIONS](https://github.com/mgks/Android-SmartWebView/discussions)** | **[ISSUES](https://github.com/mgks/Android-SmartWebView/issues)**

## Core Features

*   **Plugin Architecture:** Extend app functionality with self-registering, modular plugins.
*   **File Uploads & Camera Access:** Full support for `<input type="file">`, including direct camera capture.
*   **Push Notifications:** Integrated with Firebase Cloud Messaging (requires `google-services.json`).
*   **Google Analytics:** Built-in support for usage tracking (configure GTAG ID in `swv.properties`).
*   **Custom UI Modes:** Choose between a fullscreen immersive layout or a standard drawer navigation layout.
*   **Location Services:** Access device GPS for location-aware web applications.
*   **Content Sharing:** Natively receive and handle content shared from other apps.
*   **Downloads & Printing:** Handle file downloads and print web content using native services.
*   **Modern & Secure:** Built with up-to-date libraries, security best practices, and highly configurable via `swv.properties`.

## Included Plugins

Smart WebView now includes all plugins for free, providing a comprehensive toolkit to build powerful hybrid apps out of the box.

*   ✅ **AdMob:** Integrate Google AdMob banner, interstitial, and rewarded ads.
*   ✅ **Biometric Authentication:** Secure your app with fingerprint or face unlock.
*   ✅ **QR & Barcode Reader:** Natively scan QR codes and barcodes using the device camera.
*   ✅ **Image Compression:** Automatically compress images before uploading to save bandwidth and improve performance.
*   ✅ **JS Interface:** A powerful two-way bridge for seamless communication between your web app's JavaScript and native Android code.
*   ✅ **Location:** On-demand access to the device's GPS location.
*   ✅ **Native Dialogs:** Display native alert and confirmation dialogs from your JavaScript.
*   ✅ **Toast Messages:** Show short, non-blocking native toast notifications.
*   ✅ **In-App Review:** Prompt users to rate your app on the Google Play Store based on usage triggers.

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
This project is licensed under the [MIT License](LICENSE).
