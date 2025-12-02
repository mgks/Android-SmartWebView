---
title: 'QR/Barcode Reader Plugin (Premium)'
description: 'Scanning QR codes and barcodes using the device camera.'
icon: 'qrcode'
---

This premium plugin integrates native QR code and barcode scanning functionality using the device's camera.

::: callout tip
All Premium Plugins are now available for free and open source to developers. Consider becoming **[Project Sponsor](https://github.com/sponsors/mgks/sponsorships?sponsor=mgks&tier_id=468838)**.
:::

---

## Setup and Configuration

1.  **Obtain the Plugin:** Acquire the plugin files through a GitHub sponsorship.
2.  **Add to Project:** Place the `QRScannerPlugin.java` file in the `plugins/` directory.
3.  **Enable Plugin:** Ensure the plugin is enabled in `SmartWebView.java`:
    ```java
    put("QRScannerPlugin", true);
    ```
4.  **Dependencies:** The plugin relies on the `zxing-android-embedded` library. Ensure this dependency is present in your `app/build.gradle` file:
    ```groovy
    implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
    ```
5.  **Camera Permission:** The app already requests the `CAMERA` permission, which this plugin requires.

---
## Usage

The plugin is controlled via a JavaScript interface.

### Starting a Scan from JavaScript

The plugin injects a `window.QRScanner` object into your web page.

```javascript
// Open the camera and start scanning for a code
window.QRScanner.scan();
```

### Callbacks in JavaScript

Define callback functions in your JavaScript to handle the results of the scan.

```javascript
// Called when a code is successfully scanned
window.QRScanner.onScanSuccess = function(contents) {
  console.log('Scanned content:', contents);
  alert('Scanned: ' + contents);
  // Process the scanned data in your web app
};

// Called if the user cancels the scan (e.g., by pressing the back button)
window.QRScanner.onScanCancelled = function() {
  console.log('Scan was cancelled by the user.');
};
```