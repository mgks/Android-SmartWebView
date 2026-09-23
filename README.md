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
    <b>A clean and modern, open-source solution for building advanced hybrid Android apps.</b>
  </p>
  
  <!-- BADGES -->
  <p>
    <img alt="Language" src="https://img.shields.io/badge/language-Java-red.svg?style=flat-square">
    <img src="https://img.shields.io/github/v/release/mgks/android-smartwebview?style=flat-square&color=38bd24" alt="release version">
    <img src="https://img.shields.io/github/actions/workflow/status/mgks/Android-SmartWebView/android-ci.yml?branch=master&style=flat-square&label=build" alt="build status">
    <img src="https://img.shields.io/badge/API-24%2B-orange?style=flat-square" alt="min API">
    <img src="https://img.shields.io/github/license/mgks/android-smartwebview.svg?style=flat-square&color=blue" alt="license">
  </p>

  <!-- MENU -->
  <p>
    <h4>
      <a href="http://mgks.github.io/Android-SmartWebView/documentation/">Documentation</a> &bull;
      <a href="https://github.com/mgks/Android-SmartWebView/discussions">Discussions</a> &bull;
      <a href="https://github.com/mgks/Android-SmartWebView/issues">Issues</a>
    </h4>
  </p>

  <!-- PREVIEW -->
  <p>
    <img width="850" alt="cover-swv" src="https://github.com/user-attachments/assets/615e82f1-18fe-42a7-bf98-0a4c53660995" />
  </p>

</div>

**Android Smart WebView** is a robust foundation for converting any website into a feature-rich mobile application, with a plugin system for extending native functionality without touching core code. Targets **Android 17 (API 37)**, minimum Android 7.0 (API 24).

## Features

- **Plugin Architecture** — Self-registering, modular plugins. Enable or disable any plugin from `swv.properties` without modifying Java source.
- **File Uploads & Camera** — Full support for `<input type="file">`, direct camera capture, and multi-file selection.
- **Push Notifications** — Firebase Cloud Messaging integration (requires `google-services.json`).
- **Google Analytics** — Built-in GTAG support, configured via `swv.properties`.
- **Location Services** — On-demand GPS with a 10-second timeout and automatic fallback to cached coordinates.
- **Content Sharing** — Receive and handle shared content from other apps via Android's share sheet.
- **Downloads & Printing** — File downloads via `DownloadManager`, web printing via native print services.
- **Edge-to-Edge Display** — Full support for Android's edge-to-edge window mode with proper inset handling.
- **Predictive Back** — Android 13+ predictive back gesture support via `OnBackInvokedCallback`.
- **Network Awareness** — `NetworkInfoPlugin` exposes the current connection state to your web app.
- **Custom UI Modes** — Fullscreen immersive layout or a standard navigation drawer layout.
- **Secure by Default** — SSL verification, screenshot blocking, and third-party cookie controls, all configurable.

## Quick Start

1. **Clone the repository:**
   ```sh
   git clone https://github.com/mgks/Android-SmartWebView.git
   ```
2. **Open in Android Studio** — `File > Open > Select the cloned project folder`
3. **Configure `swv.properties`** — Open `app/src/main/assets/swv.properties` and set `app.url` to your website's URL.
4. **Add `google-services.json` (optional)** — If using Firebase, place the file from the Firebase console into the `app/` directory.
5. **Build & Run** — `Build > Clean Project`, then `Build > Rebuild Project`.

## Configuration

All configuration lives in `app/src/main/assets/swv.properties`:

| Property | Purpose |
| :--- | :--- |
| `app.url` | The main URL the WebView loads |
| `offline.url` | Local page shown when there is no internet connection |
| `feature.*` | Boolean toggles for uploads, camera, zoom, pull-to-refresh, etc. |
| `plugins.enabled` | Comma-separated list of active plugins |
| `security.*` | SSL, screenshot blocking, third-party cookie controls |
| `permissions.on.launch` | Permission groups requested at startup (`LOCATION`, `NOTIFICATIONS`) |

## Included Plugins

All plugins are free and included. Activate them by adding their name to `plugins.enabled` in `swv.properties`.

| Plugin | Description |
| :--- | :--- |
| `AdMobPlugin` | Google AdMob banner, interstitial, and rewarded ads |
| `BiometricPlugin` | Fingerprint or face unlock at launch or on demand |
| `QRScannerPlugin` | Native QR and barcode scanning via device camera |
| `ImageCompressionPlugin` | Compress images before upload to reduce bandwidth |
| `JSInterfacePlugin` | Two-way bridge between JavaScript and native Android code |
| `LocationPlugin` | On-demand GPS with cache fallback via `GeolocationCachePlugin` |
| `GeolocationCachePlugin` | Stores and serves cached location coordinates |
| `NetworkInfoPlugin` | Exposes current connection state to your web app |
| `DialogPlugin` | Native alert and confirmation dialogs from JavaScript |
| `ToastPlugin` | Short, non-blocking native toast notifications |
| `RatingPlugin` | In-app review prompt based on configurable usage triggers |
| `ClipboardPlugin` | Read from and write to the device clipboard |
| `SharePlugin` | Native share sheet integration |

## Standalone Libraries

The core components of Smart WebView are also available as independent libraries for any Android project.

| Library | Description |
| :--- | :--- |
| **[Biometric Gate](https://github.com/mgks/android-biometric-gate)** | Lifecycle-aware lock screen for any Activity |
| **[File Handler](https://github.com/mgks/android-webview-file-handler)** | File uploads and camera captures for WebViews |
| **[JS Bridge](https://github.com/mgks/android-webview-js-bridge)** | Promise-based communication between Kotlin and JS |
| **[Print Helper](https://github.com/mgks/android-webview-print-helper)** | One-line printing for Android WebViews |
| **[FCM Sync](https://github.com/mgks/android-webview-fcm-sync)** | Sync Firebase push tokens to WebView cookies |

## Code Overview

| File | Purpose |
| :--- | :--- |
| `swv.properties` | All runtime and build configuration |
| `SWVContext.java` | Loads config, holds shared app state |
| `MainActivity.java` | WebView setup, lifecycle, and feature wiring |
| `PluginInterface.java` & `PluginManager.java` | Plugin architecture contracts and registry |
| `Playground.java` | Plugin API keys and development configuration |
| `plugins/` | Full source for all included plugins |

## Contributing

Found a bug or have an idea? [Open an issue](https://github.com/mgks/Android-SmartWebView/issues) or [submit a pull request](https://github.com/mgks/Android-SmartWebView/pulls). Questions or want to show what you've built? [Join the discussion](https://github.com/mgks/Android-SmartWebView/discussions).

## License

Distributed under the MIT License. See `LICENSE` for more information.

> **{ github.com/mgks }**
>
> ![Website Badge](https://img.shields.io/badge/Visit-mgks.dev-blue?style=flat&link=https%3A%2F%2Fmgks.dev) ![Sponsor Badge](https://img.shields.io/badge/%20%20Become%20a%20Sponsor%20%20-red?style=flat&logo=github&link=https%3A%2F%2Fgithub.com%2Fsponsors%2Fmgks)
