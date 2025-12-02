---
title: 'Analytics'
description: 'Integrating Google Analytics for usage tracking.'
icon: 'chart-area'
---

Smart WebView supports integration with Google Analytics using the gtag.js library to track user interactions within your web content.

---

## Configuration

1.  **Get Your Measurement ID:** Obtain your Google Analytics Measurement ID (e.g., `G-XXXXXXXXXX`) from your Google Analytics property settings.
2.  **Set the ID in `swv.properties`:** Assign your Measurement ID to the `analytics.gtag.id` property in `app/src/main/assets/swv.properties`.
    ```bash
    # In swv.properties
    analytics.gtag.id=G-7XXC1C7CRQ # <-- Replace with your actual ID
    ```
    If the ID is left empty, Analytics integration will be disabled.

---

## How it Works

*   **Dynamic Injection:** Instead of adding the gtag.js snippet to your HTML, Smart WebView injects it dynamically using JavaScript *after* the page has finished loading. This is handled by the `onPageFinished` event in `MainActivity.java`.
*   **Improved Performance:** This approach prevents the Analytics script from blocking initial page rendering.

---

## Tracking Events

Once gtag.js is loaded, you can track events within your web application's JavaScript just as you would on a regular website.

**Example: Tracking a Button Click**

```javascript
// In your web page's JavaScript (e.g., script.js)
document.getElementById('myButton').addEventListener('click', function() {
  // Check if gtag function exists
  if (typeof gtag === 'function') {
    gtag('event', 'button_click', {
      'event_category': 'Engagement',
      'event_label': 'Special Feature Button'
    });
    console.log('GA event sent: button_click');
  } else {
    console.error('gtag function not found.');
  }
});
```

Refer to the [Google Analytics gtag.js documentation](https://developers.google.com/analytics/devguides/collection/gtagjs/events) for more details.