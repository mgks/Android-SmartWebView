---
title: 'Image Compression Plugin (Premium)'
description: 'Compressing images before uploading.'
icon: 'file-zipper'
---

This premium plugin provides functionality to compress images selected for upload directly on the device, significantly reducing bandwidth usage and upload times.

::: callout tip
All Premium Plugins are now available for free and open source to developers. Consider becoming **[Project Sponsor](https://github.com/sponsors/mgks/sponsorships?sponsor=mgks&tier_id=468838)**.
:::

---

## Setup and Configuration

1.  **Obtain the Plugin:** Acquire the plugin files through a GitHub sponsorship.
2.  **Add to Project:** Place the `ImageCompressionPlugin.java` file in the `plugins/` directory.
3.  **Enable Plugin:** Ensure the plugin is enabled in `SmartWebView.java`:
    ```java
    put("ImageCompressionPlugin", true);
    ```
4.  **Configure Quality:** The default compression quality is `80` (out of 100). You can change this default in the `static` block of the `ImageCompressionPlugin.java` file.
    ```java
    // In ImageCompressionPlugin.java
    static {
        Map<String, Object> config = new HashMap<>();
        config.put("quality", 75); // Change default quality
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