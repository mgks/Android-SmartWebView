---
title: 'QR/Barcode Reader Plugin'
description: 'Scanning QR codes and barcodes using the device camera.'
icon: 'qrcode'
---

This plugin integrates native QR code and barcode scanning functionality using the device's camera.

::: callout tip
All Premium Plugins are now available for free and open source to developers. Consider becoming **[Project Sponsor](https://github.com/sponsors/mgks)**.
:::

---

## Setup and Configuration

1.  **Enable Plugin:** Add `QRScannerPlugin` to the `plugins.enabled` list in `app/src/main/assets/swv.properties`.
    ```bash
    # In swv.properties
    plugins.enabled=QRScannerPlugin,ToastPlugin,...
    ```
2.  **Dependencies:** This plugin relies on the `zxing-android-embedded` library. Ensure the following dependencies are present in your `app/build.gradle` file (they are included by default in v8.0.0+):
    ```groovy
    implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
    implementation 'com.google.zxing:core:3.5.2'
    ```
3.  **Permissions:** The app automatically requests the `CAMERA` permission declared in `AndroidManifest.xml` when the scanner is invoked.

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
  // Process the scanned data in your web app (e.g., redirect to URL)
};

// Called if the user cancels the scan (e.g., by pressing the back button)
window.QRScanner.onScanCancelled = function() {
  console.log('Scan was cancelled by the user.');
};
```