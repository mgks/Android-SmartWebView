---
title: 'Image Compression Plugin'
description: 'Compressing images before uploading.'
icon: 'file-zipper'
---

This plugin provides functionality to compress images selected for upload directly on the device, significantly reducing bandwidth usage and upload times.

::: callout tip
All Premium Plugins are now available for free and open source to developers. Consider becoming **[Project Sponsor](https://github.com/sponsors/mgks)**.
:::

---

## Setup and Configuration

1.  **Enable Plugin:** Ensure `ImageCompressionPlugin` is listed in the `plugins.enabled` property in `app/src/main/assets/swv.properties`.
    ```bash
    # In swv.properties
    plugins.enabled=ImageCompressionPlugin,...
    ```
2.  **Configure Quality:** The default compression quality is `80` (out of 100). To change this, you currently modify the static initializer block in `app/src/main/java/mgks/os/swv/plugins/ImageCompressionPlugin.java`.
    ```java
    // In ImageCompressionPlugin.java
    static {
        Map<String, Object> config = new HashMap<>();
        config.put("quality", 75); // Change default quality here
        PluginManager.registerPlugin(new ImageCompressionPlugin(), config);
    }
    ```

---

## Usage

The plugin is designed to be used from JavaScript, typically after a user has selected an image file for upload and you have its `base64` representation.

### Compressing an Image from JavaScript

The plugin injects a `window.ImageCompressor` object into your web page.

```javascript
// Assume 'originalBase64' is the base64 string of the image you want to compress
// (e.g., from a FileReader result)

if (window.ImageCompressor) {
  // The compress function takes the original base64 string and a callback
  window.ImageCompressor.compress(originalBase64, function(compressedBase64) {
    if (compressedBase64) {
      console.log('Compression successful!');
      console.log('Original size:', originalBase64.length);
      console.log('Compressed size:', compressedBase64.length);
      
      // Now you can upload the 'compressedBase64' string to your server
      uploadImage(compressedBase64);

    } else {
      console.error('Compression failed.');
    }
  });
}
```
This workflow allows you to seamlessly compress images on the client-side before they are transmitted, saving data for both the user and your server.