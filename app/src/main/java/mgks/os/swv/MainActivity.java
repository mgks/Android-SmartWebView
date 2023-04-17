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

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;

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

import android.provider.MediaStore;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

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
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;

// importing functions

public class MainActivity extends AppCompatActivity {
	ActivityResultLauncher<Intent> act_result_launcher;
	private final static Functions fns = new Functions();

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

	@SuppressLint({"SetJavaScriptEnabled", "WrongViewCast", "JavascriptInterface"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// calling for file upload and processing method
		//FileProcessing fileProcessing = new FileProcessing();
		//fileProcessing.onCreate(savedInstanceState);

		act_result_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
			//Log.d("SLOG_TRUE_ONLINE", String.valueOf(true_online));

			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
			Uri[] results = null;
			if (result.getResultCode() == Activity.RESULT_CANCELED) {
				// If the file request was cancelled (i.e. user exited camera),
				// we must still send a null value in order to ensure that future attempts
				// to pick files will still work.
				SmartWebView.asw_file_path.onReceiveValue(null);
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
					if (null != clipData) { // checking if multiple files selected or not
						final int numSelectedFiles = clipData.getItemCount();
						results = new Uri[numSelectedFiles];
						for (int i = 0; i < clipData.getItemCount(); i++) {
							results[i] = clipData.getItemAt(i).getUri();
						}
					} else {
						try {
							assert result.getData() != null;
							Bitmap cam_photo = (Bitmap) result.getData().getExtras().get("data");
							ByteArrayOutputStream bytes = new ByteArrayOutputStream();
							cam_photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
							stringData = MediaStore.Images.Media.insertImage(getContentResolver(), cam_photo, null, null);
						} catch (Exception ignored) {
						}
						results = new Uri[]{Uri.parse(stringData)};
					}
				}
			}
			SmartWebView.asw_file_path.onReceiveValue(results);
			SmartWebView.asw_file_path = null;
		});

		// setting port view
		String cookie_orientation = !(boolean) SmartWebView.ASWP_OFFLINE ? fns.get_cookies("ORIENT") : "";
		fns.set_orientation((!Objects.equals(cookie_orientation, "") ? Integer.parseInt(cookie_orientation) : SmartWebView.ASWV_ORIENTATION), false, getApplicationContext());

		// use Service Worker
		if (Build.VERSION.SDK_INT >= 24) {
			ServiceWorkerController swController = ServiceWorkerController.getInstance();
			swController.setServiceWorkerClient(new ServiceWorkerClient() {
				@Override
				public WebResourceResponse shouldInterceptRequest(WebResourceRequest request) {
					return null;
				}
			});
		}

		// prevent app from being started again when it is still alive in the background
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
		SmartWebView.print_view = (WebView) findViewById(R.id.print_view); //view on which you want to take a printout
		//asw_view.addJavascriptInterface(new JSInterface(), "JSOUT");
		//asw_view.addJavascriptInterface(new MainActivity.WebViewJavaScriptInterface(this), "androidapp"); //
		// "androidapp is used to call methods exposed from javascript interface, in this example case print
		// method can be called by androidapp.print(String)"
		// load your data from the URL in web view

		/// exp
		//// end exp

		// requesting new FCM token; updating final cookie variable
		fns.fcm_token();

		// notification manager
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= 26) {
			NotificationChannel notificationChannel = new NotificationChannel(SmartWebView.asw_fcm_channel, String.valueOf(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
			notificationChannel.setDescription(String.valueOf(R.string.notification_channel_desc));
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.enableVibration(true);
			notificationChannel.setShowBadge(true);
			assert notificationManager != null;
			notificationManager.createNotificationChannel(notificationChannel);
			notificationManager.createNotificationChannel(notificationChannel);
		}

		// swipe refresh
		final SwipeRefreshLayout pullfresh = findViewById(R.id.pullfresh);
		if (SmartWebView.ASWP_PULLFRESH) {
			pullfresh.setOnRefreshListener(() -> {
				fns.pull_fresh(getApplicationContext());
				pullfresh.setRefreshing(false);
			});
			SmartWebView.asw_view.getViewTreeObserver().addOnScrollChangedListener(() -> pullfresh.setEnabled(SmartWebView.asw_view.getScrollY() == 0));
		} else {
			pullfresh.setRefreshing(false);
			pullfresh.setEnabled(false);
		}

		if (SmartWebView.ASWP_PBAR) {
			SmartWebView.asw_progress = findViewById(R.id.msw_progress);
		} else {
			findViewById(R.id.msw_progress).setVisibility(View.GONE);
		}
		SmartWebView.asw_loading_text = findViewById(R.id.msw_loading_text);
		Handler handler = new Handler();

		//Launching app rating request
		if (SmartWebView.ASWP_RATINGS) {
			handler.postDelayed(fns.get_rating(getApplicationContext()), 1000 * 60); //running request after few moments
		}

		//Getting basic device information
		fns.get_info();

		//Getting GPS location of device if given permission
		if (SmartWebView.ASWP_LOCATION && !fns.check_permission(1, getApplicationContext())) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SmartWebView.loc_perm);
		}
		fns.get_location(getApplicationContext());

		//Webview settings; defaults are customized for best performance
		WebSettings webSettings = SmartWebView.asw_view.getSettings();

		// setting custom user agent
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

		if (!SmartWebView.ASWP_OFFLINE) {
			webSettings.setJavaScriptEnabled(SmartWebView.ASWP_JSCRIPT);
		}
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

		// download listener
		SmartWebView.asw_view.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {

			if (!fns.check_permission(2, getApplicationContext())) {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, SmartWebView.file_perm);
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
				Toast.makeText(getApplicationContext(), getString(R.string.dl_downloading2), Toast.LENGTH_LONG).show();
			}
		});

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
		webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		SmartWebView.asw_view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		SmartWebView.asw_view.setVerticalScrollBarEnabled(false);
		SmartWebView.asw_view.setWebViewClient(new Callback());

		//Reading incoming intents
		Intent read_int = getIntent();
		Log.d("SLOG_INTENT", read_int.toUri(0));
		String uri = read_int.getStringExtra("uri");
		String share = read_int.getStringExtra("s_uri");
		String share_img = read_int.getStringExtra("s_img");

		if (share != null) {
			//Processing shared content
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
			//Toast.makeText(this, "SHARE: "+red_url+"\nLINK: "+urlStr, Toast.LENGTH_LONG).show();
			fns.aswm_view(red_url, false, SmartWebView.asw_error_counter, getApplicationContext());

		} else if (share_img != null) {
			//Processing shared content
			Log.d("SLOG_SHARE_INTENT", share_img);
			Toast.makeText(this, share_img, Toast.LENGTH_LONG).show();
			fns.aswm_view(SmartWebView.ASWV_URL, false, SmartWebView.asw_error_counter, getApplicationContext());

		} else if (uri != null) {
			//Opening notification
			Log.d("SLOG_NOTIFI_INTENT", uri);
			fns.aswm_view(uri, false, SmartWebView.asw_error_counter, getApplicationContext());

		} else {
			//Rendering the default URL
			Log.d("SLOG_MAIN_INTENT", SmartWebView.ASWV_URL);
			fns.aswm_view(SmartWebView.ASWV_URL, false, SmartWebView.asw_error_counter, getApplicationContext());
		}

		if (SmartWebView.ASWP_ADMOB) {
			MobileAds.initialize(this, initializationStatus -> {
			});
			/*List<String> testDeviceIds = List.of("4C304B10577C757E3A3C3B667FF84F8C");
			RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
			MobileAds.setRequestConfiguration(configuration);
			MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(List.of("4C304B10577C757E3A3C3B667FF84F8C")).build());*/
			SmartWebView.asw_ad_view = findViewById(R.id.msw_ad_view);
		}

		//
		SmartWebView.asw_view.setWebChromeClient(new WebChromeClient() {

			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
				if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
					if (SmartWebView.ASWP_FUPLOAD) {
						SmartWebView.asw_file_path = filePathCallback;
						Intent takePictureIntent = null;
						Intent takeVideoIntent = null;
						if (SmartWebView.ASWP_CAMUPLOAD) {
							boolean includeVideo = false;
							boolean includePhoto = false;

							// Check the accept parameter to determine which intent(s) to include.
							paramCheck:
							for (String acceptTypes : fileChooserParams.getAcceptTypes()) {
								// Although it's an array, it still seems to be the whole value.
								// Split it out into chunks so that we can detect multiple values.
								String[] splitTypes = acceptTypes.split(", ?+");
								for (String acceptType : splitTypes) {
									switch (acceptType) {
										case "*/*":
											includePhoto = true;
											includeVideo = true;
											break paramCheck;
										case "image/*":
											includePhoto = true;
											break;
										case "video/*":
											includeVideo = true;
											break;
									}
								}
							}

							// If no `accept` parameter was specified, allow both photo and video.
							if (fileChooserParams.getAcceptTypes().length == 0) {
								includePhoto = true;
								includeVideo = true;
							}

							if (includePhoto) {
								takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
									File photoFile = null;
									try {
										photoFile = fns.create_image(getApplicationContext());
										takePictureIntent.putExtra("PhotoPath", SmartWebView.asw_pcam_message);
									} catch (IOException ex) {
										Log.e("SLOG_ERROR", "Image file creation failed", ex);
									}
									if (photoFile != null) {
										SmartWebView.asw_pcam_message = "file:" + photoFile.getAbsolutePath();
										takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", photoFile));
									} else {
										takePictureIntent = null;
									}
								}
							}

							if (includeVideo) {
								takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
								if (takeVideoIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
									File videoFile = null;
									try {
										videoFile = fns.create_video(getApplicationContext());
									} catch (IOException ex) {
										Log.e("SLOG_ERROR", "Video file creation failed", ex);
									}
									if (videoFile != null) {
										SmartWebView.asw_vcam_message = "file:" + videoFile.getAbsolutePath();
										takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", videoFile));
									} else {
										takeVideoIntent = null;
									}
								}
							}
						}

						Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
						if (!SmartWebView.ASWP_ONLYCAM) {
							contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
							contentSelectionIntent.setType(SmartWebView.ASWV_F_TYPE);
							if (SmartWebView.ASWP_MULFILE) {
								contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
							}
						}
						Intent[] intentArray;
						if (takePictureIntent != null && takeVideoIntent != null) {
							intentArray = new Intent[]{takePictureIntent, takeVideoIntent};
						} else if (takePictureIntent != null) {
							intentArray = new Intent[]{takePictureIntent};
						} else if (takeVideoIntent != null) {
							intentArray = new Intent[]{takeVideoIntent};
						} else {
							intentArray = new Intent[0];
						}

						Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
						chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
						chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.fl_chooser));
						chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
						//startActivityForResult(chooserIntent, asw_file_req);
						act_result_launcher.launch(chooserIntent);
					}
					return true;
				} else {
					fns.get_file_perm(MainActivity.this);
					return false;
				}
			}


			// getting webview content rendering progress
			@Override
			public void onProgressChanged(WebView view, int p) {
				if (SmartWebView.ASWP_PBAR) {
					SmartWebView.asw_progress.setProgress(p);
					if (p == 100) {
						SmartWebView.asw_progress.setProgress(0);
					}
				}
			}

			// overload the geoLocations permissions prompt to always allow instantly as app permission was granted previously
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				if (Build.VERSION.SDK_INT < 23 || fns.check_permission(1, getApplicationContext())) {
					// location permissions were granted previously so auto-approve
					callback.invoke(origin, true, false);
				} else {
					// location permissions not granted so request them
					ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SmartWebView.loc_perm);
				}
			}
		});
		if (getIntent().getData() != null) {
			String path = getIntent().getDataString();
            /*
            If you want to check or use specific directories or schemes or hosts

            Uri data        = getIntent().getData();
            String scheme   = data.getScheme();
            String host     = data.getHost();
            List<String> pr = data.getPathSegments();
            String param1   = pr.get(0);
            */
			fns.aswm_view(path, false, SmartWebView.asw_error_counter, getApplicationContext());
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
		//Coloring the "recent apps" tab header; doing it onResume, as an insurance
		if (Build.VERSION.SDK_INT >= 23) {
			Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
			ActivityManager.TaskDescription taskDesc;
			taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getColor(R.color.colorPrimary));
			this.setTaskDescription(taskDesc);
		}
		fns.get_location(getApplicationContext());
	}

	//Checking if users allowed the requested permissions or not
	@SuppressLint("MissingSuperCall")
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == 1) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				fns.get_location(getApplicationContext());
			}
		}
	}

	//Action on back key tap/click
	@Override
	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (SmartWebView.asw_view.canGoBack()) {
					SmartWebView.asw_view.goBack();
				} else {
					if (SmartWebView.ASWP_EXITDIAL) {
						fns.ask_exit(getApplicationContext());
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
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		SmartWebView.asw_view.restoreState(savedInstanceState);
	}

	public static class WebViewJavaScriptInterface {
		/*
		WebViewJavaScriptInterface(Context context) {
			public void print(final String data){
				runOnUiThread(() -> doWebViewPrint(data));
			}
		}
		*/
	}

	//Setting activity layout visibility
	private class Callback extends WebViewClient {
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			fns.get_location(getApplicationContext());
		}

		public void onPageFinished(WebView view, String url) {
			findViewById(R.id.msw_welcome).setVisibility(View.GONE);
			findViewById(R.id.msw_view).setVisibility(View.VISIBLE);
		}

		//For android below API 23
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Toast.makeText(getApplicationContext(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
			fns.aswm_view("file:///android_asset/error.html", false, SmartWebView.asw_error_counter, getApplicationContext());
		}

		//Overriding webview URLs
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			SmartWebView.CURR_URL = url;
			return fns.url_actions(view, url, getApplicationContext());
		}

		//Overriding webview URLs for API 23+ [suggested by github.com/JakePou]
		@TargetApi(Build.VERSION_CODES.N)
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
			SmartWebView.CURR_URL = request.getUrl().toString();
			return fns.url_actions(view, request.getUrl().toString(), getApplicationContext());
		}

		@SuppressLint("WebViewClientOnReceivedSslError")
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			if (SmartWebView.ASWP_CERT_VERI) {
				super.onReceivedSslError(view, handler, error);
			} else {
				// to ignore SSL certificate errors; can cause security issues
				handler.proceed();
			}
		}
	}
}
