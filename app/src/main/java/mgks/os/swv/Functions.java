package mgks.os.swv;

/*
  Smart WebView v7
  https://github.com/mgks/Android-SmartWebView

  A modern, open-source WebView wrapper for building advanced hybrid Android apps.
  Native features, modular plugins, and full customisation—built for developers.

  - Documentation: https://docs.mgks.dev/smart-webview  
  - Plugins: https://docs.mgks.dev/smart-webview/plugins  
  - Discussions: https://github.com/mgks/Android-SmartWebView/discussions  
  - Sponsor the Project: https://github.com/sponsors/mgks  

  MIT License — https://opensource.org/licenses/MIT  

  Mentioning Smart WebView in your project helps others find it and keeps the dev loop alive.
*/

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Field;
import java.math.BigInteger;

import java.security.SecureRandom;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.regex.Pattern;

public class Functions implements NavigationView.OnNavigationItemSelectedListener {
	private final SecureRandom random = new SecureRandom();

	// Random ID creation function to help get fresh cache every-time webview reloaded
	public String random_id() {
		return new BigInteger(130, random).toString(32);
	}

	// Printing the page in view
	static void print_page(WebView view, String print_name, Activity activityContext){
		// Create a PrintDocumentAdapter
		PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(print_name);

		// Create PrintAttributes (optional)
		PrintAttributes.Builder builder = new PrintAttributes.Builder();
		builder.setMediaSize(PrintAttributes.MediaSize.ISO_A5); // Customize as needed

		// Get the PrintManager service using the activity context
		PrintManager printManager = (PrintManager) activityContext.getSystemService(Context.PRINT_SERVICE);

		// Start the print job
		if (printManager != null) {
			PrintJob printJob = printManager.print(print_name, printAdapter, builder.build());

			// Monitor print job status (optional)
			if (printJob.isCompleted()) {
				Toast.makeText(activityContext, R.string.print_complete, Toast.LENGTH_LONG).show();
			} else if (printJob.isFailed()) {
				Toast.makeText(activityContext, R.string.print_failed, Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(activityContext, R.string.print_error, Toast.LENGTH_LONG).show();
		}
	}

	// Checking if internet/network is available
	public static boolean isInternetAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			Log.e("NetworkUtils", "ConnectivityManager is null");
			return false; // Handling the absence of ConnectivityManager as needed
		}
		Network network = connectivityManager.getActiveNetwork();
		if (network == null) {
			return false;
		}
		NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
		return capabilities != null &&
			(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
				capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
				capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
				capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
	}

	// Opening URLs inside webview with request
	void aswm_view(String url, Boolean tab, int error_counter, Context context) {
		if (error_counter > 2) {
			exit_app(context);
		} else {
			if (tab) {
				if (SmartWebView.ASWP_TAB) {
					CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
					intentBuilder.setStartAnimations(context.getApplicationContext(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					intentBuilder.setExitAnimations(context.getApplicationContext(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					CustomTabsIntent customTabsIntent = intentBuilder.build();
					try {
						customTabsIntent.launchUrl(context.getApplicationContext(), Uri.parse(url));
					} catch (ActivityNotFoundException e) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						context.startActivity(intent);
					}
				} else {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					context.startActivity(intent);
				}
			} else {
				// Check to see whether the url already has query parameters and handle appropriately
				url = url + (url.contains("?") ? "&" : "?") + "rid=" + random_id();
				SmartWebView.asw_view.loadUrl(url);
			}
		}
	}

	// Push JavaScript into webview
	public static void push_js(WebView view, String class_name, String html) {
		view.evaluateJavascript(
			"document.getElementsByClassName('" + class_name + "')[0].innerHTML = `" + html + "`;", null);
	}

	// Get data from webview DOM field
	public Object swv_get(String fieldName) throws NoSuchFieldException, IllegalAccessException {
		Field field = getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(this);
	}

	// Set data to webview DOM field
	public boolean swv_set(String fieldName, Object value) {
		try {
			Field field = getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(this, value);
			return true;
		} catch (NoSuchFieldException | IllegalAccessException e) {
			Log.e("ERROR", String.valueOf(e));
			return false;
		}
	}

	// URL actions based on URL structure
	public boolean url_actions(WebView view, String url, Activity activity) {
		boolean a = true;
		Context context = activity.getApplicationContext();

		// Show toast error if not connected to the network
		if (!SmartWebView.ASWP_OFFLINE && !isInternetAvailable(context)) {
			Toast.makeText(context, context.getString(R.string.check_connection), Toast.LENGTH_SHORT).show();

		// Redirect back to default URL :: refresh:android
		} else if (url.startsWith("refresh:")) {
			String ref_sch = (Uri.parse(url).toString()).replace("refresh:", "");
			if (ref_sch.matches("URL")) {
				SmartWebView.CURR_URL = SmartWebView.ASWV_URL;
			}
			pull_fresh(context);

		// Launch default phone dialer for specific number :: tel:+919876543210
		} else if (url.startsWith("tel:")) {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
			try {
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(context, "No dialer app found.", Toast.LENGTH_SHORT).show();
				Log.e("FCM_ERROR", "PORT_TEL", e);
			}

		// Open google play store app page :: rate:android
		} else if (url.startsWith("rate:")) {
			final String app_package = context.getPackageName(); // Requesting app package name from Context or Activity object
			try {
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_package)));
			} catch (ActivityNotFoundException anfe) {
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_package)));
			}

		// Sharing content from webview to external apps :: share:URL (link to be shared)
		} else if (url.startsWith("share:")) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, view.getTitle());
			intent.putExtra(Intent.EXTRA_TEXT, view.getTitle() + " Visit: " + (Uri.parse(url).toString()).replace("share:", ""));
			context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_w_friends)));

		// Exit app manually :: exit:android
		} else if (url.startsWith("exit:")) {
			exit_app(context);

		// Getting location for offline files
		} else if (url.startsWith("getloc:")) {
			String[] loc = get_location(context).split(",");
			push_js(SmartWebView.asw_view, "fetch-loc", "<br><b>Latitude: "+loc[0]+"<br>Longitude: "+loc[1]+"</b>");

			if(SmartWebView.SWV_DEBUGMODE) {
				Log.d("SLOG_OFFLINE_LOC_REQ", loc[0]+","+loc[1]);
			}

		// Creating firebase notification
		} else if (url.startsWith("fcm:")) {
			String title = null, body = null, nuri = null;

			// Manually parse parameters from fcm: URL
			String[] parts = url.substring(4).split("&"); // Remove "fcm:" and split by &
			for (String part : parts) {
				String[] keyValue = part.split("=");
				if (keyValue.length == 2) {
					String key = keyValue[0];
					String value = keyValue[1];
					switch (key) {
						case "title":
							title = value;
							break;
						case "body":
							body = value;
							break;
						case "uri":
							nuri = value;
							break;
					}
				}
			}

			// Set default values if not found
			if (title == null || title.isEmpty()) {
				title = "Hello Developer!";
			}
			if (body == null || body.isEmpty()) {
				body = "This is a test notification from Smart WebView.";
			}
			if (nuri == null || nuri.isEmpty()) {
				nuri = SmartWebView.ASWV_URL;
			}

			if(check_permission(4, context)) {
				// Send the notification
				Firebase firebase = new Firebase();
				firebase.sendMyNotification(title, body, "OPEN_URI", nuri, null, String.valueOf(SmartWebView.ASWV_FCM_ID), context);
			}else{
				get_permissions(4, activity);
			}

		// Opening external URLs in android default web browser
		} else if (SmartWebView.ASWP_EXTURL && !aswm_host(url).equals(SmartWebView.ASWV_HOST) && !SmartWebView.ASWV_EXC_LIST.contains(aswm_host(url))) {
			aswm_view(url, true, SmartWebView.asw_error_counter, context);

		// Setting device orientation on request
		} else if (url.startsWith("orient:")) {
			set_orientation(5, true, context);

		// Else return false
		} else {
			a = false;
		}
		return a;
	}

	// Getting host name
	public static String aswm_host(String url) {
		if (url == null || url.isEmpty()) {
			return "";
		}
		int dslash = url.indexOf("//");
		if (dslash == -1) {
			dslash = 0;
		} else {
			dslash += 2;
		}
		int end = url.indexOf('/', dslash);
		end = end >= 0 ? end : url.length();
		int port = url.indexOf(':', dslash);
		end = (port > 0 && port < end) ? port : end;
		Log.i("SLOG_URL_HOST", url.substring(dslash, end));
		return url.substring(dslash, end);
	}

	// Reloading current page
	public void pull_fresh(Context context) {
		aswm_view((!SmartWebView.CURR_URL.isEmpty() ? SmartWebView.CURR_URL : SmartWebView.ASWV_URL), false, SmartWebView.asw_error_counter, context);
	}

	// Changing port view
	@SuppressLint("SourceLockedOrientationActivity")
	public void set_orientation(int orientation, boolean cookie, Context context) { // setting the view port var
		if (context instanceof Activity activity) {
			if (orientation == 1) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else if (orientation == 2) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else if (orientation == 5) { //experimental switch
				SmartWebView.ASWV_ORIENTATION = (SmartWebView.ASWV_ORIENTATION == 1 ? 2 : 1);
			} else {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
			if (cookie) {
				set_cookie("ORIENT=" + orientation);
			}
		}
	}

	// Setting cookies
	public void set_cookie(String data) {
		if(SmartWebView.true_online) {
			// Cookie manager initialisation
			SmartWebView.cookie_manager = CookieManager.getInstance();
			SmartWebView.cookie_manager.setAcceptCookie(true);
			SmartWebView.cookie_manager.setCookie(SmartWebView.ASWV_URL, data);
			if(SmartWebView.SWV_DEBUGMODE) {
				Log.d("SLOG_COOKIES", SmartWebView.cookie_manager.getCookie(SmartWebView.ASWV_URL));
			}
		}
	}

	// Getting device basic information
	public String[] get_info() {
		String[] info = new String[3];
		info[0] = "android";
		info[1] = new MetaPull().device();
		info[2] = new MetaPull().swv();

		set_cookie("DEVICE_TYPE=" + info[0]);
		set_cookie("DEVICE_INFO=" + info[1]);
		set_cookie("APP_INFO=" + info[2]);

		return info;
	}

	// Using cookies to update user locations
	public String get_location(Context context) {
		String new_loc = "0,0";

		// Check for the user's preference
		if (!SmartWebView.ASWP_LOCATION) {
			Log.d("SmartWebView", "Location access is disabled by the user.");
			return new_loc; // Or return a message indicating location is disabled
		}

		// Check for location permissions
		if (check_permission(1, context)) {
			GPSTrack gps;
			gps = new GPSTrack(context);
			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			if (gps.canGetLocation()) {
				if (latitude != 0 || longitude != 0) {
					if (SmartWebView.true_online) {
						set_cookie("lat=" + latitude);
						set_cookie("long=" + longitude);
						set_cookie("LATLANG=" + latitude + "x" + longitude);
					}
					new_loc = latitude + "," + longitude;
					if (SmartWebView.SWV_DEBUGMODE) {
						Log.d("SLOG_NEW_LOCATION", new_loc);
					}
				} else {
					if (SmartWebView.SWV_DEBUGMODE) {
						Log.d("SLOG_UPDATED_LOCATION", "NULL");
					}
				}
			} else {
				// Handle the case where location is not available
				Log.d("SmartWebView", "Cannot get location. GPS turned off or service not available.");
				// You can show a message to the user here if you want
			}
		} else {
			// Handle the case where location permissions are not granted
			Log.d("SmartWebView", "Location permission not granted.");
			// You can show a rationale and request permissions again if needed
			show_notification(1, 1, context);
		}
		return new_loc;
	}

	// Get cookie value
	public String get_cookies(String cookie) {
		String value = "";
		if(SmartWebView.true_online) {
			SmartWebView.cookie_manager = CookieManager.getInstance();
			String cookies = SmartWebView.cookie_manager.getCookie(SmartWebView.ASWV_URL);
			if (cookies !=null && !cookies.isEmpty()) {
				String[] temp = cookies.split(";");
				for (String ar1 : temp) {
					if (ar1.contains(cookie)) {
						String[] temp1 = ar1.split("=");
						value = temp1[1];
						break;
					}
				}
			}else{
				value = "";
				if(SmartWebView.SWV_DEBUGMODE) {
					Log.d("SLOG_COOKIES", "Cookies either NULL or Empty");
				}
			}
		}else{
			Log.w("SLOG_NETWORK","DEVICE NOT ONLINE");
		}
		return value;
	}

	// Divide the URL pattern into pieces
	public static Pattern url_pattern() {
		return Pattern.compile("(?:^|\\W)((ht|f)tp(s?)://|www\\.)" + "(([\\w\\-]+\\.)+([\\w\\-.~]+/?)*" + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]*$~@!:/{};']*)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	}

	// Options menu for drawer theme
	@SuppressLint("ResourceAsColor")
	public boolean onCreateOptionsMenu(Menu menu, Activity context) {
		// Inflate the menu; this adds items to the action bar if it is present.
		context.getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		if (searchView != null) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(context.getComponentName()));
		}
		if (searchView != null) {
			searchView.setQueryHint(context.getString(R.string.search_hint));
		}
		assert searchView != null;
		searchView.setIconified(true);
		searchView.setIconifiedByDefault(true);
		searchView.clearFocus();

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			public boolean onQueryTextSubmit(String query) {
				searchView.clearFocus();
				aswm_view(SmartWebView.ASWV_SEARCH + query, false, SmartWebView.asw_error_counter, context.getApplicationContext());
				searchView.setQuery(query, false);
				return false;
			}

			public boolean onQueryTextChange(String query) {
				return false;
			}
		});
		//searchView.setQuery(SmartWebView.asw_view.getUrl(),false);
		return true;
	}

	// Options trigger for drawer theme
	public boolean onOptionsItemSelected(MenuItem item, Context context) {
		int id = item.getItemId();
		if (id == R.id.action_exit) {
			exit_app(context);
			return true;
		}
		return onOptionsItemSelected(item, context);
	}

	public interface TokenCallback {
		void onTokenReceived(String token);
		void onTokenFailed(Exception e);
	}

	// Get fresh firebase tokens
	public void fcm_token(final TokenCallback callback) {
		FirebaseMessaging.getInstance().getToken()
			.addOnSuccessListener(token -> {
				if (!SmartWebView.ASWP_OFFLINE) {
					set_cookie("FCM_TOKEN=" + token);
					if (SmartWebView.SWV_DEBUGMODE) {
						Log.d("SLOG_FCM_BAKED", "YES");
						Log.d("SLOG_COOKIES", get_cookies(SmartWebView.ASWV_URL));
					}
				}
				SmartWebView.fcm_token = token;
				if (SmartWebView.SWV_DEBUGMODE) {
					Log.d("SLOG_REQ_FCM_TOKEN", token);
				}
				callback.onTokenReceived(token); // Pass token to callback
			})
			.addOnFailureListener(e -> {
				SmartWebView.fcm_token = "";
				Log.e("SLOG_REQ_FCM_TOKEN", "FAILED", e);
				callback.onTokenFailed(e); // Pass exception to callback
			});
	}

	// Injecting Google Analytics (gtag.js)
	public void inject_gtag(WebView webView, String gaId) {
		String gtag_code = "function load_gtag(){var script = document.createElement('script');script.async = true;script.src = 'https://www.googletagmanager.com/gtag/js?id=" + gaId + "';var firstScript = document.getElementsByTagName('script')[0];firstScript.parentNode.insertBefore(script, firstScript);window.dataLayer = window.dataLayer || [];function gtag(){dataLayer.push(arguments);}gtag('js', new Date());gtag('config', '" + gaId + "');console.log('Google Analytics (gtag.js) loaded.');} load_gtag();";
		webView.evaluateJavascript(gtag_code, null);
	}

	// Checking if particular permission is given or not
	public boolean check_permission(int permission, Context context) {
		return switch (permission) {
			case 1 ->
				ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
			case 2 ->
				ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
			case 3 ->
				ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
			case 4 ->
				Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
			default -> false;
		};
	}

	// Get permissions for different activities
	public void get_permissions(int req, Activity activity) {
		if (req == 1 && !check_permission(1, activity)) {
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, SmartWebView.loc_perm);
		} else if (req == 2 && !check_permission(2, activity)) {
			// Checking for storage permission
			if (Build.VERSION.SDK_INT >= 33) {
				ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, SmartWebView.file_perm);
			} else {
				ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SmartWebView.file_perm);
			}
		} else if (req == 3 && !check_permission(3, activity)) {
			// Checking for camera permission
			if (Build.VERSION.SDK_INT >= 33) {
				// Camera permission is still needed for capturing from camera
				ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, SmartWebView.cam_perm);
			} else {
				ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, SmartWebView.cam_perm);
			}
		} else if (req == 4 && Build.VERSION.SDK_INT >= 33 && !check_permission(4, activity)) {
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, SmartWebView.noti_perm);
		}
	}

	// Launching app rating dialog [developed by github.com/hotchemi]
	public Runnable get_rating(Context context) {
		if (isInternetAvailable(context)) {
			AppRate.with(context)
				.setInstallDays(SmartWebView.ASWR_DAYS)
				.setLaunchTimes(SmartWebView.ASWR_TIMES)
				.setRemindInterval(SmartWebView.ASWR_INTERVAL)
				.setTitle(R.string.rate_dialog_title)
				.setMessage(R.string.rate_dialog_message)
				.setTextLater(R.string.rate_dialog_cancel)
				.setTextNever(R.string.rate_dialog_no)
				.setTextRateNow(R.string.rate_dialog_ok)
				.monitor();
			AppRate.showRateDialogIfMeetsConditions(context);
		}
		return null;
	}

	// Creating custom notifications with IDs
	public void show_notification(int type, int id, Context context) {
		long when = System.currentTimeMillis();
		String cont_title = "", cont_text = "", cont_desc = "";

		SmartWebView.asw_notification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent i = new Intent();
		if (type == 1) {
			i.setClass(context, MainActivity.class);
		} else if (type == 2) {
			i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		} else {
			i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			i.addCategory(Intent.CATEGORY_DEFAULT);
			i.setData(Uri.parse("package:" + context.getPackageName()));
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		}
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent;
		final int flag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
		pendingIntent = PendingIntent.getActivity(context, 0, i, flag);
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "");
		builder.setTicker(context.getString(R.string.app_name));
		switch (type) {
			case 1:
				cont_title = context.getString(R.string.loc_fail);
				cont_text = context.getString(R.string.loc_fail_text);
				cont_desc = context.getString(R.string.loc_fail_more);
				break;

			case 2:
				cont_title = context.getString(R.string.loc_perm);
				cont_text = context.getString(R.string.loc_perm_text);
				cont_desc = context.getString(R.string.loc_perm_more);
				builder.setSound(alarmSound);
				break;
		}
		builder.setContentTitle(cont_title);
		builder.setContentText(cont_text);
		builder.setStyle(new NotificationCompat.BigTextStyle().bigText(cont_desc));
		builder.setVibrate(new long[]{350, 700, 350, 700, 350});
		builder.setSmallIcon(R.mipmap.ic_launcher);
		builder.setOngoing(false);
		builder.setAutoCancel(true);
		builder.setWhen(when);
		builder.setContentIntent(pendingIntent);
		SmartWebView.asw_notification_new = builder.build();
		SmartWebView.asw_notification.notify(id, SmartWebView.asw_notification_new);
	}

	// Exit app
	public void exit_app(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	// Creating exit dialogue
	public void ask_exit(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.exit_title));
		builder.setMessage(context.getString(R.string.exit_subtitle));
		builder.setCancelable(true);

		// Action if user selects 'yes'
		builder.setPositiveButton("Yes", (dialogInterface, i) -> exit_app(context));

		// Actions if user selects 'no'
		builder.setNegativeButton("No", (dialogInterface, i) -> {});

		// Create the alert dialog using alert dialog builder
		AlertDialog dialog = builder.create();

		// Finally, display the dialog when user press back button
		dialog.show();
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		Context context = SmartWebView.getAppContext(); // Use the getAppContext method

		if (id == R.id.nav_home) {
			aswm_view("https://mgks.github.io/Android-SmartWebView/", false, 0, context);
		} else if (id == R.id.nav_doc) {
			aswm_view("https://mgks.dev/app/smart-webview-documentation/#config", false, 0, context);
		} else if (id == R.id.nav_plugins) {
			aswm_view("https://mgks.dev/app/smart-webview-documentation/#plugins", false, 0, context);
		} else if (id == R.id.nav_fcm) {
			aswm_view("https://mgks.dev/app/smart-webview-documentation/#push-notifications", false, 0, context);
		} else if (id == R.id.nav_gps) {
			aswm_view("https://mgks.dev/app/smart-webview-documentation/#geolocation", false, 0, context);
		} else if (id == R.id.nav_url_handling) {
			aswm_view("https://mgks.dev/app/smart-webview-documentation/#url-handling", false, 0, context);
		} else if (id == R.id.nav_changelog) {
			aswm_view("https://mgks.dev/app/smart-webview-documentation/#changelog", false, 0, context);
		} else if (id == R.id.nav_support) {
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("mailto:hello@mgks.dev"));
			intent.putExtra(Intent.EXTRA_SUBJECT, "Android Smart WebView Help");
			// Use try-catch to handle ActivityNotFoundException
			try {
				context.startActivity(Intent.createChooser(intent, "Send Email"));
			} catch (ActivityNotFoundException e) {
				Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show();
			}
		}

		// Close the drawer after handling the click
		if (SmartWebView.asw_view !=null && SmartWebView.ASWV_LAYOUT == 1) { //check if drawer is enabled
			DrawerLayout drawer = ((Activity) context).findViewById(R.id.drawer_layout);
			if(drawer != null) { //drawer may not be initialized
				drawer.closeDrawer(GravityCompat.START);
			}
		}
		return true;
	}
}

