<!--
  Smart WebView 7.0

  MIT License (https://opensource.org/licenses/MIT)
  Copyright (c) 2023 mgks (https://github.com/mgks)

  Smart WebView is an Open Source project that integrates native features into
  WebView to help create advanced hybrid applications (https://github.com/mgks/Android-SmartWebView).

  Join the discussion: (https://github.com/mgks/Android-SmartWebView/discussions)
  Support Smart WebView: (https://github.com/sponsors/mgks)

  Your support and acknowledgment of the project's source are greatly appreciated.
  Giving credit to developers encourages them to create better projects.
-->

<div align="center">
  <a href="https://github.com/mgks/Android-SmartWebView/"><img src="https://raw.githubusercontent.com/mgks/Android-SmartWebView/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="100" alt="Smart WebView Icon"></a>
  <h1>Android Smart WebView</h1>
  <p>
    <a href="#config-variables"><img alt="Variant" src="https://img.shields.io/badge/variant-java-red.svg"></a>
    <a href="https://github.com/mgks/Android-SmartWebView/releases"><img alt="Version" src="https://img.shields.io/badge/version-7.0-green.svg"></a>
    <a href="https://github.com/mgks/Android-SmartWebView/blob/master/LICENSE"><img alt="MIT License" src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
  </p>
  <p>
    <strong>
      <a href="#getting-started">GETTING STARTED</a> &middot;
      <a href="https://mgks.dev/blog/smart-webview-plugins">PLUGINS</a> &middot;
      <a href="https://mgks.dev/blog/smart-webview-documentation#index">DOCUMENTATION</a> &middot;
      <a href="https://github.com/mgks/Android-SmartWebView/issues">ISSUES</a>
    </strong>
  </p>
</div>

Smart WebView introduces a powerful plugin architecture, empowering developers to seamlessly extend and customize their hybrid applications. This update enhances core functionalities and streamlines the development process, making it easier to integrate advanced features like QR code scanning, Google Analytics, and more.

## Table of Contents

*   [Getting Started](#getting-started)
    *   [Prerequisites](#prerequisites)
    *   [Setup](#setup)
*   [Configurations](#configurations)
    *   [Permissions](#permissions)
    *   [Variables](#variables)
*   [Features](#features)
    *   [Plugins](#plugins)
    *   [File Uploads and Camera Access](#file-uploads-and-camera-access)
    *   [GPS Location](#gps-location)
    *   [Firebase Cloud Messaging](#firebase-cloud-messaging)
    *   [Google Analytics (gtag.js)](#google-analytics-gtagjs)
    *   [Chrome Tab Handling](#chrome-tab-handling)
    *   [Content Sharing](#content-sharing)
    *   [User Interfaces](#user-interfaces)
    *   [Rating System](#rating-system)
    *   [Downloads Handling](#downloads-handling)
    *   [Printing](#printing)
*   [Contributing](#contributing)
*   [Support the Project](#support-the-project)
*   [License](#license)
*   [Acknowledgements](#acknowledgements)
*   [Closing Note](#closing-note)

## Getting Started

These instructions will help you get your Smart WebView copy up and running on your local machine for development and testing purposes.

**Video Tutorial - [Getting Started with Smart WebView](https://www.youtube.com/watch?v=vE_GsHwspH4&list=PLUvke9lIV6YMGU5XdQ5zOtDOWxslsg6mT&pp=gAQBiAQB)**

### Prerequisites

Project is built on Android Studio and requires minimum Android API 23+ (6.0 Marshmallow) SDK to test run.

### Setup

1. **Download project files**
    *   (Recommended) Download latest Source code asset(s) from [releases](https://github.com/mgks/Android-SmartWebView/releases)
    *   Or simply clone the project (may include untested changes)

        `git clone https://github.com/mgks/Android-SmartWebView`

2. **Download `google-services.json` file from Firebase** ([instructions](https://github.com/mgks/Android-SmartWebView#firebase-messaging))

3. **Load project in Android Studio**

   `File > Open > Browse to Project and Select`

4. **Let Android Studio process the project and download supporting libraries and dependencies**

5. **Try `cleaning` and `rebuilding` the project before run**

   `Build > Clean Project` then `Build > Rebuild Project`

## Configurations

For detailed configuration, check project [Documentation](https://mgks.dev/blog/smart-webview-documentation#config).

### Permissions

You can remove any of the following permissions if your app does not require them. For first-time (default) setup, following permissions are required.

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    tools:ignore="ScopedStorage"
    android:maxSdkVersion="35" />
```

`INTERNET` permission is required for any webview to work.

`ACCESS_NETWORK_STATE` and `ACCESS_WIFI_STATE` are used for checking network and connection status.

`POST_NOTIFICATIONS` is required for push notifications using Firebase.

`CAMERA` is required for accessing camera and taking photos or videos; if you have `ASWP_CAMUPLOAD` enabled.

`ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` are required for accessing user location if `ASWP_LOCATION` is enabled.

`VIBRATE` is required for haptic feedback on long presses.

`READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`, and `READ_MEDIA_AUDIO` are required for accessing and selecting images and videos from the device (API 33+).

`WRITE_EXTERNAL_STORAGE` is required for saving downloaded files to the device (deprecated and should be removed if possible).

### Variables

You can set/change variables in `SmartWebView.java`.

```java
static boolean ASWP_OFFLINE       = ASWV_APP_URL.matches("^(file)://.*$") && Functions.isInternetAvailable(appContext);        // `true` if app loads from local file or no internet connection is available (DISABLES GPS, FIREBASE and other online features)

static boolean ASWP_FUPLOAD       = true;         // upload file from webview
static boolean ASWP_CAMUPLOAD     = true;         // enable upload from camera for photos
static boolean ASWP_MULFILE       = true;         // upload multiple files in webview
static boolean ASWP_LOCATION      = true;         // track GPS locations
static boolean ASWP_COPYPASTE     = false;        // enable copy/paste within webview
static boolean ASWP_RATINGS       = true;         // show ratings dialog; auto configured ; edit method get_rating() for customizations
static boolean ASWP_PULLFRESH     = true;         // pull refresh current url
static boolean ASWP_PBAR          = true;         // show progress bar in app
static boolean ASWP_ZOOM          = false;        // zoom control for webpages view
static boolean ASWP_SFORM         = false;        // save form cache and auto-fill information
static boolean ASWP_EXTURL        = true;         // open external url with default browser instead of app webview

static boolean ASWP_TAB           = true;         // instead of default browser, open external URLs in chrome tab

static boolean ASWP_EXITDIAL	  = true;         // confirm to exit app on back press
```

### Security

```java
static boolean ASWP_CERT_VERI     = true;         // verify whether HTTPS port needs certificate verification
```

### Other Variables

```java
static int ASWV_LAYOUT          = 0; // default=0; for clear fullscreen layout, and =1 for drawer layout

static String ASWV_URL          = ASWP_OFFLINE ? ASWV_OFFLINE_URL : ASWV_APP_URL; // finalising app URL to load
static String ASWV_SHARE_URL    = ASWV_URL + "?share="; // URL where you process external content shared with the app

// domains allowed to be opened inside webview
static String ASWV_EXC_LIST     = "mgks.dev,mgks.github.io,github.com"; //separate domains with a comma (,)

// custom user agent defaults
static boolean POSTFIX_USER_AGENT    = true; // set to true to append USER_AGENT_POSTFIX to user agent
static boolean OVERRIDE_USER_AGENT   = false; // set to true to use USER_AGENT instead of default one
static String USER_AGENT_POSTFIX     = "SWVAndroid"; // useful for identifying traffic, e.g. in Google Analytics
static String CUSTOM_USER_AGENT      = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36"; // custom user-agent

// config analytics
static String ASWV_GTAG     = "G-7XXC1C7CRQ"; // your unique analytics ID

// to upload any file type using "*/*"; check file type references for more
static String ASWV_F_TYPE   = "*/*";
```

## Features

### Plugins

Smart WebView now supports a powerful plugin architecture that allows you to extend the functionality of your app without modifying the core codebase. Plugins can self-register and interact with the `WebView` and `Activity` through a simplified interface.

#### Plugin Interface (`PluginInterface.java`)

```java
public interface PluginInterface {
    void initialize(Activity activity, WebView webView);
    String getPluginName();
    String[] getOverriddenUrls();
    void handlePermissionRequest(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    void handleActivityResult(int requestCode, int resultCode, Intent data);
    boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request);
    void onPageStarted(String url);
    void onPageFinished(String url);

    // Add more custom methods as needed for your plugin functionalities
    void onQRCodeScanResult(String result); // Method to handle QR code scan result
}
```

#### Plugin Manager (`PluginManager.java`)

The `PluginManager` handles the registration, initialization, and lifecycle events of plugins. It also provides methods for plugins to interact with the `WebView` and `Activity`.

#### Creating a Plugin

To create a new plugin, simply create a new Java class that implements the `PluginInterface` and add a static block to self-register the plugin:

```java
public class MyPlugin implements PluginInterface {

    static {
        try {
            PluginManager.registerPlugin("MyPlugin", new MyPlugin(), new String[]{"myplugin://"});
        } catch (Exception e) {
            Log.e("MyPlugin", "Failed to register MyPlugin", e);
        }
    }

    // Implement the PluginInterface methods
    // ...
}
```

#### Example Plugin: `PlugQRReader.java`

A sample QR code reader plugin is included to demonstrate the plugin architecture. This plugin:

*   Registers itself with the `PluginManager`.
*   Overrides the URL `qrscan://start` to trigger QR code scanning.
*   Uses the ZXing library to handle the scanning process.
*   Injects JavaScript into the WebView to add a "Scan QR Code" button on specific pages.
*   Handles the camera permission request.
*   Handles the QR code scanning result and updates the WebView accordingly.

### File Uploads and Camera Access

File uploads and camera access are handled by the `FileProcessing.java` class. It uses the modern `ActivityResultLauncher` approach to start activities for file selection and camera capture, and it handles the results asynchronously. The `onShowFileChooser()` method in `FileProcessing.java` is triggered when a file upload input is detected in the WebView.

#### MIME Type Handling

The `onShowFileChooser()` method dynamically sets the MIME type of the file chooser intent based on the `accept` attribute of the HTML input element, you can also set it to accept multiple files at once. This allows you to control which file types the user can select from within your web content.

```html
<!-- For images only -->
<input type="file" accept="image/*">

<!-- For videos only -->
<input type="file" accept="video/*">

<!-- For images and videos -->
<input type="file" accept="image/*,video/*">

<!-- For PDF files only -->
<input type="file" accept=".pdf">

<!-- For specific image formats -->
<input type="file" accept=".jpg,.jpeg,.png">

<!-- Multiple files -->
<input type="file" id="add-img" multiple="multiple">
```

### GPS Location

`ASWP_LOCATION` variable is used to enable or disable GPS Location. If enabled and when permission is granted, app will start requesting GPS coordinates on regular intervals and data is sent to the webpage in form of cookies.

```text
"lat" for latitude
"long" for longitude
```

### Firebase Cloud Messaging

Firebase Cloud Messaging (FCM) is integrated to enable push notifications. The `Firebase.java` file handles the receiving of new FCM tokens and incoming messages.

**Setup:**

1. Create a Firebase project in the [Firebase console](https://console.firebase.google.com/).
2. Add an Android app to your Firebase project and follow the instructions to download the `google-services.json` file.
3. Place the `google-services.json` file in the `app/` directory of your project.

**Sending Notifications:**

You can send notifications from the Firebase console or use the FCM API to send notifications from your server.

**Example POST request to FCM API:**

```text
To: https://fcm.googleapis.com/fcm/send

Headers:
content-type: application/json
authorization: key=__server_key_here__ (Firebase > Project settings > Cloud Messaging > Server key)

Data:
{
  "to": "__user_token__",
  "notification": {
    "title": "Notification Title",
    "body": "Notification Body",
    "click_action": "OPEN_URI"
  },
  "data": {
    "uri": "https://your-website.com/some-page"
  }
}
```

You can get the user token (`__user_token__`) from the `fcm_token` variable in your `SmartWebView` class.

### Google Analytics (gtag.js)

Google Analytics integration has been updated to dynamically inject the gtag.js script after the page has finished loading. This ensures that the analytics script doesn't interfere with the initial page load and that the `onPageFinished()` event is called reliably.

The `ASWV_GTAG` variable in `SmartWebView.java` should be set to your Google Analytics tracking ID.

### Chrome Tab Handling

Smart WebView can handle external links by opening them in a Chrome Custom Tab if the `ASWP_TAB` variable is set to `true`. This provides a more seamless browsing experience for users.

### Content Sharing

Users can share external content (text, links, images) with your Smart WebView app. The shared content can be received on the active webpage by providing an `ASWV_SHARE_URL` endpoint that can handle such requests. To disable this feature, remove the `ShareActivity` from your `AndroidManifest.xml`.

### User Interfaces

Smart WebView supports two layout modes:

*   **Fullscreen Layout:** The default layout (`ASWV_LAYOUT = 0`) provides a clean, fullscreen WebView experience.
*   **Drawer Layout:** Setting `ASWV_LAYOUT = 1` enables a drawer layout with a navigation bar and search option.

You can customize the drawer menu items in `onNavigationItemSelected()` and `activity_main_drawer.xml`.

### Rating System

The rating dialogue is enabled by default (`ASWP_RATINGS = true`) and can be configured with the following variables:

```
ASWR_DAYS       = 3;    // after how many days of usage would you like to show the dialog
ASWR_TIMES      = 10;   // overall request launch times being ignored
ASWR_INTERVAL   = 2;    // reminding users to rate after days interval
```

### Downloads Handling

Smart WebView automatically handles file downloads initiated from the WebView. Downloaded files are saved to the device's "Downloads" directory.

### Printing

The `print:` URL scheme can be used in hyperlinks to trigger the printing of the current page.

## Contributing

If you want to contribute to the project, you're most welcome to do so. Just:

*   Fork it
*   Create your feature branch `git checkout -b my-new-feature`
*   Commit your changes `git commit -am 'Added some feature'`
*   Push to the branch `git push origin my-new-feature`
*   Create new Pull Request

## Support the Project

**[GitHub Sponsors](https://github.com/sponsors/mgks):** Support this project and my other work by becoming a GitHub sponsor, it means a lot :)

**[Get Smart WebView Plugins](https://mgks.dev/blog/smart-webview#plugins):** You can also support this project by getting plugins made specifically for Smart WebView. Some of the features/plugins include - Google Login, QR/Barcode Reader, and more.

**[Join Discussions](https://github.com/mgks/Android-SmartWebView/discussions):** Feedbacks have helped this project become what it is today, share your honest feedback in [Discussion Board](https://github.com/mgks/Android-SmartWebView/discussions), [Email me](mailto:hello@mgks.dev) or report your project [Issues](https://github.com/mgks/Android-SmartWebView/issues) here.

**[Follow Me](https://github.com/mgks) on GitHub** | **Add Project to Watchlist** | **Star the Project**

## License

This project is published under the MIT License - see [LICENSE](LICENSE) file for details or read [MIT license](https://opensource.org/licenses/MIT).

## Acknowledgements

* Rating method [Android-Rate](https://github.com/hotchemi/Android-Rate) is developed by [hotchemi](https://github.com/hotchemi)

Thanks to other [contributors](https://github.com/mgks/Android-SmartWebView/graphs/contributors) who helped make this project amazing.

## Closing Note

This project was initially developed by **[Ghazi Khan](https://mgks.dev)**, but coming this far wouldn't be possible without the people who contributed to the project.

***For new developers:** Programming can be challenging at times, but with practice and persistence, you can develop the skills to create amazing things. The beauty of programming is that it empowers you to bring your ideas to life and create your own world. Keep exploring & experimenting, and all the best for your next project!*

<img src="https://forthebadge.com/images/badges/built-with-love.svg" alt="Built with Love">
