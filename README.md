<!--
  Smart WebView v7
  https://github.com/mgks/Android-SmartWebView

  A modern, open-source WebView wrapper for building advanced hybrid Android apps.
  Native features, modular plugins, and full customisation—built for developers.

  - Documentation: https://docs.mgks.dev/smart-webview  
  - Plugins: https://docs.mgks.dev/smart-webview/plugins  
  - Discussions: https://github.com/mgks/Android-SmartWebView/discussions  
  - Sponsor the Project: https://github.com/sponsors/mgks  

  MIT License — https://opensource.org/licenses/MIT  

  Mentioning Smart WebView in your project helps others find it and keeps the dev loop alive.
-->

# Android Smart WebView

<a href="https://github.com/mgks/Android-SmartWebView/">
  <img align="right" src="https://raw.githubusercontent.com/mgks/Android-SmartWebView/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="150" alt="Smart WebView Icon">
</a>

<p>
  <a href="#features"><img alt="Variant" src="https://img.shields.io/badge/language-Java-red.svg"></a>
  <a href="https://github.com/mgks/Android-SmartWebView/releases"><img alt="GitHub Release" src="https://img.shields.io/github/v/release/mgks/android-smartwebview"></a>
  <a href="https://github.com/mgks/Android-SmartWebView/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/mgks/android-smartwebview"></a>
</p>

**Android Smart WebView** is a modern, open-source solution for building advanced hybrid Android apps. It allows you to effortlessly extend your app with plugins, native features, and a customizable UI.

**[DOCUMENTATION](https://docs.mgks.dev/smart-webview/)** | **[GET PLUGINS](https://github.com/sponsors/mgks)** | **[ISSUES](https://github.com/mgks/Android-SmartWebView/issues)**

## Core Features

*   **Plugin Architecture:** Extend app functionality with self-registering plugins. See the `PluginInterface.java`, `PluginManager.java`, and existing plugins in `/plugins/` for details.
*   **File Uploads & Camera Access:** Support for file selection and direct camera capture in WebView.
*   **Push Notifications:** Integrated Firebase Cloud Messaging (requires `google-services.json`).
*   **Google Analytics:** Built-in support (configure GTAG ID in `SmartWebView.java`).
*   **Custom UI Modes:** Fullscreen and drawer layouts (configurable in `SmartWebView.java`).
*   **Location & Permissions:** Access device GPS/location and manage permissions.
*   **Content Sharing:** Receive and handle shared content from other apps via `ShareActivity.java`.
*   **Downloads & Printing:** Handle file downloads and print web content.
*   **Modern WebView:** Secure, up-to-date, and highly configurable via `SmartWebView.java` and `MainActivity.java`.

## Plugin System (v7.1+)

Smart WebView features a plugin system to add new features with minimal effort.
*   **Understanding Plugins:**
    *   The core contract is defined in `PluginInterface.java`.
    *   Plugin lifecycle and registration are managed by `PluginManager.java`.
    *   Example plugins (`AdMobPlugin.java`, `JSInterfacePlugin.java`, `ToastPlugin.java`) are located in `/plugins/`. These serve as excellent references for creating new plugins.
*   **Testing Plugins:**
    *   `Playground.java` is used to configure, test, and demonstrate plugin functionality during development.
*   **Included Plugins:** The project includes an example plugin for Toasts. Plugins are designed to be self-registering.
*   **Premium Plugins:** List of premium plugins for **[Project Sponsors](https://github.com/sponsors/mgks)**.
    - [**Admob**](https://docs.mgks.dev/smart-webview/plugins/admob): Integrate Google AdMob ads into your app.
    - [**Google Auth**](https://docs.mgks.dev/smart-webview/plugins/google-auth): Add Google authentication for seamless sign-in.
    - [**QR & Barcode Reader**](https://docs.mgks.dev/smart-webview/plugins/qr-barcode-reader): Scan QR codes and barcodes directly from your app.
    - [**Biometric Auth**](https://docs.mgks.dev/smart-webview/plugins/biometric-auth): Enable fingerprint or face authentication.
    - [**Image Compression**](https://docs.mgks.dev/smart-webview/plugins/image-compression): Compress images before uploading for better performance.
    - [**CSS Injection**](https://docs.mgks.dev/smart-webview/plugins/css-injection): Dynamically inject custom CSS into your web pages.
    - [**Payment Gateway**](https://docs.mgks.dev/smart-webview/plugins/payment-gateway): Integrate payment solutions for in-app purchases.

## Quick Start

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/mgks/Android-SmartWebView.git
    ```
2.  **Open in Android Studio:**
    *   `File > Open > Select the project folder`
3.  **Add `google-services.json`:**
    *   If you plan to use Firebase services (like FCM for push notifications), obtain your `google-services.json` file from the Firebase console and place it in the `app/` directory.
4.  **Build & Run:**
    *   `Build > Clean Project` then `Build > Rebuild Project`

## Basic Configuration

Most configuration is done within `SmartWebView.java`:

*   **Main Application URL:**
    *   Set `ASWV_APP_URL` to your web application's address.
    *   `ASWV_OFFLINE_URL` (`file:///android_asset/offline.html`) is used if `ASWV_APP_URL` points to a local file or if no internet is detected (and `ASWP_OFFLINE` logic permits).
*   **Feature Toggles:**
    *   Enable or disable features (file uploads, camera access, location services, pull-to-refresh, etc.) by modifying the boolean `ASWP_*` variables.
*   **Permissions:**
    *   Review and adjust permissions in `AndroidManifest.xml` based on the features you enable. For example, `CAMERA` for camera uploads, `ACCESS_FINE_LOCATION` for GPS.
*   **Adding/Modifying Plugins:**
    *   Plugins are Java classes that implement `PluginInterface`.
    *   They typically self-register with the `PluginManager` using a static block.
    *   Refer to the existing plugins in the `/plugins/` directory for examples on how to create and integrate them.

## Further Information & Understanding the Code

The best way to understand the project in depth is to explore the source code:
*   **`SmartWebView.java`**: Contains most global configurations and constants.
*   **`MainActivity.java`**: The main entry point, handles WebView setup, and integrates core features.
*   **`Functions.java`**: Utility functions used throughout the app.
*   **`PluginInterface.java`, `PluginManager.java`, `Playground.java`**: Key components of the plugin architecture.
*   The `/plugins/` directory: Contains example plugin implementations.
*   Inline comments throughout the code provide additional context.

## Contributing & Support
*   Found a bug or want to contribute? [Open an issue](https://github.com/mgks/Android-SmartWebView/issues) or [create a pull request](https://github.com/mgks/Android-SmartWebView/pulls).
*   Support the project via [GitHub Sponsors](https://github.com/sponsors/mgks).

## License
This project is licensed under the [MIT License](LICENSE).

> **For new developers:** Programming can be challenging at times, but with practice and persistence, you can develop the skills to create amazing things. The beauty of programming is that it empowers you to bring your ideas to life and create your own world. Keep exploring & experimenting, and all the best for your next project!