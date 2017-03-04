# Android Smart WebView
This project will help you create Smart Android applications on the go without much re-creating a whole app or learning JAVA or Android programming. You can embed your existing website or can develop a simple HTML based app.

Android Smart WebView gathers all necessary information needed to make an Advanced Android App with Location Tracking, File Uploads, Using Camera for Uploading Images, Custom Dialogues and Notifications.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Requirement
The project requires minimum Android API 16+ (4.1 JellyBean) SDK to test. And you can use any development software of your choice, I used Android Studio making this.

### Test Run
Try rebuilding the project on your programming environment, once you are done fixing any error (incase if one came up), you'll be ready to look into the project.

### Permissions
You can remove any of the following requests if you do not need them or you can disable any feature using easy setup variables.
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.VIBRATE" />
```
`INTERNET` permission is required if you are requesting a weburl or webpage and `WRITE_EXTERNAL_STORAGE` is required for camera photo creation, if you have enabled `ASWP_FUPLOAD` and `ASWP_CAMUPLOAD` to upload image files.

### Easy Setup
Once your project is ready here are some static variables you can change as per your Apps requirement.

#### Permission variables

```java
static boolean ASWP_JSCRIPT     = true;     //enable JavaScript for webview
static boolean ASWP_FUPLOAD     = true;     //upload file from webview
static boolean ASWP_CAMUPLOAD   = true;     //enable upload from camera for photos
static boolean ASWP_LOCATION    = true;     //track GPS locations
static boolean ASWP_RATINGS     = true;     //show ratings dialog; auto configured, edit method get_rating() for customizations
static boolean ASWP_PBAR        = true;     //show progress bar in app
static boolean ASWP_ZOOM        = false;    //zoom in webview
static boolean ASWP_SFORM       = false;    //save form cache and auto-fill information
static boolean ASWP_OFFLINE		= false;	//whether the loading webpages are offline or online
static boolean ASWP_EXTURL		= true;		//open external url with default browser instead of app webview
```

#### Configuration variables
Complete URL of your website, landing page or local file as (file:///android_res/dir/file.html)
```java
ASWV_URL      = "https://infeeds.com/@mgks";	//domain, or directory or locating to any root file
```
If file upload enabled, you can define its extention type, default is "\*/\*" for all file types;

Use "image/*" for image types; check file type references on web for more
```java
ASWV_F_TYPE   = "*/*";
```

## Getting GPS Location
If `ASWP_LOCATION = true` then the app will start requesting GPS locations of the device on regular basis and all of the recorded data will be sent to the webpage in terms of cookies, updating along with locations.
```java
COOKIE "lat" for latitude
COOKIE "long" for longitude
```

## Author
This project is initially developed by **Ghazi Khan**.

Public Profiles:
* [Infeeds](https://infeeds.com/@mgks)
* [GitHub](https://github.com/mgks)
* [Facebook](https://www.facebook.com/imgks)
* [Twitter](https://twitter.com/getmgks)

### Thanks to
* hotchemi [for Android-Rate script]


## License
This project is licensed under the MIT License - see [LICENSE.md](LICENSE.md) file for details or read [MIT license](https://opensource.org/licenses/MIT).


## Acknowledgment
Rating method used in this app is developed by another author, whom I was unable to find on github as files were non-credited. Thanks to the programmer who did it.

Post any issue if you got any problem handling the project and if you want to contribute, you're most welcome to help me make it smarter than what it is. Just drop me a mail at: [getmgks@gmail.com](mailto:getmgks@gmail.com)

**This project on Infeeds - [Android Smart WebView open source to upload files, get GPS locations and more advanced features](https://infeeds.com/d/CODEmgks/25019/android-smart-webview-open-source-upload)**

**A little note:** *You all must keep up with programming. It's sometimes difficult and sometimes easy but fun afterall, you can create your own world with programming, that's the beauty of it. So, all the best for your next creation.*
