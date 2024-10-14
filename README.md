<!--
 * Smart WebView 7.0
 * Smart WebView is an Open Source project that integrates native features into webview to help create advanced hybrid applications. Original source (https://github.com/mgks/Android-SmartWebView)
 * This program is free to use for private and commercial purposes under MIT License (https://opensource.org/licenses/MIT)
 * Join the discussion (https://github.com/mgks/Android-SmartWebView/discussions)
 * Support Smart WebView (https://github.com/sponsors/mgks)
 * Acknowledging project sources and developers helps them continue their valuable work. Thank you for your support :)
-->

<span align="center" style="text-align:center">

<a href="https://github.com/mgks/Android-SmartWebView/"><img src="https://raw.githubusercontent.com/mgks/Android-SmartWebView/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="100"></a>

<h1>Android Smart WebView</h1>

[![alt text](https://img.shields.io/badge/variant-java-red.svg)](#config-variables) [![alt text](https://img.shields.io/badge/version-7.0&nbsp;beta-green.svg)](https://github.com/mgks/Android-SmartWebView/releases) [![MIT Licence](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/mgks/Android-SmartWebView/blob/master/LICENSE)

</span><span align="center" style="text-align:center">

**[GETTING STARTED](#getting-started) &middot; [PLUGINS](https://mgks.dev/blog/smart-webview#plugins) &middot; [DOCUMENTATION](https://mgks.dev/blog/smart-webview-documentation#index) &middot; [ISSUES](https://github.com/mgks/Android-SmartWebView/issues)**

</span>

Smart WebView is a lightweight and powerful framework designed to streamline the development of advanced hybrid webview applications.

This easy-to-use solution provides a wide range of features that work seamlessly right out of the box, including GPS location tracking, FCM notifications, AdMob integration, chrome tabs, file uploads, camera input processing, downloads handler, custom rating systems, multiple UIs, deep linking, and error handling. Whether you're a seasoned developer or just starting out, Smart WebView offers a user-friendly approach to creating high-performance webview apps that your users will love.

## Table of Contents

* [Getting Started](#getting-started)
  * *[YouTube Playlist](https://www.youtube.com/watch?v=vE_GsHwspH4&list=PLUvke9lIV6YMGU5XdQ5zOtDOWxslsg6mT&pp=gAQBiAQB) (external link)*
  * [Prerequisites](#prerequisites)
  * [Setup](#setup)
* [Configurations](#configurations)
  * [Device Permissions](#device-permissions)
  * [Config Variables](#config-variables)
  * [Permission Variables](#permission-variables)
  * [Security Variables](#security-variables)
  * [Other Variables](#other-variables)
* [Features](#features)
  * [GPS Location](#gps-location)
  * [Camera Input](#camera-input)
  * [Firebase Messaging](#firebase-messaging)
  * [Google AdMob](#google-admob)
  * [Chrome Tab](#chrome-tab)
  * [Content Sharing](#content-sharing)
  * [User Interfaces](#user-interfaces)
  * [Rating System](#rating-system)
* [Contributing](#contributing)
* [License](#license)
* [Acknowledgements](#acknowledgements)
* [Contact](#closing-note)

## Getting Started

These instructions will help you get your Smart WebView copy up and running on your local machine for development and testing purposes.

**YouTube Playlist - [Getting Started with Smart WebView](https://www.youtube.com/watch?v=vE_GsHwspH4&list=PLUvke9lIV6YMGU5XdQ5zOtDOWxslsg6mT&pp=gAQBiAQB)**

### Prerequisites

Project is built on Android Studio and requires minimum Android API 21+ (5.0 Lollipop) SDK to test run.

### Setup

1. Download project files
   - (Recommended) Download latest Source code asset(s) from [releases](https://github.com/mgks/Android-SmartWebView/releases)
   - Or simply clone the project (may include untested changes)

       `git clone https://github.com/mgks/Android-SmartWebView`

2. Download `google-services.json` file from Firebase ([instructions](https://github.com/mgks/Android-SmartWebView#firebase-messaging))

3. Load project in Android Studio

    `File > Open > Browse to Project and Select`

4. Let Android Studio process the project and download supporting libraries and dependencies

5. Just to make sure, try `cleaning` and `rebuilding` the project before run

    `Build > Clean Project` then `Build > Rebuild Project`

6. Got any errors? You better fasten you seatbelt. It's going be a bumpy night.

## Configurations

For detailed configuration, check project [Documentation](https://mgks.dev/blog/smart-webview-documentation#config).

### Device Permissions

You can (or should) remove any of the following requests if your app does not require them, you can also disable features with permission variables. For first-time (default) setup, following permissions are required.

```xml
INTERNET
ACCESS_NETWORK_STATE
ACCESS_WIFI_STATE
WRITE_EXTERNAL_STORAGE
READ_EXTERNAL_STORAGE
CAMERA
ACCESS_FINE_LOCATION
VIBRATE
```
```xml
c2dm.permission.RECEIVE
```
```xml
hardware.location.gps
hardware.touchscreen
```

`INTERNET` permission is required for any webview to work.

`WRITE_EXTERNAL_STORAGE` is required for camera file processing: if you have `ASWP_FUPLOAD` and `ASWP_CAMUPLOAD` enabled to upload image/video files.

### Config Variables

You should use complete webpage URL or load local file as `file:///android_res/dir/file.html`

```kotlin
ASWV_URL   = "https://mgks.dev/"   // domain or directory or address to any root file
```

### Permission Variables

```kotlin
ASWP_JSCRIPT       = true;         // enable JavaScript for webview
ASWP_FUPLOAD       = true;         // upload file from webview
ASWP_CAMUPLOAD     = true;         // enable upload from camera for photos
ASWP_ONLYCAM       = false;        // incase you want only camera files to upload
ASWP_MULFILE       = true;         // upload multiple files in webview

ASWP_LOCATION      = true;         // track GPS locations

ASWP_COPYPASTE     = false;        // enable copy/paste within webview
ASWP_RATINGS       = true;         // show ratings dialog; auto configured ; edit method get_rating() for customizations
ASWP_PULLFRESH     = true;         // pull refresh current url
ASWP_PBAR          = true;         // show progress bar in app
ASWP_ZOOM          = false;        // zoom control for webpages view
ASWP_SFORM         = false;        // save form cache and auto-fill information

ASWP_OFFLINE       = false;        // whether the loading webpages are offline or online
ASWP_EXTURL        = true;         // open external url with default browser instead of app webview

ASWP_TAB           = true;         // instead of default browser, open external URLs in chrome tab
ASWP_ADMOB         = true;         // to load admob or not

ASWP_EXITDIAL      = true;         // confirm to exit app on back press
```

### Security Variables

```kotlin
ASWP_CERT_VERIFICATION   = true   // verify whether HTTPS port needs certificate verification
```

### Other Variables

```kotlin
ASWV_ORIENTATION = 0;   // change device orientation to portrait (1)(default) or landscape (2) or unspecified (0)
ASWV_LAYOUT      = 0;   // default=0; for clear fullscreen layout and 1 to add drawer and navigation bar

ASWV_SEARCH      = "https://www.google.com/search?q=";   // search query will start by the end of the present string
ASWV_SHARE_URL   = ASWV_URL+"?share=";   // URL where you process external content shared with the app
ASWV_EXC_LIST    = "github.com";       // domains allowed to be opened inside webview; separate domains with a comma (,)

ASWV_ADMOB       = "ca-app-pub-9276682923792397~7957851075";   // your unique publishers ID; this one is temporary

ASWV_F_TYPE      = "*/*"   // use `image/*` for image files only; check file type references for custom file type

POSTFIX_USER_AGENT      = true;     // set to true to append USER_AGENT_POSTFIX to user agent
OVERRIDE_USER_AGENT     = false;    // set to true to use USER_AGENT instead of default one
USER_AGENT_POSTFIX      = "SWVAndroid";    // useful for identifying traffic, e.g. in Google Analytics
CUSTOM_USER_AGENT       = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36";    // custom user-agent
```

## Features

### GPS Location

`ASWP_LOCATION` needs to be set `true` if your app needs GPS location coordinates, in this case app will start requesting GPS location data of the device on regular intervals (specifically when some fragment is changed or requested by the user). All of the recorded data will be sent to the active webpage in form of cookies, to store it in logs locally, try [PQL](https://github.com/mgks/PaperlessQL).

```kotlin
COOKIE "lat" for latitude
COOKIE "long" for longitude
```

### Camera Input

You have the flexibility to choose between selecting input from both the file manager and the camera, or just choosing a single input medium. Additionally, you can also specify a particular file type or even single or multiple files. To get video input, you can add a file type condition in your HTML input using `video/*` for videos and `image/*` for photos. By default, the `ASWV_F_TYPE` is set to `*/*`, and it is suggested not to change it. Instead, file types should be provided on the active web page's end.

### Firebase Messaging

You need a firebase account to get started. Then download `google-services.json` from firebase panel, and put it in the app level directory. Then you can create a POST request from your server end, like the sample below.
[Check detailed steps](https://support.google.com/firebase/answer/7015592?hl=en) to download firebase config file.

**To URL:** `https://fcm.googleapis.com/fcm/send`

**Header:**
```
content-type:application/json
authorization:key=__server_key_here__ (Firebase > Settings > Cloud Messaging)
```

**Data:**
```
{ "notification": {
    "title": "__title_string__",
    "text": "__text_string__",
     "click_action": "Open_URI"
  },
    "data": {
    "uri": "__the_URL_where_you_want_users_to_send__"
    },
  "to" : "__user_token__"
}
```

You can get `__user_token__` from COOKIE `fcm_token`.

### Google AdMob

Enable `ASWP_ADMOB` *(permission variable)* to show ads, and get your App ID from AdMob to replace `ASWV_ADMOB` *(config variable)*.

### Chrome Tab

With `ASWP_TAB` you can handle external link actions, whether to be opened in the chrome tab or external browser.

### Content Sharing

Users can share external content with your application including text, link or image. You can received data on active web page end by proving `ASWV_SHARE_URL` endpoint that can handle such requests. To disable this feature, you need to remove `ShareActivity` from application `Manifest`.

### User Interfaces

You can switch between clean and native interface with `ASWV_LAYOUT` where `0` represents full screen interface and `1` as drawer layout with search option and navigation bar.

To customize drawer bar, you can look into `onNavigationItemSelected()` method and `activity_main_drawer.xml` for menu items.

### Rating System

Rating dialogue is enabled by default and can be handled by `ASWP_RATINGS`.

```
ASWR_DAYS            = 3;           // after how many days of usage would you like to show the dialog
ASWR_TIMES           = 10;          // overall request launch times being ignored
ASWR_INTERVAL        = 2;           // reminding users to rate after days interval
```

## Contributing

If you want to contribute to the project, you're most welcome to do so. Just:

- Fork it
- Create your feature branch `git checkout -b my-new-feature`
- Commit your changes `git commit -am 'Added some feature'`
- Push to the branch `git push origin my-new-feature`
- Create new Pull Request

## Support the Project

<img src="https://raw.githubusercontent.com/mgks/Android-SmartWebView/master/res/swv_banner.jpg">

#### There are few ways to support the project -

**[GitHub Sponsors](https://github.com/sponsors/mgks):** Support this project and my other work by becoming a GitHub sponsor, it means a lot :)

**[Buy Smart WebView Plugins](https://mgks.dev/blog/smart-webview#plugins):** You can also support this project by getting plugins made specifically for Smart WebView. Some of the features/plugins include - PQL for local data storage, Google Login, QR/Barcode Reader, and more.

**[Join Discussions](https://github.com/mgks/Android-SmartWebView/discussions):** Feedbacks have helped this project become what it is today, share your honest feedback in [Discussion Board](https://github.com/mgks/Android-SmartWebView/discussions), [Email me](mailto:hello@mgks.dev) or report your project [Issues](https://github.com/mgks/Android-SmartWebView/issues) here.

**[Follow Me](https://github.com/mgks) on GitHub** | **Add Project to Watchlist** | **Star the Project**

### Reported Bugs
```
...
```
*Check [Issues](https://github.com/mgks/Android-SmartWebView/issues) tab for more.*

## License

This project is published under the MIT License - see [LICENSE](LICENSE) file for details or read [MIT license](https://opensource.org/licenses/MIT).


## Acknowledgements

- Rating method [Android-Rate](https://github.com/hotchemi/Android-Rate) is developed by [hotchemi](https://github.com/hotchemi)

Thanks to other [contributors](https://github.com/mgks/Android-SmartWebView/graphs/contributors) who helped make this project amazing.

## Closing Note

This project was initially developed by **[Ghazi Khan](https://mgks.dev)**, but coming this far wouldn't be possible without the people who contributed to the project.

***For new developers:** Programming can be challenging at times, but with practice and persistence, you can develop the skills to create amazing things. The beauty of programming is that it empowers you to bring your ideas to life and create your own world. Keep exploring & experimenting, and all the best for your next project!*

[![Profile](https://forthebadge.com/images/badges/built-with-love.svg)](https://mgks.dev)
