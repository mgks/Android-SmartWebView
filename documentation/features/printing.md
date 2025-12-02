---
title: 'Printing'
description: 'Allowing users to print the current web page content.'
icon: 'print'
---

Smart WebView supports printing the content currently displayed in the WebView using the native Android print framework.

---

## How to Trigger Printing

Printing is initiated from your web content by using a hyperlink with the special URL scheme `print:`.

**HTML Example:**

```html
<a href="print:">Print this Page</a>

<!-- Or use a button with JavaScript -->
<button onclick="window.location.href='print:'">Print Report</button>
```

---

## How it Works

1.  A user clicks a `print:` link in the WebView.
2.  The `shouldOverrideUrlLoading` method in `MainActivity.java` intercepts this URL.
3.  It calls the `Functions.print_page` method.
4.  This method uses the Android `PrintManager` service to create a print job from the current WebView content.
5.  The standard Android print preview screen appears, allowing the user to select a printer, save as a PDF, and adjust settings.

::: callout tip
The quality of the printout depends on how well your webpage's CSS is optimized for print media (e.g., using `@media print` styles).
:::

---

## Requirements

*   Android 4.4 (KitKat, API 19) or higher.
*   The device must have print services enabled or configured (e.g., Cloud Print, Wi-Fi Direct printing, or Save as PDF).