<a href="https://github.com/mgks/Android-SmartWebView/"><img src="https://raw.githubusercontent.com/mgks/Android-SmartWebView/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="65"></a>

# Android Smart WebView
[![alt text](https://img.shields.io/badge/project%20variant-java-red.svg)](#) [![alt text](https://img.shields.io/badge/version-4-green.svg)](https://github.com/mgks/Android-SmartWebView/releases) [![MIT Licence](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/mit-license.php) [![alt text](https://img.shields.io/badge/learn%20about-SWV%20Pro-yellow.svg "Get Smart WebView Pro")](https://github.com/voinsource/SmartWebView-Pro)

**For kotlin variant, see: [Kotlin Smart WebView](https://github.com/mgks/Kotlin-SmartWebView)**

This project is developed to help anyone create hybrid android applications with just webview. This app gives web based applications power to use native android features without hassle, whether online or offline w/ just HTML+JavaScript.

Android Smart WebView gathers only required information from user's device based on request, that includes Live GPS location, File upload, Camera image processing, Rating system and more, with clean minimal interface.

## Getting Started
These instructions will help you get your Smart WebView copy up and running on your local machine for development and testing purposes.

**[Watch Explainer Video for Getting Started w/ SWV](https://www.youtube.com/watch?v=BM_5j-KAgoQ)** created by [Nate Harris](https://www.youtube.com/channel/UCuav96GscozuOSAx18r8b4g).

### Requirement
The project requires minimum Android API 21+ (5.0 Lollipop) SDK to test. I use Android Studio (latest release by time last update) for this.

### Test Run
Try cleaning and rebuilding the project in your programming environment, once you are done fixing any error (incase of one), you'll be ready to look into the project.

### Permissions
You can remove any of the following requests if you do not need them or you can disable any feature using easy setup variables. Currently, these permissions are must for default variables to work properly.
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.VIBRATE" />
```
`INTERNET` permission is required if you are requesting a weburl or webpage.
`WRITE_EXTERNAL_STORAGE` is required for camera photo creation, if you have enabled `ASWP_FUPLOAD` and `ASWP_CAMUPLOAD` to upload image files.

### Easy Setup
Once your project is ready, here are some important config variables that you can adjust as per your app requirements.

#### Permission variables
```kotlin
static boolean ASWP_JSCRIPT     = true     //enable JavaScript for webview
static boolean ASWP_FUPLOAD     = true     //upload file from webview
static boolean ASWP_CAMUPLOAD   = true     //enable upload from camera for photos
static boolean ASWP_ONLYCAM     = false    //incase you want only camera files to upload
static boolean ASWP_MULFILE     = true     //upload multiple files in webview
static boolean ASWP_LOCATION    = true     //track GPS locations
static boolean ASWP_RATINGS     = true     //show ratings dialog; auto configured, edit method get_rating() for customizations
static boolean ASWP_PULLFRESH   = true     //pull refresh current url
static boolean ASWP_PBAR        = true     //show progress bar in app
static boolean ASWP_ZOOM        = false    //zoom control for webpages view
static boolean ASWP_SFORM       = false    //save form cache and auto-fill information
static boolean ASWP_OFFLINE     = false    //whether the loading webpages are offline or online
static boolean ASWP_EXTURL      = true     //open external url with default browser instead of app webview
```
#### Security variables
```kotlin
static boolean ASWP_CERT_VERIFICATION   = true    //verify whether HTTPS port needs certificate verification
```
#### Configuration variables
Complete URL of your website, landing page or local file as `file:///android_res/dir/file.html`
```kotlin
ASWV_URL      = "https://github.com/mgks"    //domain, or directory or locating to any root file
```

If file upload enabled, you can define its extention type, default is `*/*` for all file types;

Use `image/*` for image types; check file type references on web for custom file type
```kotlin
ASWV_F_TYPE   = "*/*"
```

## Getting GPS Location
If `ASWP_LOCATION = true` then the app will start requesting GPS locations of the device on regular basis and all of the recorded data will be sent to the webpage in form of cookies, with updated live GPS locations.
```kotlin
COOKIE "lat" for latitude
COOKIE "long" for longitude
```

## Want to support this project?
**UPDATE: I've decided to merge Smart WebView Pro with original open source after 15 copies sold of Pro.**

`Currently sold: 6 (9 to go) *I'll keep this space updated`
<hr>

#### There are few ways you can support this project -

**- Donating:** Even your tiniest contribution will be appreciate. If this project helped you or your business in any way and you feel like donating some change, you can always buy me a coffee :)

<a href="https://ko-fi.com/Z8Z4BPQ6" target="_blank" title="Buy me a Coffee"><img width="150" style="border:0px;width:150px;display:block;margin:0 auto" src="https://az743702.vo.msecnd.net/cdn/kofi2.png?v=0" border="0" alt="Buy Me a Coffee at ko-fi.com" /></a>

**- Buying SWV Pro:** Dozens of amazing additional features including firebase notifications, admob, chromium tabs for external links and mutiple layouts.

**[Get Smart WebView Pro](https://voinsource.github.io/SmartWebView-Pro/)**

**- Feeback:** Donations are definitely not everything. Without an honest feedback no good project is possible. Your valuable feedbacks are always welcome, just drop an [email](mailto:getmgks@gmail.com).<br />Report your [issues](https://github.com/mgks/Kotlin-SmartWebView/issues) here.

#### More about Smart WebView Pro
**[SWV Pro](https://voinsource.github.io/SmartWebView-Pro/) is a commercial app built for small and medium level businesses, easy to implement with any existing environment.**

```
PRO FEATURES:
- Firebase Push Notifications
- Google AdMob
- Chromium Tab for External URLs
- Navigation Drawer
- Search Bar
- Action Menu
- And other customizations
```

*To Get Smart WebView Pro's Updates, Demo and Documentation, follow [SmartWebView-Pro](https://github.com/voinsource/SmartWebView-Pro) repo.*

```
NOTE: All public contributions will always be a part of this open source here.
SWV Pro just contains some additional features that have been implemented only by me to make the app more commercial oriented which helps me keep this repo updated.
```

## License
This project is licensed under the MIT License - see [LICENSE.md](LICENSE.md) file for details or read [MIT license](https://opensource.org/licenses/MIT).

## Acknowledgment
Rating method (Android-Rate) used in this app is developed by [hotchemi](https://github.com/hotchemi) and thanks to other programmers who contributed to this project.

Post in Github Repo issues section if you got any problem handling the project and if you want to contribute, you're most welcome to help me make a smarter project than what it is.

**A personal note:** `You all must keep up with programming. It's sometimes difficult and sometimes easy but fun afterall, you can create your own world with programming and that's the beauty of it. So, all the best for your next creation.`

This project is initially developed by **[Ghazi Khan](https://github.com/mgks)**.

[![Profile](https://forthebadge.com/images/badges/built-with-love.svg)](https://github.com/mgks)
