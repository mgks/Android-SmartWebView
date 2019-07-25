<!--
 * Android Smart WebView is an Open Source Project available on GitHub (https://github.com/mgks/Android-SmartWebView).
 * Initially developed by Ghazi Khan (https://github.com/mgks) under MIT Open Source License.
 * This program is free to use for private and commercial purposes.
 * Please mention project source or developer credit in your Application's License(s) Wiki.
 * Giving right credit to developers encourages them to create better projects :)
-->

<span align="center" style="text-align:center">

<a href="https://github.com/mgks/Android-SmartWebView/"><img src="https://raw.githubusercontent.com/mgks/Android-SmartWebView/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="100"></a>

[![alt text](https://img.shields.io/badge/variant-java-red.svg)](#environment-setup) [![alt text](https://img.shields.io/badge/version-p_%5F0.1-green.svg)](https://github.com/mgks/Android-SmartWebView/releases) [![MIT Licence](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/mit-license.php)

<h1>Android Smart WebView</h1>

</span><span align="center" style="text-align:center">

**[GETTING STARTED](#getting-started) &middot; [WIKI](https://github.com/mgks/Android-SmartWebView/wiki) &middot; [ISSUES](https://github.com/mgks/Android-SmartWebView/issues)**

</span>

SWV is a framework built on Java to develop advanced hybrid webview applications with ease.

A small build with features working out of the box: Live GPS Location, Notifications with FCM, AdMob, Chrome Tabs, Process Camera Input, Upload/Download Files, Custom Rating System, Multiple User Interfaces and more.

**For kotlin variant, see: [Kotlin Smart WebView](https://github.com/mgks/Kotlin-SmartWebView)**

## Table of Contents

* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Environment Setup](#environment-setup)
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
* [Contact](#contact)

## Getting Started
These instructions will help you get your Smart WebView copy up and running on your local machine for development and testing purposes.

**[Watch Explainer Video for Getting Started w/ SWV](https://www.youtube.com/watch?v=BM_5j-KAgoQ)** created by [Nate Harris](https://www.youtube.com/channel/UCuav96GscozuOSAx18r8b4g).

### Prerequisites
The project requires minimum Android API 21+ (5.0 Lollipop) SDK to test. I use Android Studio (latest release by time last update) for this.

### Environment Setup
Try cleaning and rebuilding the project in your programming environment, once you are done fixing any error (incase of one), you'll be ready to look into the project.

## Configurations

### Device Permissions
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

### Config Variables
Complete URL of your website, landing page or local file as `file:///android_res/dir/file.html`
```java
ASWV_URL      = "https://github.com/mgks"    //domain, or directory or locating to any root file
```

### Permission Variables
```java
ASWP_JSCRIPT     = true     //enable JavaScript for webview
ASWP_FUPLOAD     = true     //upload file from webview
ASWP_CAMUPLOAD   = true     //enable upload from camera for photos
ASWP_ONLYCAM     = false    //incase you want only camera files to upload
ASWP_MULFILE     = true     //upload multiple files in webview
ASWP_LOCATION    = true     //track GPS locations
ASWP_RATINGS     = true     //show ratings dialog; auto configured, edit method get_rating() for customizations
ASWP_PULLFRESH   = true     //pull refresh current url
ASWP_PBAR        = true     //show progress bar in app
ASWP_ZOOM        = false    //zoom control for webpages view
ASWP_SFORM       = false    //save form cache and auto-fill information
ASWP_OFFLINE     = false    //whether the loading webpages are offline or online
ASWP_EXTURL      = true     //open external url with default browser instead of app webview
```

### Security Variables
```java
ASWP_CERT_VERIFICATION   = true    //verify whether HTTPS port needs certificate verification
```

### Other Variables
If file upload enabled, you can define its extention type, default is `*/*` for all file types;

Use `image/*` for image types; check file type references on web for custom file type
```java
ASWV_F_TYPE   = "*/*"
```

## Features
### GPS Location
If `ASWP_LOCATION = true` then the app will start requesting GPS locations of the device on regular basis and all of the recorded data will be sent to the webpage in form of cookies, with updated live GPS locations.
```java
COOKIE "lat" for latitude
COOKIE "long" for longitude
```

### Camera Input

### Firebase Messaging

### Google AdMob

### Chrome Tab

### Content Sharing

### User Interfaces

### Rating System

## Contributing
If you want to contribute to the project, you're most welcome to do so. Just:
- Fork it
- Create your feature branch `git checkout -b my-new-feature`
- Commit your changes `git commit -am 'Added some feature'`
- Push to the branch `git push origin my-new-feature`
- Create new Pull Request

## Support the Project

#### There are few ways to support this project -

**DONATE:** If this project helped you or your business in any way and you feel like donating some change, you can always buy me a cup of coffee :)

<a href="https://ko-fi.com/Z8Z4BPQ6" target="_blank" title="Buy me a Coffee"><img width="150" style="border:0px;width:150px;display:block;margin:0 auto" src="https://az743702.vo.msecnd.net/cdn/kofi2.png?v=0" border="0" alt="Buy Me a Coffee at ko-fi.com" /></a>

Even your tiniest contribution will be appreciated. 

**PROVIDE FEEDBACK:** Donations help us run things but feedback helps us learn new things and understand you better. Without an honest feedback no good project is possible and your valuable feedbacks are always welcome: just drop an [email](mailto:getmgks@gmail.com).

Report your [issues](https://github.com/mgks/Kotlin-SmartWebView/issues) here.


## License
This project is published under the MIT License - see [LICENSE.md](LICENSE.md) file for details or read [MIT license](https://opensource.org/licenses/MIT).


## Acknowledgements
- Rating method [Android-Rate](https://github.com/hotchemi/Android-Rate) is developed by [hotchemi](https://github.com/hotchemi)

Thanks to other [contributers](https://github.com/mgks/Android-SmartWebView/graphs/contributors) who helped make this project amazing.

## Contact
This project was initially developed by **[Ghazi Khan](https://github.com/mgks)**, but coming this far wouldn't be possible without the people who contributed to this project.

**A personal note:** `You all must keep up with programming. It's sometimes difficult and sometimes easy but fun afterall, you can create your own world with programming and that's the beauty of it. So, all the best for your next creation.`

[![Profile](https://forthebadge.com/images/badges/built-with-love.svg)](https://github.com/mgks)
