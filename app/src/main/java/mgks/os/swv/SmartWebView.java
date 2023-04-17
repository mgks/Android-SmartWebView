package mgks.os.swv;

/*
 * Smart WebView is an Open Source project that integrates native features into webview to help create advanced hybrid applications. Available on GitHub (https://github.com/mgks/Android-SmartWebView).
 * Initially developed by Ghazi Khan (https://github.com/mgks) under MIT Open Source License.
 * This program is free to use for private and commercial purposes under MIT License (https://opensource.org/licenses/MIT).
 * Please mention project source or developer credits in your Application's License(s) Wiki.
 * Contribute to the project (https://github.com/mgks/Android-SmartWebView/discussions)
 * Sponsor the project (https://github.com/sponsors/mgks)
 * Giving right credits to developers encourages them to keep improving their projects :)
 */

import static mgks.os.swv.Functions.aswm_fcm_id;
import static mgks.os.swv.Functions.aswm_host;

import android.app.Notification;
import android.app.NotificationManager;
import android.net.Uri;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.lang.reflect.Field;

public class SmartWebView {

	public SmartWebView(){
		// smart webview constructor here
	}

	// permission variables
	static boolean ASWP_JSCRIPT       = true;         // enable JavaScript for webview
	static boolean ASWP_FUPLOAD       = true;         // upload file from webview
	static boolean ASWP_CAMUPLOAD     = true;         // enable upload from camera for photos
	static boolean ASWP_ONLYCAM       = false;        // incase you want only camera files to upload
	static boolean ASWP_MULFILE       = true;         // upload multiple files in webview
	static boolean ASWP_LOCATION      = true;         // track GPS locations
	static boolean ASWP_COPYPASTE     = false;        // enable copy/paste within webview
	static boolean ASWP_RATINGS       = true;         // show ratings dialog; auto configured ; edit method get_rating() for customizations
	static boolean ASWP_PULLFRESH     = true;         // pull refresh current url
	static boolean ASWP_PBAR          = true;         // show progress bar in app
	static boolean ASWP_ZOOM          = false;        // zoom control for webpages view
	static boolean ASWP_SFORM         = false;        // save form cache and auto-fill information
	static boolean ASWP_OFFLINE       = false;        // whether the loading webpages are offline or online
	static boolean ASWP_EXTURL        = true;         // open external url with default browser instead of app webview

	static boolean ASWP_TAB           = true;         // instead of default browser, open external URLs in chrome tab
	static boolean ASWP_ADMOB         = true;         // to load admob or not

	static boolean ASWP_EXITDIAL	  = true;         // confirm to exit app on back press

	// security variables
	static boolean ASWP_CERT_VERI     = false;         // verify whether HTTPS port needs certificate verification


	// config variables
	static int ASWV_ORIENTATION	  	  = 0;		      // change device orientation to portrait (1)(default) or landscape (2) or unspecified (0)

	// layout configs
	static int ASWV_LAYOUT            = 0;            // default=0; for clear fullscreen layout, and =1 for drawer layout

	// URL configs
	static String ASWV_URL_ONLINE	  = "https://apps.mgks.dev/swv/?android=true";	// if online URL is not provided, offline URL will be loaded by default
	static String ASWV_URL_OFFLINE	  = "file:///android_asset/offline.html";	// if ASWP_OFFLINE is set false or ASWV_URL_ONLINE is empty
	static String ASWV_URL            = ASWP_OFFLINE || (ASWV_URL_ONLINE == null || ASWV_URL_ONLINE.length() == 0) ? ASWV_URL_OFFLINE : ASWV_URL_ONLINE;	// complete URL of your website or offline webpage "file:///android_asset/offline.html";
	static String ASWV_SEARCH         = "https://www.google.com/search?q=";         // search query will start by the end of the present string
	static String ASWV_SHARE_URL      = ASWV_URL + "?share=";                       // URL where you process external content shared with the app

	// domains allowed to be opened inside webview
	static String ASWV_EXC_LIST       = "github.com";       //separate domains with a comma (,)

	// custom user agent defaults
	static boolean POSTFIX_USER_AGENT       = true;         // set to true to append USER_AGENT_POSTFIX to user agent
	static boolean OVERRIDE_USER_AGENT      = false;        // set to true to use USER_AGENT instead of default one
	static String USER_AGENT_POSTFIX        = "SWVAndroid"; // useful for identifying traffic, e.g. in Google Analytics
	static String CUSTOM_USER_AGENT         = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36";    // custom user-agent

	// to upload any file type using "*/*"; check file type references for more
	static String ASWV_F_TYPE         = "*/*";

	// admob config
	static String ASWV_ADMOB          = "";		// your unique publishers ID

	// rating config
	static int ASWR_DAYS      = 3;            // after how many days of usage would you like to show the dialog
	static int ASWR_TIMES     = 10;           // overall request launch times being ignored
	static int ASWR_INTERVAL  = 2;            // reminding users to rate after days interval

	/* -- following variables are used in MainActivity and Functions classes -- */
	// internal variable initialization
	static String TAG = MainActivity.class.getSimpleName();
	static String ASWV_HOST = aswm_host(ASWV_URL);
	static String asw_fcm_channel = "1";
	static String CURR_URL = ASWV_URL;
	static String fcm_token;
	static String asw_pcam_message;
	static String asw_vcam_message;

	static int ASWV_FCM_ID = aswm_fcm_id();
	static int asw_error_counter = 0;
	static int asw_file_req = 1;
	static int loc_perm = 1;
	static int file_perm = 2;

	static boolean true_online = !ASWP_OFFLINE;

	static WebView asw_view;
	static WebView print_view;
	static AdView asw_ad_view;
	static CookieManager cookie_manager;
	static ProgressBar asw_progress;
	static TextView asw_loading_text;
	static NotificationManager asw_notification;
	static Notification asw_notification_new;
	ValueCallback<Uri> asw_file_message;
	static ValueCallback<Uri[]> asw_file_path;

	public Object swv_get(String fieldName) throws NoSuchFieldException, IllegalAccessException {
		Field field = getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(this);
	}

	public boolean swv_set(String fieldName, Object value) {
		try {
			Field field = getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(this, value);
			return true;
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}
}
