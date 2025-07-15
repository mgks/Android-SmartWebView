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
  <img align="right" src="https://raw.githubusercontent.com/mgks/Android-SmartWebView/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.webp" width="175" alt="Smart WebView Logo">
</a>

<p>
  <a href="#features"><img alt="Variant" src="https://img.shields.io/badge/language-Java-red.svg"></a>
  <a href="https://github.com/mgks/Android-SmartWebView/releases"><img alt="GitHub Release" src="https://img.shields.io/github/v/release/mgks/android-smartwebview"></a>
  <a href="https://github.com/mgks/Android-SmartWebView/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/mgks/android-smartwebview"></a>
</p>

**Android Smart WebView** is a modern, open-source solution for building advanced hybrid Android apps. It allows you to effortlessly extend your app with plugins, native features, and a customizable UI.

**[DOCUMENTATION](https://docs.mgks.dev/smart-webview/)** | **[GET PREMIUM PLUGINS](https://github.com/sponsors/mgks/sponsorships?sponsor=mgks&tier_id=468838)** | **[ISSUES](https://github.com/mgks/Android-SmartWebView/issues)**


## Core Features

*   **Plugin Architecture:** Extend app functionality with self-registering plugins. See `PluginInterface.java`, `PluginManager.java`, and existing plugins in `/plugins/` for details.
*   **File Uploads & Camera Access:** Support for file selection and direct camera capture in WebView.
*   **Push Notifications:** Integrated Firebase Cloud Messaging (requires `google-services.json`).
*   **Google Analytics:** Built-in support (configure GTAG ID in `swv.properties`).
*   **Custom UI Modes:** Fullscreen and drawer layouts (configurable in `swv.properties`).
*   **Location & Permissions:** Access device GPS/location and manage permissions.
*   **Content Sharing:** Receive and handle shared content from other apps via `ShareActivity.java`.
*   **Downloads & Printing:** Handle file downloads and print web content.
*   **Modern WebView:** Secure, up-to-date, and highly configurable via `swv.properties` and `MainActivity.java`.

## Plugins

Smart WebView features a plugin system to add new features with minimal effort.

*   **Understanding Plugins:**
    *   The core contract is defined in `PluginInterface.java`.
    *   Plugin lifecycle and registration are managed by `PluginManager.java`.
    *   Example plugins (`ToastPlugin.java`, `LocationPlugin.java`) are located in `/plugins/`. These serve as excellent references for creating new plugins.

*   **Configuring & Testing Plugins:**
    *   `Playground.java` is used to configure, test, and demonstrate plugin functionality during development. This is where you set plugin-specific options like AdMob IDs or enable Biometric Auth on launch.

*  **Premium Plugins ⭐:** List of premium plugins for **[Project Sponsors](https://github.com/sponsors/mgks)**.
    - ✅ **AdMob**: Integrate Google AdMob into your app; Includes Banner, Interstitial and Rewarded ad units.
    - ✅ **Biometric Auth**: Enable fingerprint or face authentication for overall app or trigger on demand with JavaScript.
    - ✅ **QR & Barcode Reader**: Scan QR codes and barcodes directly from your app.
    - ✅ **Image Compression**: Compress images before uploading for better performance.
    - ✅ **JS Interface**: Allows JavaScript code to call native methods and receive callbacks.
    - 🚧 **Google Auth**: Add Google authentication for seamless sign-in.
    - 🚧 **Payment Gateway**: Integrate payment solutions for in-app purchases.
    - 🚧 **Enhanced Video Player**: Handle requests for fullscreen video playback (e.g., from YouTube or Vimeo embeds).
    - 🚧 **CSS Injection**: Dynamically inject custom CSS into your web pages.
    - 🚧 **WebRTC**: Build rich, real-time communication features directly into your web app.
    - 🚧 **Local Network Access**: Host app with local network (e.g., for a dev server or an enterprise intranet).

   *<sup>🚧</sup> Currently in development; availability in final bundle is subject to change.*

## Quick Start

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/mgks/Android-SmartWebView.git
    ```
2.  **Open in Android Studio:**
    *   `File > Open > Select the project folder`
3.  **Configure `swv.properties`:**
    *   Open `app/src/main/assets/swv.properties`.
    *   Change `app.url` to your website's URL and adjust other settings as needed. This is the main configuration file for the app.
4.  **Add `google-services.json` (Optional):**
    *   If you plan to use Firebase services (like FCM for push notifications), obtain your `google-services.json` file from the Firebase console and place it in the `app/` directory.
5.  **Build & Run:**
    *   `Build > Clean Project` then `Build > Rebuild Project`

## Basic Configuration

All primary configuration is done within `app/src/main/assets/swv.properties`:

*   **Main Application URL:**
    *   Set `app.url` to your web application's address.
    *   `offline.url` (`file:///android_asset/web/offline.html`) is used if no internet is detected.
*   **Feature Toggles:**
    *   Enable or disable features (file uploads, camera access, location services, pull-to-refresh, etc.) by modifying the `feature.*` boolean properties.
*   **Permissions:**
    *   Review and adjust permissions in `AndroidManifest.xml` based on the features you enable. For example, `CAMERA` for camera uploads, `ACCESS_FINE_LOCATION` for GPS.
*   **Plugin Configuration:**
    *   Plugin behavior (like AdMob IDs or Biometric Auth on launch) is configured in `Playground.java`. This allows you to change settings without modifying the plugin source code itself.

## Further Information & Understanding the Code

The best way to understand the project in depth is to explore the source code:
*   **`app/src/main/assets/swv.properties`**: Contains all global configurations.
*   **`SWVContext.java`**: The central class that loads the configuration and holds app state.
*   **`MainActivity.java`**: The main entry point, handles WebView setup, and integrates core features.
*   **`Functions.java`**: Utility functions used throughout the app.
*   **`PluginInterface.java`, `PluginManager.java`**: Key components of the plugin architecture.
*   **`Playground.java`**: The central place for configuring and testing plugins.
*   The `plugins/` directory: Contains example and premium plugin implementations.
*   Inline comments throughout the code provide additional context.

## Contributing & Support
*   Found a bug or want to contribute? [Open an issue](https://github.com/mgks/Android-SmartWebView/issues) or [create a pull request](https://github.com/mgks/Android-SmartWebView/pulls).
*   Support the project via [GitHub Sponsors](https://github.com/sponsors/mgks).

## License
This project is licensed under the [MIT License](LICENSE).

> **For new developers:** Programming can be challenging at times, but with practice and persistence, you can develop the skills to create amazing things. The beauty of programming is that it empowers you to bring your ideas to life and create your own world. Keep exploring & experimenting, and all the best for your next project!