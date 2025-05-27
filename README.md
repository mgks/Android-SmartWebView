<!--
  Smart WebView 7.0

  MIT License (https://opensource.org/licenses/MIT)

  Smart WebView is an Open Source project that integrates native features into
  WebView to help create advanced hybrid applications (https://github.com/mgks/Android-SmartWebView).

  Explore plugins and enhanced capabilities: (https://docs.mgks.dev/smart-webview/plugins)
  Join the discussion: (https://github.com/mgks/Android-SmartWebView/discussions)
  Support Smart WebView: (https://github.com/sponsors/mgks)

  Your support and acknowledgment of the project's source are greatly appreciated.
  Giving credit to developers encourages them to create better projects.
-->

# Android Smart WebView

<a href="https://github.com/mgks/Android-SmartWebView/">
  <img align="right" src="https://raw.githubusercontent.com/mgks/Android-SmartWebView/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="150" alt="Smart WebView Icon">
</a>

<p>
  <a href="#features"><img alt="Variant" src="https://img.shields.io/badge/language-java-red.svg"></a>
  <a href="https://github.com/mgks/Android-SmartWebView/releases"><img alt="Version" src="https://img.shields.io/badge/version-7.0-green.svg"></a>
  <a href="https://github.com/mgks/Android-SmartWebView/blob/master/LICENSE"><img alt="MIT License" src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
</p>

**Android Smart WebView** is a modern, open-source solution for building advanced hybrid Android apps. Effortlessly extend your app with plugins, native features, and a customizable UI. Whether you're a hobbyist or a professional, Smart WebView helps you bring your web content to life on Android with ease and flexibility.

## Features

- **Plugin Architecture:** Easily extend app functionality with self-registering plugins.
- **File Uploads & Camera Access:** Support for file selection and direct camera capture in WebView.
- **Push Notifications:** Integrated Firebase Cloud Messaging.
- **Google Analytics:** Built-in analytics support.
- **Custom UI Modes:** Fullscreen and drawer layouts.
- **Location & Permissions:** Access device GPS/location and manage permissions.
- **Content Sharing:** Receive and handle shared content from other apps.
- **Downloads & Printing:** Handle file downloads and print web content.
- **Modern WebView:** Secure, up-to-date, and highly configurable.

## Plugin Support (v7.1+)

Smart WebView now supports a powerful plugin system, allowing you to add new features and integrations with minimal effort. Plugins can be enabled, disabled, or customized to fit your app's needs. Here are some of the available plugins:

- [**Plugin Architecture**](https://docs.mgks.dev/smart-webview/plugins): Learn how the plugin system works and how to integrate plugins.
- [**Creating Plugins**](https://docs.mgks.dev/smart-webview/plugins/creating-plugins): Guide to building your own custom plugins.
- [**Playground**](https://docs.mgks.dev/smart-webview/plugins/playground): Test and experiment with plugins in a sandboxed environment.
- [**Toast**](https://docs.mgks.dev/smart-webview/plugins/toast): Show native Android toast messages from your web content.
- [**Admob**](https://docs.mgks.dev/smart-webview/plugins/admob): Integrate Google AdMob ads into your app.
- [**Google Auth**](https://docs.mgks.dev/smart-webview/plugins/google-auth): Add Google authentication for seamless sign-in.
- [**QR & Barcode Reader**](https://docs.mgks.dev/smart-webview/plugins/qr-barcode-reader): Scan QR codes and barcodes directly from your app.
- [**Biometric Auth**](https://docs.mgks.dev/smart-webview/plugins/biometric-auth): Enable fingerprint or face authentication.
- [**Image Compression**](https://docs.mgks.dev/smart-webview/plugins/image-compression): Compress images before uploading for better performance.
- [**CSS Injection**](https://docs.mgks.dev/smart-webview/plugins/css-injection): Dynamically inject custom CSS into your web pages.
- [**Payment Gateway**](https://docs.mgks.dev/smart-webview/plugins/payment-gateway): Integrate payment solutions for in-app purchases.

You can find more details and usage examples in the [Plugin Documentation](https://docs.mgks.dev/smart-webview/plugins).

## Quick Start

1. **Clone the repository:**
   ```sh
   git clone https://github.com/mgks/Android-SmartWebView.git
   ```
2. **Open in Android Studio:**
   - `File > Open > Select the project folder`
3. **Add your `google-services.json`** (if using Firebase services).
4. **Build & Run:**
   - `Build > Clean Project` then `Build > Rebuild Project`

## Basic Configuration

- **Set Main URL:**
  - Edit `ASWV_APP_URL` in `SmartWebView.java` to set your app's default web address.
- **Toggle Features:**
  - Enable/disable features (file upload, camera, location, etc.) by setting the corresponding `ASWP_*` variables in `SmartWebView.java`.
- **Permissions:**
  - Adjust required permissions in `AndroidManifest.xml` based on your app's needs (e.g., `INTERNET`, `CAMERA`, `LOCATION`).
- **Plugins:**
  - Add or remove plugins in the `plugins/` directory and register them in your code. See [Plugin Architecture](https://docs.mgks.dev/smart-webview/plugins).

## Documentation
- [Getting Started](https://docs.mgks.dev/smart-webview/)
- [Configuration](https://docs.mgks.dev/smart-webview/configuration)
- [Customization](https://docs.mgks.dev/smart-webview/customization)
- [Plugin Architecture](https://docs.mgks.dev/smart-webview/plugins)
- [Playground](https://docs.mgks.dev/smart-webview/plugins/playground)
- [FAQ & Troubleshooting](https://docs.mgks.dev/smart-webview/faq/)
- [Contributing](https://docs.mgks.dev/smart-webview/contributing)
- [License](https://docs.mgks.dev/smart-webview/license)

## Contributing & Support
- Found a bug or want to contribute? [Open an issue](https://github.com/mgks/Android-SmartWebView/issues) or [create a pull request](https://github.com/mgks/Android-SmartWebView/pulls).
- Support the project via [GitHub Sponsors](https://github.com/sponsors/mgks).

## License
This project is licensed under the [MIT License](LICENSE).

> **For new developers:** Programming can be challenging at times, but with practice and persistence, you can develop the skills to create amazing things. The beauty of programming is that it empowers you to bring your ideas to life and create your own world. Keep exploring & experimenting, and all the best for your next project!