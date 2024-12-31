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

import android.Manifest;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.net.Uri;

import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.ServiceWorkerClient;
import android.webkit.ServiceWorkerController;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity {
	ActivityResultLauncher<Intent> act_result_launcher;
	static Functions fns = new Functions();
	private FileProcessing fileProcessing;
	private PluginManager pluginManager;

	private static final int PERMISSION_REQUEST_CODE = 1001; // You can use any unique integer

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		// Forward the result to the PluginManager
		pluginManager.onActivityResult(requestCode, resultCode, intent);
	}

	@SuppressLint({"SetJavaScriptEnabled", "WrongViewCast", "JavascriptInterface"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SmartWebView.setAppContext(getApplicationContext());

		fileProcessing = new FileProcessing(this);
		pluginManager = new PluginManager(this, SmartWebView.asw_view);

		fns.fcm_token(new Functions.TokenCallback() {
			@Override
			public void onTokenReceived(String token) {
				Log.d("MainActivity_FCM_TOKEN", "Received token: " + token);
				// You can now use the token (e.g., send it to your server)
			}

			@Override
			public void onTokenFailed(Exception e) {
				// Handle the failure here
				Log.e("MainActivity_FCM_TOKEN", "Failed to retrieve token", e);
			}
		});

		act_result_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			Uri[] results = null;
			if (result.getResultCode() == Activity.RESULT_CANCELED) {
				// If the file request was cancelled (i.e. user exited camera), we must still send a null value in order to ensure that future attempts to pick files will still work
				if (SmartWebView.asw_file_path != null) {
					SmartWebView.asw_file_path.onReceiveValue(null);
					SmartWebView.asw_file_path = null;
				}
				return;

			} else if (result.getResultCode() == Activity.RESULT_OK) {
				if (null == SmartWebView.asw_file_path) {
					return;
				}
				ClipData clipData;
				String stringData;
				try {
					assert result.getData() != null;
					clipData = result.getData().getClipData();
					stringData = result.getData().getDataString();
				} catch (Exception e) {
					clipData = null;
					stringData = null;
				}

				if (clipData == null && stringData == null && (SmartWebView.asw_pcam_message != null || SmartWebView.asw_vcam_message != null)) {
					results = new Uri[]{Uri.parse(SmartWebView.asw_pcam_message != null ? SmartWebView.asw_pcam_message : SmartWebView.asw_vcam_message)};
				} else {
					// Checking if multiple files are selected
					if (clipData != null) {
						final int numSelectedFiles = clipData.getItemCount();
						results = new Uri[numSelectedFiles];
						for (int i = 0; i < numSelectedFiles; i++) {
							results[i] = clipData.getItemAt(i).getUri();
						}
					} else if (stringData != null) {
						results = new Uri[]{Uri.parse(stringData)};
					}
				}
			}
			// Send the file paths to the callback and reset
			if (SmartWebView.asw_file_path != null) {
				SmartWebView.asw_file_path.onReceiveValue(results);
				SmartWebView.asw_file_path = null;
			}
		});

		// Pass the launcher to FileProcessing
		fileProcessing.registerActivityResultLauncher();

		// Setting port view
		String cookie_orientation = !(boolean) SmartWebView.ASWP_OFFLINE ? fns.get_cookies("ORIENT") : "";
		fns.set_orientation((!Objects.equals(cookie_orientation, "") ? Integer.parseInt(cookie_orientation) : SmartWebView.ASWV_ORIENTATION), false, this);

		// Use service worker
		if (Build.VERSION.SDK_INT >= 24) {
			ServiceWorkerController swController = ServiceWorkerController.getInstance();
			swController.setServiceWorkerClient(new ServiceWorkerClient() {
				@Override
				public WebResourceResponse shouldInterceptRequest(WebResourceRequest request) {
					return null;
				}
			});
		}

		// Prevent app from being started again when it is still alive in the background
		if (!isTaskRoot()) {
			finish();
			return;
		}

		if (SmartWebView.ASWV_LAYOUT == 1) {
			setContentView(R.layout.drawer_main);
			findViewById(R.id.app_bar).setVisibility(View.VISIBLE);

			Toolbar toolbar = findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

			DrawerLayout drawer = findViewById(R.id.drawer_layout);
			ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
			drawer.addDrawerListener(toggle);
			toggle.syncState();

			NavigationView navigationView = findViewById(R.id.nav_view);
			navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
		} else {
			setContentView(R.layout.activity_main);
		}

		SmartWebView.asw_view = findViewById(R.id.msw_view);

		// Add permission to print; allow only then to exec print_view
		SmartWebView.print_view = findViewById(R.id.print_view); // view on which you want to take a printout

		// Initializing notification manager
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= 26) {
			NotificationChannel notificationChannel = new NotificationChannel(SmartWebView.asw_fcm_channel, String.valueOf(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
			notificationChannel.setDescription(String.valueOf(R.string.notification_channel_desc));
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.enableVibration(true);
			notificationChannel.setShowBadge(true);
			assert notificationManager != null;
			notificationManager.createNotificationChannel(notificationChannel);
		}

		// Swipe refresh
		final SwipeRefreshLayout pull_refresh = findViewById(R.id.pullfresh);
		if (SmartWebView.ASWP_PULLFRESH) {
			pull_refresh.setOnRefreshListener(() -> {
				fns.pull_fresh(getApplicationContext());
				pull_refresh.setRefreshing(false);
			});
			SmartWebView.asw_view.getViewTreeObserver().addOnScrollChangedListener(() -> pull_refresh.setEnabled(SmartWebView.asw_view.getScrollY() == 0));
		} else {
			pull_refresh.setRefreshing(false);
			pull_refresh.setEnabled(false);
		}

		// Progress bar permission loop
		if (SmartWebView.ASWP_PBAR) {
			SmartWebView.asw_progress = findViewById(R.id.msw_progress);
		} else {
			findViewById(R.id.msw_progress).setVisibility(View.GONE);
		}
		SmartWebView.asw_loading_text = findViewById(R.id.msw_loading_text);

		Handler handler = new Handler();

		// Launching app rating request
		if (SmartWebView.ASWP_RATINGS) {
			handler.postDelayed(fns.get_rating(getApplicationContext()), 1000 * 60); //running request after few moments
		}

		// Logging basic device information
		fns.get_info();

		// Fetching GPS location if given permission
		if (SmartWebView.ASWP_LOCATION && !fns.check_permission(1, getApplicationContext())) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SmartWebView.loc_perm);
		}else {
			fns.get_location(getApplicationContext());
		}

		// Webview default customizations; customized for best performance
		WebSettings webSettings = SmartWebView.asw_view.getSettings();

		// Setting custom user agent
		if (SmartWebView.OVERRIDE_USER_AGENT || SmartWebView.POSTFIX_USER_AGENT) {
			String userAgent = webSettings.getUserAgentString();
			if (SmartWebView.OVERRIDE_USER_AGENT) {
				userAgent = SmartWebView.CUSTOM_USER_AGENT;
			}
			if (SmartWebView.POSTFIX_USER_AGENT) {
				userAgent = userAgent + " " + SmartWebView.USER_AGENT_POSTFIX;
			}
			webSettings.setUserAgentString(userAgent);
		}

		webSettings.setJavaScriptEnabled(true);
		webSettings.setSaveFormData(SmartWebView.ASWP_SFORM);
		webSettings.setSupportZoom(SmartWebView.ASWP_ZOOM);
		webSettings.setGeolocationEnabled(SmartWebView.ASWP_LOCATION);
		webSettings.setAllowFileAccess(true);
		webSettings.setAllowFileAccessFromFileURLs(true);
		webSettings.setAllowUniversalAccessFromFileURLs(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setDomStorageEnabled(true);

		if (!SmartWebView.ASWP_COPYPASTE) {
			SmartWebView.asw_view.setOnLongClickListener(v -> true);
		}
		SmartWebView.asw_view.setHapticFeedbackEnabled(false);

		// Webview download listener
		SmartWebView.asw_view.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {

			if (!fns.check_permission(2, getApplicationContext()) && !fns.check_permission(3, getApplicationContext())) {
				fns.get_permissions(3,MainActivity.this);

			} else {
				DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

				request.setMimeType(mimeType);
				request.addRequestHeader("cookie", fns.get_cookies(""));
				request.addRequestHeader("User-Agent", userAgent);
				request.setDescription(getString(R.string.dl_downloading));
				request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
				request.allowScanningByMediaScanner();
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
				DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
				assert dm != null;
				dm.enqueue(request);
				Toast.makeText(this, getString(R.string.dl_downloading2), Toast.LENGTH_LONG).show();
			}
		});

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
		webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		SmartWebView.asw_view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		SmartWebView.asw_view.setVerticalScrollBarEnabled(false); //** set this as permission variable
		SmartWebView.asw_view.setWebViewClient(new Callback());

		// Reading incoming intents
		Intent read_int = getIntent();
		Log.d("SLOG_INTENT", read_int.toUri(0));
		String uri = read_int.getStringExtra("uri");
		String share = read_int.getStringExtra("s_uri");
		String share_img = read_int.getStringExtra("s_img");

		if (share != null) {
			// Processing shared content
			Log.d("SLOG_SHARE_INTENT", share);
			Matcher matcher = Functions.url_pattern().matcher(share);
			String urlStr = "";
			if (matcher.find()) {
				urlStr = matcher.group();
				if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
					urlStr = urlStr.substring(1, urlStr.length() - 1);
				}
			}
			String red_url = SmartWebView.ASWV_SHARE_URL + "?text=" + share + "&link=" + urlStr + "&image_url=";
			fns.aswm_view(red_url, false, SmartWebView.asw_error_counter, getApplicationContext());

		// Processing shared image
		} else if (share_img != null) {
			Log.d("SLOG_SHARE_INTENT", share_img);
			Toast.makeText(this, share_img, Toast.LENGTH_LONG).show();
			fns.aswm_view(SmartWebView.ASWV_URL, false, SmartWebView.asw_error_counter, getApplicationContext());

		// Opening notification
		} else if (uri != null) {
			Log.d("SLOG_NOTIFICATION_INTENT", uri);
			fns.aswm_view(uri, false, SmartWebView.asw_error_counter, getApplicationContext());

		// Rendering default URL
		} else {
			Log.d("SLOG_MAIN_INTENT", SmartWebView.ASWV_URL);
			fns.aswm_view(SmartWebView.ASWV_URL, false, SmartWebView.asw_error_counter, getApplicationContext());
		}

		SmartWebView.asw_view.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				if(SmartWebView.SWV_DEBUGMODE) {
					Log.d("SWV_JS", consoleMessage.message() + " -- From line " +
						consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
				}
				return true;
			}

			@Override
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
				return fileProcessing.onShowFileChooser(webView, filePathCallback, fileChooserParams);
			}

			// Webview content rendering progress
			@Override
			public void onProgressChanged(WebView view, int p) {
				if (SmartWebView.ASWP_PBAR) {
					SmartWebView.asw_progress.setProgress(p);
					if (p == 100) {
						SmartWebView.asw_progress.setProgress(0);
					}
				}
			}

			// Overload the geoLocations permissions prompt to always allow instantly as app permission was granted previously
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				if (fns.check_permission(1, getApplicationContext())) {
					callback.invoke(origin, true, false);
				} else {
					fns.get_permissions(1, MainActivity.this);

				}
			}
		});

		if (getIntent().getData() != null) {
			String path = getIntent().getDataString();
			fns.aswm_view(path, false, SmartWebView.asw_error_counter, getApplicationContext());
		}

		// Debug mode logging data
		if(SmartWebView.SWV_DEBUGMODE){
			Log.d("SWV_DEBUG", "URL: "+SmartWebView.CURR_URL+"DEVICE INFO: "+ Arrays.toString(fns.get_info()));
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		SmartWebView.asw_view.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		SmartWebView.asw_view.onResume();
		// coloring the "recent apps" tab header; doing it onResume, as an insurance
		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
		ActivityManager.TaskDescription taskDesc;
		taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getColor(R.color.colorPrimary));
		this.setTaskDescription(taskDesc);

		if (SmartWebView.ASWP_LOCATION) {
			fns.get_location(getApplicationContext());
		}
	}

	// Actions on key logging
	@Override
	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (SmartWebView.asw_view.canGoBack()) {
					SmartWebView.asw_view.goBack();
				} else {
					if (SmartWebView.ASWP_EXITDIAL) {
						fns.ask_exit(getApplicationContext()); // call ask_exit()
					} else {
						finish();
					}
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		SmartWebView.asw_view.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		SmartWebView.asw_view.restoreState(savedInstanceState);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		// Forward the result to the PluginManager
		pluginManager.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == SmartWebView.loc_perm) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Location permission granted
				if (SmartWebView.ASWP_LOCATION) {
					fns.get_location(getApplicationContext());
				}
			} else {
				// Location permission denied
				if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
					// User has denied permission with "Don't ask again"
					// Guide the user to app settings to enable the permission manually
					// You can display a dialog or a Snackbar here
				} else {
					// User has denied permission without "Don't ask again"
					// You can show a rationale again or simply inform the user that the feature is disabled
				}
			}
		} else if (requestCode == SmartWebView.file_perm || requestCode == SmartWebView.cam_perm) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// File or Camera permission granted
				if (SmartWebView.ASWP_FUPLOAD) {
					// You might want to add a method here to re-trigger the file chooser
					// For example:
					// retryOpenFileChooser();
				}
			} else {
				// Permission denied
				if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
					!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
					// User has denied permission with "Don't ask again"
					// Guide the user to app settings to enable the permission manually
				} else {
					// User has denied permission without "Don't ask again"
					// Inform the user that the feature is disabled or show a rationale
				}
			}
		}else if (requestCode == SmartWebView.noti_perm) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Send a test notification
				Firebase firebase = new Firebase();
				firebase.sendMyNotification("Yay! Firebase is working", "This is a test notification in action.", "OPEN_URI", SmartWebView.ASWV_URL, null, String.valueOf(SmartWebView.ASWV_FCM_ID), getApplicationContext());
			}

		}
	}

	// Setting activity layout visibility
	private class Callback extends WebViewClient {
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			fns.get_location(getApplicationContext());
		}

		public void onPageFinished(WebView view, String url) {
			findViewById(R.id.msw_welcome).setVisibility(View.GONE);
			findViewById(R.id.msw_view).setVisibility(View.VISIBLE);

			// Injecting Google Tag Manager
			fns.inject_gtag(view, SmartWebView.ASWV_GTAG);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
			String url = request.getUrl().toString();

			// Check if the PluginManager wants to override the URL
			if (pluginManager.shouldOverrideUrlLoading(view, request)) {
				return true; // URL was handled by a plugin
			}

			// Default handling for other URLs
			if (url.startsWith("print:")) {
				Functions.print_page(view, view.getTitle(), MainActivity.this);
				return true;
			} else {
				if (url.matches("^(https?|file)://.*$")) {
					SmartWebView.CURR_URL = url;
				}
				return fns.url_actions(view, url, MainActivity.this);
			}
		}

		@SuppressLint("WebViewClientOnReceivedSslError")
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			if (SmartWebView.ASWP_CERT_VERI) {
				// Default behavior: Don't proceed with untrusted certificates
				super.onReceivedSslError(view, handler, error);
			} else {
				// Bypass SSL error
				handler.proceed();

				// Show Toast message if debug mode is enabled
				if (SmartWebView.SWV_DEBUGMODE) {
					Toast.makeText(MainActivity.this, "SSL Error: " + error.getPrimaryError(), Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
			super.onReceivedHttpError(view, request, errorResponse);
			Log.e("HTTP_ERROR", "Error loading " + request.getUrl().toString() + ": " + errorResponse.getStatusCode());
		}
	}
}
