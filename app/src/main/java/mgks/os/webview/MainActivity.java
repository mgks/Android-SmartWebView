package mgks.os.webview;

/*
 * Android Smart WebView is an Open Source Project available on GitHub (https://github.com/mgks/Android-SmartWebView).
 * Initially developed by Ghazi Khan (https://github.com/mgks) under MIT Open Source License.
 * This program is free to use for private and commercial purposes.
 * Please mention project source or credit developers in your Application's License(s) Wiki.
 * Giving right credit to developers encourages them to create better projects :)
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

	// permission variables
	static boolean ASWP_JSCRIPT		= SmartWebView.ASWP_JSCRIPT;
	static boolean ASWP_FUPLOAD		= SmartWebView.ASWP_FUPLOAD;
	static boolean ASWP_CAMUPLOAD	= SmartWebView.ASWP_CAMUPLOAD;
	static boolean ASWP_ONLYCAM		= SmartWebView.ASWP_ONLYCAM;
	static boolean ASWP_MULFILE		= SmartWebView.ASWP_MULFILE;
	static boolean ASWP_LOCATION	= SmartWebView.ASWP_LOCATION;
	static boolean ASWP_RATINGS		= SmartWebView.ASWP_RATINGS;
	static boolean ASWP_PULLFRESH	= SmartWebView.ASWP_PULLFRESH;
	static boolean ASWP_PBAR		= SmartWebView.ASWP_PBAR;
	static boolean ASWP_ZOOM        = SmartWebView.ASWP_ZOOM;
	static boolean ASWP_SFORM       = SmartWebView.ASWP_SFORM;
	static boolean ASWP_OFFLINE		= SmartWebView.ASWP_OFFLINE;
	static boolean ASWP_EXTURL		= SmartWebView.ASWP_EXTURL;
	static boolean ASWP_ADMOB		= SmartWebView.ASWP_ADMOB;
	static boolean ASWP_TAB			= SmartWebView.ASWP_TAB;
	static boolean ASWP_EXITDIAL	= SmartWebView.ASWP_EXITDIAL;
	static boolean ASWP_CP			= SmartWebView.ASWP_CP;

	// security variables
	static boolean ASWP_CERT_VERIFICATION 	= SmartWebView.ASWP_CERT_VERI;

	//Configuration variables
	private static String ASWV_URL     		= SmartWebView.ASWV_URL;
	private String CURR_URL					= ASWV_URL;
	private static String ASWV_SEARCH		= SmartWebView.ASWV_SEARCH;
	private static String ASWV_SHARE_URL	= SmartWebView.ASWV_SHARE_URL;
	private static String ASWV_EXC_LIST		= SmartWebView.ASWV_EXC_LIST;

	private static String ASWV_F_TYPE   	= SmartWebView.ASWV_F_TYPE;

	private static String ASWV_ADMOB		= SmartWebView.ASWV_ADMOB;

    public static String ASWV_HOST			= aswm_host(ASWV_URL);

	public static int ASWV_FCM_ID			= aswm_fcm_id();
	public static int ASWV_LAYOUT			= SmartWebView.ASWV_LAYOUT;

    //Careful with these variable names if altering
    WebView asw_view;
    WebView print_view;
    ProgressBar asw_progress;
    TextView asw_loading_text;
    NotificationManager asw_notification;
    Notification asw_notification_new;
	int asw_error_counter = 0;

	Boolean true_online = !ASWP_OFFLINE && !ASWV_URL.startsWith("file:///");

    private String asw_cam_message;
    private ValueCallback<Uri> asw_file_message;
    private ValueCallback<Uri[]> asw_file_path;
    private final static int asw_file_req = 1;

	private final static int loc_perm = 1;
	private final static int file_perm = 2;

	public static String asw_fcm_channel = "1";
	public String fcm_token;

    private SecureRandom random = new SecureRandom();

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            Uri[] results = null;
            if (resultCode == Activity.RESULT_CANCELED) {
                if (requestCode == asw_file_req) {
                    // If the file request was cancelled (i.e. user exited camera),
                    // we must still send a null value in order to ensure that future attempts
                    // to pick files will still work.
                    asw_file_path.onReceiveValue(null);
                    return;
                }
            }
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == asw_file_req) {
                    if (null == asw_file_path) {
                        return;
                    }
					ClipData clipData;
                    String stringData;
					try {
						clipData = intent.getClipData();
						stringData = intent.getDataString();
					}catch (Exception e){
						clipData = null;
						stringData = null;
					}

					if (clipData == null && stringData == null && asw_cam_message != null) {
						results = new Uri[]{Uri.parse(asw_cam_message)};

					} else {
						if (null != clipData) { // checking if multiple files selected or not
							final int numSelectedFiles = clipData.getItemCount();
							results = new Uri[numSelectedFiles];
							for (int i = 0; i < clipData.getItemCount(); i++) {
								results[i] = clipData.getItemAt(i).getUri();
							}
						} else {
							results = new Uri[]{Uri.parse(stringData)};
						}
					}
                }
            }
            asw_file_path.onReceiveValue(results);
            asw_file_path = null;

        } else {
            if (requestCode == asw_file_req) {
                if (null == asw_file_message) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                asw_file_message.onReceiveValue(result);
                asw_file_message = null;
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.w("READ_PERM = ",Manifest.permission.READ_EXTERNAL_STORAGE);
		Log.w("WRITE_PERM = ",Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // prevent app from being started again when it is still alive in the background
        if (!isTaskRoot()) {
        	finish();
        	return;
        }

		if(ASWV_LAYOUT==1){
			setContentView(R.layout.drawer_main);
			findViewById(R.id.app_bar).setVisibility(View.VISIBLE);

			Toolbar toolbar = findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

			DrawerLayout drawer = findViewById(R.id.drawer_layout);
			ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawer, toolbar, R.string.open, R.string.close);
			drawer.addDrawerListener(toggle);
			toggle.syncState();

			NavigationView navigationView = findViewById(R.id.nav_view);
			navigationView.setNavigationItemSelectedListener(MainActivity.this);
		}else{
			setContentView(R.layout.activity_main);
		}

		asw_view = findViewById(R.id.msw_view);
		print_view = (WebView) findViewById(R.id.print_view); //view on which you want to take a printout
		asw_view.addJavascriptInterface(new MainActivity.WebViewJavaScriptInterface(this), "androidapp"); //
		// "androidapp is used to call methods exposed from javascript interface, in this example case print
		// method can be called by androidapp.print(String)"
		// load your data from the URL in web view

		// requesting new FCM token; updating final cookie variable
		fcm_token();

		// notification manager
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if(Build.VERSION.SDK_INT >= 26) {
			NotificationChannel notificationChannel = new NotificationChannel(asw_fcm_channel,String.valueOf(R.string.notification_channel_name),NotificationManager.IMPORTANCE_HIGH);
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
		if (ASWP_PULLFRESH) {
			pullfresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					pull_fresh();
					pullfresh.setRefreshing(false);
				}
			});
			asw_view.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
				@Override
				public void onScrollChanged() {
					if (asw_view.getScrollY() == 0) {
						pullfresh.setEnabled(true);
					} else {
						pullfresh.setEnabled(false);
					}
				}
			});
		}else{
			pullfresh.setRefreshing(false);
			pullfresh.setEnabled(false);
		}

		if (ASWP_PBAR) {
            asw_progress = findViewById(R.id.msw_progress);
        } else {
            findViewById(R.id.msw_progress).setVisibility(View.GONE);
        }
        asw_loading_text = findViewById(R.id.msw_loading_text);
        Handler handler = new Handler();

        //Launching app rating request
        if (ASWP_RATINGS) {
            handler.postDelayed(new Runnable() { public void run() { get_rating(); }}, 1000 * 60); //running request after few moments
        }

        //Getting basic device information
		get_info();

		//Getting GPS location of device if given permission
		if(ASWP_LOCATION && !check_permission(1)){
			ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, loc_perm);
		}
		get_location();

        //Webview settings; defaults are customized for best performance
        WebSettings webSettings = asw_view.getSettings();

		if(!ASWP_OFFLINE){
			webSettings.setJavaScriptEnabled(ASWP_JSCRIPT);
		}
		webSettings.setSaveFormData(ASWP_SFORM);
		webSettings.setSupportZoom(ASWP_ZOOM);
		webSettings.setGeolocationEnabled(ASWP_LOCATION);
		webSettings.setAllowFileAccess(true);
		webSettings.setAllowFileAccessFromFileURLs(true);
		webSettings.setAllowUniversalAccessFromFileURLs(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setDomStorageEnabled(true);

		if(!ASWP_CP) {
			asw_view.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					return true;
				}
			});
		}
		asw_view.setHapticFeedbackEnabled(false);

		// download listener
		asw_view.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

				if(!check_permission(2)){
					ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, file_perm);
				}else {
					DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

					request.setMimeType(mimeType);
					String cookies = CookieManager.getInstance().getCookie(url);
					request.addRequestHeader("cookie", cookies);
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
			}
		});

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
		webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		asw_view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        asw_view.setVerticalScrollBarEnabled(false);
        asw_view.setWebViewClient(new Callback());

		//Reading incoming intents
		Intent read_int = getIntent();
		Log.d("INTENT", read_int.toUri(0));
		String uri = read_int.getStringExtra("uri");
		String share = read_int.getStringExtra("s_uri");
		String share_img = read_int.getStringExtra("s_img");

		if(share != null) {
			//Processing shared content
			Log.d("SHARE INTENT",share);
			Matcher matcher = urlPattern.matcher(share);
			String urlStr = "";
			if(matcher.find()){
				urlStr = matcher.group();
				if(urlStr.startsWith("(") && urlStr.endsWith(")")) {
					urlStr = urlStr.substring(1, urlStr.length() - 1);
				}
			}
			String red_url = ASWV_SHARE_URL+"?text="+share+"&link="+urlStr+"&image_url=";
			//Toast.makeText(MainActivity.this, "SHARE: "+red_url+"\nLINK: "+urlStr, Toast.LENGTH_LONG).show();
			aswm_view(red_url, false, asw_error_counter);

		}else if(share_img != null) {
			//Processing shared content
			Log.d("SHARE INTENT",share_img);
			Toast.makeText(MainActivity.this, share_img, Toast.LENGTH_LONG).show();
			aswm_view(ASWV_URL, false, asw_error_counter);

		}else if(uri != null) {
			//Opening notification
			Log.d("NOTIFICATION INTENT",uri);
			aswm_view(uri, false, asw_error_counter);

		}else{
			//Rendering the default URL
			Log.d("MAIN INTENT",ASWV_URL);
			aswm_view(ASWV_URL, false, asw_error_counter);
		}

		if(ASWP_ADMOB) {
			MobileAds.initialize(this, ASWV_ADMOB);
			AdView asw_ad_view = findViewById(R.id.msw_ad_view);
			AdRequest adRequest = new AdRequest.Builder().build();
			asw_ad_view.loadAd(adRequest);
		}

        asw_view.setWebChromeClient(new WebChromeClient() {
            // handling input[type="file"]
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams){
            	if(check_permission(2) && check_permission(3)) {
					if (ASWP_FUPLOAD) {
						asw_file_path = filePathCallback;
						Intent takePictureIntent = null;
						Intent takeVideoIntent = null;
						if (ASWP_CAMUPLOAD) {
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
								if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
									File photoFile = null;
									try {
										photoFile = create_image();
										takePictureIntent.putExtra("PhotoPath", asw_cam_message);
									} catch (IOException ex) {
										Log.e(TAG, "Image file creation failed", ex);
									}
									if (photoFile != null) {
										asw_cam_message = "file:" + photoFile.getAbsolutePath();
										takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
									} else {
										takePictureIntent = null;
									}
								}
							}

							if (includeVideo) {
								takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
								if (takeVideoIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
									File videoFile = null;
									try {
										videoFile = create_video();
									} catch (IOException ex) {
										Log.e(TAG, "Video file creation failed", ex);
									}
									if (videoFile != null) {
										asw_cam_message = "file:" + videoFile.getAbsolutePath();
										takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
									} else {
										takeVideoIntent = null;
									}
								}
							}
						}

						Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
						if (!ASWP_ONLYCAM) {
							contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
							contentSelectionIntent.setType(ASWV_F_TYPE);
							if (ASWP_MULFILE) {
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
						startActivityForResult(chooserIntent, asw_file_req);
					}
					return true;
				}else{
            		get_file();
            		return false;
				}
            }

            //Getting webview rendering progress
            @Override
            public void onProgressChanged(WebView view, int p) {
                if (ASWP_PBAR) {
                    asw_progress.setProgress(p);
                    if (p == 100) {
                        asw_progress.setProgress(0);
                    }
                }
            }

	    	// overload the geoLocations permissions prompt to always allow instantly as app permission was granted previously
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				if(Build.VERSION.SDK_INT < 23 || check_permission(1)){
					// location permissions were granted previously so auto-approve
					callback.invoke(origin, true, false);
				} else {
					// location permissions not granted so request them
					ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, loc_perm);
				}
			}
        });
        if (getIntent().getData() != null) {
            String path     = getIntent().getDataString();
            /*
            If you want to check or use specific directories or schemes or hosts

            Uri data        = getIntent().getData();
            String scheme   = data.getScheme();
            String host     = data.getHost();
            List<String> pr = data.getPathSegments();
            String param1   = pr.get(0);
            */
            aswm_view(path, false, asw_error_counter);
        }
    }

	public class WebViewJavaScriptInterface {
		WebViewJavaScriptInterface(Context context) {
			/*public void print(final String data){
				runOnUiThread(() -> doWebViewPrint(data));
			}*/
		}
	}

	private void doWebViewPrint(String ss) {
		print_view.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				print_page(view,view.getTitle(),false);
				super.onPageFinished(view, url);
			}
		});
		// Generate an HTML document on the fly:
		print_view.loadDataWithBaseURL(null, ss, "text/html", "UTF-8", null);
	}

	@Override
	public void onPause() {
		super.onPause();
		asw_view.onPause();
	}

    @Override
    public void onResume() {
        super.onResume();
        asw_view.onResume();
        //Coloring the "recent apps" tab header; doing it onResume, as an insurance
        if (Build.VERSION.SDK_INT >= 23) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDesc;
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getColor(R.color.colorPrimary));
            MainActivity.this.setTaskDescription(taskDesc);
        }
        get_location();
    }

    //Setting activity layout visibility
	private class Callback extends WebViewClient {
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            get_location();
        }

        public void onPageFinished(WebView view, String url) {
            findViewById(R.id.msw_welcome).setVisibility(View.GONE);
            findViewById(R.id.msw_view).setVisibility(View.VISIBLE);
        }
        //For android below API 23
		@Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
            aswm_view("file:///android_asset/error.html", false, asw_error_counter);
        }

        //Overriding webview URLs
		@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	CURR_URL = url;
			return url_actions(view, url);
        }

		//Overriding webview URLs for API 23+ [suggested by github.com/JakePou]
		@TargetApi(Build.VERSION_CODES.N)
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        	CURR_URL = request.getUrl().toString();
			return url_actions(view, request.getUrl().toString());
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        	if(ASWP_CERT_VERIFICATION) {
				super.onReceivedSslError(view, handler, error);
			} else {
        		handler.proceed(); // Ignore SSL certificate errors
			}
		}
	}

    //Random ID creation function to help get fresh cache every-time webview reloaded
    public String random_id() {
        return new BigInteger(130, random).toString(32);
    }

    //Opening URLs inside webview with request
    void aswm_view(String url, Boolean tab, int error_counter) {
		if(error_counter > 2){
			asw_error_counter = 0;
			aswm_exit();
		}else {
			if(tab){
				if(ASWP_TAB) {
					CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
					intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
					intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
					intentBuilder.setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					CustomTabsIntent customTabsIntent = intentBuilder.build();
					try {
						customTabsIntent.launchUrl(MainActivity.this, Uri.parse(url));
					} catch (ActivityNotFoundException e) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
					}
				}else{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					startActivity(intent);
				}
			} else {
				if (url.contains("?")) { // check to see whether the url already has query parameters and handle appropriately.
					url += "&";
				} else {
					url += "?";
				}
				url += "rid=" + random_id();
				asw_view.loadUrl(url);
			}
        }
    }

	/*--- actions based on URL structure ---*/

	public boolean url_actions(WebView view, String url){
		boolean a = true;
		// show toast error if not connected to the network
		if (!ASWP_OFFLINE && !DetectConnection.isInternetAvailable(MainActivity.this)) {
			Toast.makeText(getApplicationContext(), getString(R.string.check_connection), Toast.LENGTH_SHORT).show();

		// use this in a hyperlink to redirect back to default URL :: href="refresh:android"
		} else if (url.startsWith("refresh:")) {
			String ref_sch = (Uri.parse(url).toString()).replace("refresh:","");
			if(ref_sch.matches("URL")){
				CURR_URL = ASWV_URL;
			}
			pull_fresh();

		// use this in a hyperlink to launch default phone dialer for specific number :: href="tel:+919876543210"
		} else if (url.startsWith("tel:")) {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
			startActivity(intent);

		} else if(url.startsWith("print:")) {
			print_page(view,view.getTitle(),true);

		// use this to open your apps page on google play store app :: href="rate:android"
		} else if (url.startsWith("rate:")) {
			final String app_package = getPackageName(); //requesting app package name from Context or Activity object
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_package)));
			} catch (ActivityNotFoundException anfe) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_package)));
			}

		// sharing content from your webview to external apps :: href="share:URL" and remember to place the URL you want to share after share:___
		} else if (url.startsWith("share:")) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, view.getTitle());
			intent.putExtra(Intent.EXTRA_TEXT, view.getTitle()+"\nVisit: "+(Uri.parse(url).toString()).replace("share:",""));
			startActivity(Intent.createChooser(intent, getString(R.string.share_w_friends)));

		// use this in a hyperlink to exit your app :: href="exit:android"
		} else if (url.startsWith("exit:")) {
			aswm_exit();

		// getting location for offline files
		} else if (url.startsWith("offloc:")) {
			String offloc = ASWV_URL+"?loc="+get_location();
			aswm_view(offloc,false, asw_error_counter);
			Log.d("OFFLINE LOC REQ",offloc);

		// creating firebase notification for offline files
		} else if (url.startsWith("fcm:")) {
			String fcm = ASWV_URL+"?fcm="+fcm_token();
			aswm_view(fcm,false, asw_error_counter);
			Log.d("OFFLINE_FCM_TOKEN",fcm);

		// opening external URLs in android default web browser
		} else if (ASWP_EXTURL && !aswm_host(url).equals(ASWV_HOST)) {
			aswm_view(url,true, asw_error_counter);

		// else return false for no special action
		} else {
			a = false;
		}
		return a;
	}

	//Getting host name
	public static String aswm_host(String url){
		if (url == null || url.length() == 0) {
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
		Log.w("URL Host: ",url.substring(dslash, end));
		return url.substring(dslash, end);
	}

	//Reloading current page
	public void pull_fresh(){
    	aswm_view((!CURR_URL.equals("")?CURR_URL:ASWV_URL),false, asw_error_counter);
	}

	//Getting device basic information
	public void get_info(){
		if(true_online) {
			fcm_token();
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			cookieManager.setCookie(ASWV_URL, "DEVICE=android");
			cookieManager.setCookie(ASWV_URL, "DEV_API=" + Build.VERSION.SDK_INT);
			Log.d("COOKIES: ", cookieManager.getCookie(ASWV_URL));
		}
	}

	//Checking permission for storage and camera for writing and uploading images
	public void get_file(){
		String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

		//Checking for storage permission to write images for upload
		if (ASWP_FUPLOAD && ASWP_CAMUPLOAD && !check_permission(2) && !check_permission(3)) {
			ActivityCompat.requestPermissions(MainActivity.this, perms, file_perm);

		//Checking for WRITE_EXTERNAL_STORAGE permission
		} else if (ASWP_FUPLOAD && !check_permission(2)) {
			ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, file_perm);

		//Checking for CAMERA permissions
		} else if (ASWP_CAMUPLOAD && !check_permission(3)) {
			ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, file_perm);
		}
	}

    //Using cookies to update user locations
	public String get_location(){
		String newloc = "0,0";
		//Checking for location permissions
		if (ASWP_LOCATION && (Build.VERSION.SDK_INT < 23 || check_permission(1))) {
			GPSTrack gps;
			gps = new GPSTrack(MainActivity.this);
			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			if (gps.canGetLocation()) {
				if (latitude != 0 || longitude != 0) {
					if(true_online) {
						CookieManager cookieManager = CookieManager.getInstance();
						cookieManager.setAcceptCookie(true);
						cookieManager.setCookie(ASWV_URL, "lat=" + latitude);
						cookieManager.setCookie(ASWV_URL, "long=" + longitude);
					}
					//Log.w("New Updated Location:", latitude + "," + longitude);  //enable to test dummy latitude and longitude
					newloc = latitude+","+longitude;
				} else {
					Log.w("New Updated Location:", "NULL");
				}
			} else {
				show_notification(1, 1);
				Log.w("New Updated Location:", "FAIL");
			}
		}
		return newloc;
	}

	// get cookie value
	public String get_cookies(String cookie){
		String value = "";
		CookieManager cookieManager = CookieManager.getInstance();
		String cookies = cookieManager.getCookie(ASWV_URL);
		String[] temp=cookies.split(";");
		for (String ar1 : temp ){
			if(ar1.contains(cookie)){
				String[] temp1=ar1.split("=");
				value = temp1[1];
				break;
			}
		}
		return value;
	}

	private static final Pattern urlPattern = Pattern.compile(
			"(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"+"(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"+"[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	@SuppressLint("ResourceAsColor")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setQueryHint(getString(R.string.search_hint));
		searchView.setIconified(true);
		searchView.setIconifiedByDefault(true);
		searchView.clearFocus();

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				searchView.clearFocus();
				aswm_view(ASWV_SEARCH+query,false,asw_error_counter);
				searchView.setQuery(query,false);
				return false;
			}
			@Override
			public boolean onQueryTextChange(String query) {
				return false;
			}
		});
		//searchView.setQuery(asw_view.getUrl(),false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_exit) {
			aswm_exit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onNavigationItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.nav_home) {
			aswm_view("file:///android_asset/offline.html",false,asw_error_counter);
		} else if (id == R.id.nav_doc) {
			aswm_view("https://github.com/mgks/Android-SmartWebView/tree/master/documentation",false,asw_error_counter);
		} else if (id == R.id.nav_fcm) {
			aswm_view("https://github.com/mgks/Android-SmartWebView/blob/master/documentation/fcm.md",false,asw_error_counter);
		} else if (id == R.id.nav_admob) {
			aswm_view("https://github.com/mgks/Android-SmartWebView/blob/master/documentation/admob.md",false,asw_error_counter);
		} else if (id == R.id.nav_gps) {
			aswm_view("https://github.com/mgks/Android-SmartWebView/blob/master/documentation/gps.md",false,asw_error_counter);
		} else if (id == R.id.nav_share) {
			aswm_view("https://github.com/mgks/Android-SmartWebView/blob/master/documentation/share.md",false,asw_error_counter);
		} else if (id == R.id.nav_lay) {
			aswm_view("https://github.com/mgks/Android-SmartWebView/blob/master/documentation/layout.md",false,asw_error_counter);
		} else if (id == R.id.nav_support) {
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("mailto:getmgks@gmail.com"));
			intent.putExtra(Intent.EXTRA_SUBJECT, "SWV Help");
			startActivity(Intent.createChooser(intent, "Send Email"));
		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	public static int aswm_fcm_id(){
		//Date now = new Date();
		//Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
		return 1;
	}

	public String fcm_token(){
		FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this, instanceIdResult -> {
			fcm_token = instanceIdResult.getToken();
				if(true_online) {
					CookieManager cookieManager = CookieManager.getInstance();
					cookieManager.setAcceptCookie(true);
					cookieManager.setCookie(ASWV_URL, "FCM_TOKEN="+fcm_token);
					Log.d("FCM_BAKED","YES");
					//Log.d("COOKIES: ", cookieManager.getCookie(ASWV_URL));
				}
			Log.d("REQ_FCM_TOKEN", fcm_token);
		}).addOnFailureListener(e -> Log.d("REQ_FCM_TOKEN", "FAILED"));
		return fcm_token;
	}

	//Checking if particular permission is given or not
	public boolean check_permission(int permission){
		switch(permission){
			case 1:
				return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

			case 2:
				return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

			case 3:
				return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

		}
		return false;
	}

	//Creating image file for upload
    private File create_image() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String file_name    = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
        String new_name     = "file_"+file_name+"_";
        File sd_directory   = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(new_name, ".jpg", sd_directory);
    }

	//Creating video file for upload
    private File create_video() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String file_name    = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
        String new_name     = "file_"+file_name+"_";
        File sd_directory   = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(new_name, ".3gp", sd_directory);
    }

    //Launching app rating dialoge [developed by github.com/hotchemi]
    public void get_rating() {
        if (DetectConnection.isInternetAvailable(MainActivity.this)) {
            AppRate.with(this)
                .setStoreType(StoreType.GOOGLEPLAY)     //default is Google Play, other option is Amazon App Store
                .setInstallDays(SmartWebView.ASWR_DAYS)
                .setLaunchTimes(SmartWebView.ASWR_TIMES)
				.setRemindInterval(SmartWebView.ASWR_INTERVAL)
                .setTitle(R.string.rate_dialog_title)
                .setMessage(R.string.rate_dialog_message)
                .setTextLater(R.string.rate_dialog_cancel)
                .setTextNever(R.string.rate_dialog_no)
                .setTextRateNow(R.string.rate_dialog_ok)
                .monitor();
            AppRate.showRateDialogIfMeetsConditions(this);
        }
        //for more customizations, look for AppRate and DialogManager
    }

    //Creating custom notifications with IDs
    public void show_notification(int type, int id) {
        long when = System.currentTimeMillis();
        asw_notification = (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i = new Intent();
        if (type == 1) {
            i.setClass(MainActivity.this, MainActivity.class);
        } else if (type == 2) {
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        } else {
            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "");
        switch(type){
            case 1:
                builder.setTicker(getString(R.string.app_name));
                builder.setContentTitle(getString(R.string.loc_fail));
                builder.setContentText(getString(R.string.loc_fail_text));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.loc_fail_more)));
                builder.setVibrate(new long[]{350,350,350,350,350});
                builder.setSmallIcon(R.mipmap.ic_launcher);
            break;

            case 2:
                builder.setTicker(getString(R.string.app_name));
                builder.setContentTitle(getString(R.string.loc_perm));
                builder.setContentText(getString(R.string.loc_perm_text));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.loc_perm_more)));
                builder.setVibrate(new long[]{350, 700, 350, 700, 350});
                builder.setSound(alarmSound);
                builder.setSmallIcon(R.mipmap.ic_launcher);
            break;
        }
        builder.setOngoing(false);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setWhen(when);
        builder.setContentIntent(pendingIntent);
        asw_notification_new = builder.build();
        asw_notification.notify(id, asw_notification_new);
    }

    //Printing pages
	private void print_page(WebView view, String print_name, boolean manual) {
		PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
		PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(print_name);
		PrintAttributes.Builder builder = new PrintAttributes.Builder();
		builder.setMediaSize(PrintAttributes.MediaSize.ISO_A5);
		PrintJob printJob = printManager.print(print_name, printAdapter, builder.build());

		if(printJob.isCompleted()){
			Toast.makeText(getApplicationContext(), R.string.print_complete, Toast.LENGTH_LONG).show();
		}
		else if(printJob.isFailed()){
			Toast.makeText(getApplicationContext(), R.string.print_failed, Toast.LENGTH_LONG).show();
		}
	}

	//Checking if users allowed the requested permissions or not
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
		if (requestCode == 1) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				get_location();
			}
		}
	}

	//Action on back key tap/click
	@Override
	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (asw_view.canGoBack()) {
					asw_view.goBack();
				} else {
					if(ASWP_EXITDIAL) {
						ask_exit();
					}else{
						finish();
					}
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void aswm_exit(){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	// Creating exit dialogue
	public void ask_exit(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		builder.setTitle(getString(R.string.exit_title));
		builder.setMessage(getString(R.string.exit_subtitle));
		builder.setCancelable(true);

		// Action if user selects 'yes'
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				finish();
			}
		});

		// Actions if user selects 'no'
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
			}
		});

		// Create the alert dialog using alert dialog builder
		AlertDialog dialog = builder.create();

		// Finally, display the dialog when user press back button
		dialog.show();
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState ){
        super.onSaveInstanceState(outState);
        asw_view.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        asw_view.restoreState(savedInstanceState);
    }
}
