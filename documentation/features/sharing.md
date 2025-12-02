---
title: 'Inbound Sharing'
description: 'Receiving text and links shared from other apps.'
icon: 'share'
---

Smart WebView can register as a target for Android's native sharing functionality, allowing users to share content like URLs and text directly *to* your application from other apps.

---

## How it Works

1.  **Enabling via Manifest:** Sharing is enabled via `<intent-filter>` elements for the `ShareActivity` in `AndroidManifest.xml`. These filters specify that the app can handle `ACTION_SEND` intents for `text/*` and `image/*` MIME types.
2.  **User Action:** A user in another app (like a browser or social media app) uses the "Share" button and selects your app from the list.
3.  **Activity Launch:** Android launches the `ShareActivity` of your app.
4.  **Data Handling:** `ShareActivity` extracts the shared text or link from the intent.
5.  **Redirection:** It then constructs a URL based on the main app URL (`ASWV_URL`) and appends the shared content as query parameters. For example: `https://your-site.com/?s_uri=SHARED_CONTENT`.
6.  **Loading in WebView:** Finally, it launches the `MainActivity` and instructs it to load this newly constructed URL, allowing your web application to process the shared content.

---

## Processing on Your Website

Your web application needs to be able to parse the URL query parameters to handle the shared data.

**Example JavaScript:**

```javascript
const urlParams = new URLSearchParams(window.location.search);
const sharedContent = urlParams.get('s_uri'); // Matches the key from ShareActivity

if (sharedContent) {
  // The content was shared from another app
  console.log('Received shared content:', sharedContent);
  // Now you can display it, fill a form, etc.
  document.getElementById('my-textarea').value = sharedContent;
}
```

---

## Disabling Sharing

To disable this feature, remove or comment out the entire `<activity android:name=".ShareActivity">...</activity>` block from `AndroidManifest.xml`.