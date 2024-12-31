package mgks.os.swv;

/*
  Smart WebView 7.0

  MIT License (https://opensource.org/licenses/MIT)

  Smart WebView is an Open Source project that integrates native features into
  WebView to help create advanced hybrid applications (https://github.com/mgks/Android-SmartWebView).

  Explore plugins and enhanced capabilities: (https://mgks.dev/blog/smart-webview-plugins)
  Join the discussion: (https://github.com/mgks/Android-SmartWebView/discussions)
  Support Smart WebView: (https://github.com/sponsors/mgks)

  Your support and acknowledgment of the project's source are greatly appreciated.
  Giving credit to developers encourages them to create better projects.
*/

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SmartWebView {

	public SmartWebView(){
		// Smart webview constructor here
	}

	private static Context appContext; // application context
	public static void setAppContext(Context context) {
		appContext = context.getApplicationContext(); // store context in attachBaseContext for robustness and consistency
	}

	// DEBUG MODE (set to `false` for production apps)
	static boolean SWV_DEBUGMODE	  = true;	// enable debug mode for detailed reports in log and toast alerts for errors and warnings

	// URL configs
	static String ASWV_APP_URL	  	  = "https://mgks.github.io/Android-SmartWebView/";	// default app URL (web or file address)
	static String ASWV_OFFLINE_URL	  = "file:///android_asset/offline.html";	// default app address if ASWP_OFFLINE is set `true` OR ASWV_APP_URL is empty; basically a fail-safe page with no online features
	static String ASWV_SEARCH         = "https://www.google.com/search?q=";         // search query will start by the end of the present string

	// Permission variables
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

	// Security variables
	static boolean ASWP_CERT_VERI	  = true;		  // verify SSL certificate (Recommended: Keep this true for security)

	// Config variables
	static int ASWV_ORIENTATION	  	  = 0;		      // change device orientation to portrait (1)(default) or landscape (2) or unspecified (0)

	// Layout configs
	static int ASWV_LAYOUT            = 0;            // default=0; for clear fullscreen layout, and =1 for drawer layout

	static String ASWV_URL            = ASWP_OFFLINE ? ASWV_OFFLINE_URL : ASWV_APP_URL;	// finalising app URL to load
	static String ASWV_SHARE_URL      = ASWV_URL + "?share=";                       // URL where you process external content shared with the app

	// Domains allowed to be opened inside webview
	static String ASWV_EXC_LIST       = "mgks.dev,mgks.github.io,github.com";       //separate domains with a comma (,)

	// Custom user agent defaults
	static boolean POSTFIX_USER_AGENT       = true;         // set to true to append USER_AGENT_POSTFIX to user agent
	static boolean OVERRIDE_USER_AGENT      = false;        // set to true to use USER_AGENT instead of default one
	static String USER_AGENT_POSTFIX        = "SWVAndroid"; // useful for identifying traffic, e.g. in Google Analytics
	static String CUSTOM_USER_AGENT         = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36";    // custom user-agent

	// Upload any file type using "*/*"; check file type references for more
	static String ASWV_F_TYPE         = "*/*";

	// Config analytics
	static String ASWV_GTAG           = "7XXC1C7CRQ";		// your unique analytics ID

	// Rating config
	static int ASWR_DAYS      = 3;            // after how many days of usage would you like to show the dialog
	static int ASWR_TIMES     = 10;           // overall request launch times being ignored
	static int ASWR_INTERVAL  = 2;            // reminding users to rate after days interval

	/* -- Following variables are used in MainActivity and Functions classes -- */
	// Internal variable initialization
	static String ASWV_HOST = Functions.aswm_host(ASWV_URL);
	static String CURR_URL = ASWV_URL;
	static String fcm_token;
	static String asw_pcam_message;
	static String asw_vcam_message;
	static String asw_fcm_channel = "1";

	static int ASWV_FCM_ID = (int) System.currentTimeMillis();
	static int asw_error_counter = 0;

	static int loc_perm = 1;
	static int file_perm = 2;
	static int cam_perm = 3;
	static int noti_perm = 4;

	static boolean true_online = !ASWP_OFFLINE;

	static WebView asw_view;
	static WebView print_view;
	static CookieManager cookie_manager;
	static ProgressBar asw_progress;
	static TextView asw_loading_text;
	static NotificationManager asw_notification;
	static Notification asw_notification_new;
	static ValueCallback<Uri[]> asw_file_path;
}
