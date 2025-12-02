---
title: 'File Handling'
description: 'Managing file uploads, camera access, and downloads.'
icon: 'folder-open'
---

Smart WebView provides robust support for handling file uploads initiated from your web content, including direct access to the device camera, and manages file downloads.

---

## File Uploads & Camera Access

This functionality allows users to interact with `<input type="file">` elements in your web content.

**Configuration:**

Controlled by variables in `SmartWebView.java`:
*   `ASWP_FUPLOAD` (`true`/`false`): Globally enable/disable file input.
*   `ASWP_CAMUPLOAD` (`true`/`false`): Include a camera capture option in the chooser.
*   `ASWP_MULFILE` (`true`/`false`): Allow multiple file selection if the HTML input tag supports it.

**Permissions:**

The following permissions are declared in `AndroidManifest.xml` and requested at runtime if needed:
*   `android.permission.CAMERA`
*   `android.permission.READ_MEDIA_IMAGES`
*   `android.permission.READ_MEDIA_VIDEO`
*   `android.permission.READ_MEDIA_AUDIO`
*   `android.permission.WRITE_EXTERNAL_STORAGE` (with `maxSdkVersion` for older Android versions)

**How it Works:**

1.  A user taps an `<input type="file">` element in the WebView.
2.  The `onShowFileChooser` method in `FileProcessing.java` is triggered.
3.  It constructs an `Intent` that opens a system chooser, allowing the user to select files or use the camera (if enabled).
4.  The HTML `accept` attribute can filter the file types shown (e.g., `image/*`).
5.  The HTML `multiple` attribute, combined with `ASWP_MULFILE`, allows for multi-file selection.
6.  The selected file URIs are returned to the WebView to be processed by your web application.

---

## Downloads

This handles files downloaded *from* the WebView.

**How it Works:**

1.  The WebView's `DownloadListener` detects a URL that triggers a download.
2.  It uses the Android `DownloadManager` service to handle the download.
3.  A system notification shows the download progress.
4.  Files are saved to the public "Downloads" directory on the device.
5.  A Toast message confirms that the download has started.