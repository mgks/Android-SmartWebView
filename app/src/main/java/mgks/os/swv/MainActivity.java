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
import android.os.Looper;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
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

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * Main Activity for Smart WebView
 * Handles WebView configuration, lifecycle events and user interactions
 */
public class MainActivity extends AppCompatActivity {
    // Class members
    private static final String TAG = "MainActivity";

    static Functions fns = new Functions();
    private FileProcessing fileProcessing;
    private LinearLayout adContainer;
    private ActivityResultLauncher<Intent> fileUploadLauncher;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        SmartWebView.getPluginManager().onActivityResult(requestCode, resultCode, intent);
    }

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the ActivityResultLauncher here, before it's needed
        fileUploadLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Uri[] results = null;
                    if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // If the file request was cancelled, we must send a null value
                        if (SmartWebView.asw_file_path != null) {
                            SmartWebView.asw_file_path.onReceiveValue(null);
                        }
                        return;
                    }
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (null == SmartWebView.asw_file_path) {
                            return;
                        }
                        ClipData clipData;
                        String stringData;
                        try {
                            clipData = result.getData().getClipData();
                            stringData = result.getData().getDataString();
                        } catch (Exception e) {
                            clipData = null;
                            stringData = null;
                        }

                        if (clipData == null && stringData == null && (SmartWebView.asw_pcam_message != null || SmartWebView.asw_vcam_message != null)) {
                            results = new Uri[]{Uri.parse(SmartWebView.asw_pcam_message != null ? SmartWebView.asw_pcam_message : SmartWebView.asw_vcam_message)};
                        } else {
                            if (null != clipData) {
                                final int numSelectedFiles = clipData.getItemCount();
                                results = new Uri[numSelectedFiles];
                                for (int i = 0; i < numSelectedFiles; i++) {
                                    results[i] = clipData.getItemAt(i).getUri();
                                }
                            } else {
                                try {
                                    Bitmap cam_photo = (Bitmap) result.getData().getExtras().get("data");
                                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                    cam_photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                    stringData = MediaStore.Images.Media.insertImage(getContentResolver(), cam_photo, null, null);
                                } catch (Exception ignored) {}
                                results = new Uri[]{Uri.parse(stringData)};
                            }
                        }
                    }
                    if (SmartWebView.asw_file_path != null) {
                        SmartWebView.asw_file_path.onReceiveValue(results);
                        SmartWebView.asw_file_path = null;
                    }
                }
        );

        // Initialize app context
        SmartWebView.setAppContext(getApplicationContext());

        // Initialize file processing, passing the new launcher
        fileProcessing = new FileProcessing(this, fileUploadLauncher);

        // Set screen orientation from cookie or default
        String cookie_orientation = !SmartWebView.ASWP_OFFLINE ? fns.get_cookies("ORIENT") : "";
        fns.set_orientation((!Objects.equals(cookie_orientation, "") ? Integer.parseInt(cookie_orientation) : SmartWebView.ASWV_ORIENTATION), false, this);

        // Setup layout based on configuration
        setupLayout();

        // Initialize Smart WebView components
        initializeWebView();

        // Setup features and handle intents
        setupFeatures();
        handleIncomingIntents();

        // Debug mode logging
        if(SmartWebView.SWV_DEBUGMODE){
            Log.d(TAG, "URL: "+SmartWebView.CURR_URL+"DEVICE INFO: "+ Arrays.toString(fns.get_info()));
        }
    }

    /**
     * Setup the UI layout based on configuration
     */
    private void setupLayout() {
        // Set content view based on configuration
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
            navigationView.setNavigationItemSelectedListener(fns);
        } else {
            setContentView(R.layout.activity_main);
        }

        // Initialize UI components
        SmartWebView.asw_view = findViewById(R.id.msw_view);
        adContainer = findViewById(R.id.msw_ad_container);
        SmartWebView.print_view = findViewById(R.id.print_view);

        // Setup window appearance
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    /**
     * Initialize WebView and its settings
     */
    private void initializeWebView() {
        // Initialize Smart WebView with current context. This will set up the PluginManager.
        SmartWebView.init(this, SmartWebView.asw_view, fns);

        // Instantiate Playground. It will now correctly wait for the onPluginsInitialized callback.
        new Playground(this, SmartWebView.asw_view, fns);

        // Configure WebView settings
        WebSettings webSettings = SmartWebView.asw_view.getSettings();

        // Configure user agent
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

        // Configure WebView settings
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(SmartWebView.ASWP_SFORM);
        webSettings.setSupportZoom(SmartWebView.ASWP_ZOOM);
        webSettings.setGeolocationEnabled(SmartWebView.ASWP_LOCATION);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Disable copy-paste if configured
        if (!SmartWebView.ASWP_COPYPASTE) {
            SmartWebView.asw_view.setOnLongClickListener(v -> true);
        }

        // Set WebView properties
        SmartWebView.asw_view.setHapticFeedbackEnabled(false);
        SmartWebView.asw_view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        SmartWebView.asw_view.setVerticalScrollBarEnabled(false);

        // Set WebView clients
        SmartWebView.asw_view.setWebViewClient(new WebViewCallback());
        SmartWebView.asw_view.setWebChromeClient(createWebChromeClient());

        // Setup download listener
        setupDownloadListener();
    }

    /**
     * Setup the download listener for WebView
     */
    private void setupDownloadListener() {
        SmartWebView.asw_view.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            if (!fns.check_permission(2, getApplicationContext()) && !fns.check_permission(3, getApplicationContext())) {
                fns.get_permissions(3, MainActivity.this);
            } else {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setMimeType(mimeType);
                request.addRequestHeader("cookie", fns.get_cookies(""));
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription(getString(R.string.dl_downloading));
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        URLUtil.guessFileName(url, contentDisposition, mimeType));

                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                assert dm != null;
                dm.enqueue(request);
                Toast.makeText(this, getString(R.string.dl_downloading2), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Create the WebChromeClient for WebView
     */
    private WebChromeClient createWebChromeClient() {
        return new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if(SmartWebView.SWV_DEBUGMODE) {
                    Log.d("SWV_JS", consoleMessage.message() + " -- From line " +
                            consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                }
                return true;
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                return fileProcessing.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            @Override
            public void onProgressChanged(WebView view, int p) {
                if (SmartWebView.ASWP_PBAR) {
                    if (SmartWebView.asw_progress == null) SmartWebView.asw_progress = findViewById(R.id.msw_progress);
                    SmartWebView.asw_progress.setProgress(p);
                    if (p == 100) {
                        SmartWebView.asw_progress.setProgress(0);
                    }
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if (fns.check_permission(1, getApplicationContext())) {
                    callback.invoke(origin, true, false);
                } else {
                    fns.get_permissions(1, MainActivity.this);
                }
            }
        };
    }

    /**
     * Setup various features based on configuration
     */
    private void setupFeatures() {
        // Setup service worker if supported
        ServiceWorkerController.getInstance().setServiceWorkerClient(new ServiceWorkerClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebResourceRequest request) {
                return null;
            }
        });

        // Prevent app from being started again when it is still alive in the background
        if (!isTaskRoot()) {
            finish();
            return;
        }

        // Initialize notification channel on Android 8+
        setupNotificationChannel();

        // Setup swipe refresh functionality
        setupSwipeRefresh();

        // Setup progress bar if enabled
        if (SmartWebView.ASWP_PBAR) {
            SmartWebView.asw_progress = findViewById(R.id.msw_progress);
        } else {
            findViewById(R.id.msw_progress).setVisibility(View.GONE);
        }
        SmartWebView.asw_loading_text = findViewById(R.id.msw_loading_text);

        // Setup app rating request if enabled
        if (SmartWebView.ASWP_RATINGS) {
            new Handler().postDelayed(() -> fns.get_rating(this), 60000);
        }

        // Log device info and handle location permissions
        fns.get_info();
        handleLocationPermission();

        // Get FCM token for notifications
        setupFirebaseMessaging();
    }

    /**
     * Setup notification channel for Android Oreo and above
     */
    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel(
                    SmartWebView.asw_fcm_channel,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription(getString(R.string.notification_channel_desc));
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setShowBadge(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Setup swipe refresh functionality
     */
    private void setupSwipeRefresh() {
        final SwipeRefreshLayout pullRefresh = findViewById(R.id.pullfresh);

        if (SmartWebView.ASWP_PULLFRESH) {
            pullRefresh.setOnRefreshListener(() -> {
                fns.pull_fresh(getApplicationContext());
                pullRefresh.setRefreshing(false);
            });

            // Only enable pull-to-refresh when at the top of the page
            SmartWebView.asw_view.getViewTreeObserver().addOnScrollChangedListener(
                    () -> pullRefresh.setEnabled(SmartWebView.asw_view.getScrollY() == 0));
        } else {
            pullRefresh.setRefreshing(false);
            pullRefresh.setEnabled(false);
        }
    }

    /**
     * Handle location permission and tracking
     */
    private void handleLocationPermission() {
        if (SmartWebView.ASWP_LOCATION && !fns.check_permission(1, getApplicationContext())) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    SmartWebView.loc_perm);
        } else if (SmartWebView.ASWP_LOCATION) {
            fns.get_location(getApplicationContext());
        }
    }

    /**
     * Setup Firebase Cloud Messaging
     */
    private void setupFirebaseMessaging() {
        fns.fcm_token(new Functions.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                Log.d(TAG, "FCM Token received: " + token);
            }

            @Override
            public void onTokenFailed(Exception e) {
                Log.e(TAG, "Failed to retrieve FCM token", e);
            }
        });
    }

    /**
     * Handle incoming intents for notifications, shared content, etc.
     */
    private void handleIncomingIntents() {
        Intent intent = getIntent();
        Log.d(TAG, "Intent: " + intent.toUri(0));

        String uri = intent.getStringExtra("uri");
        String share = intent.getStringExtra("s_uri");
        String shareImg = intent.getStringExtra("s_img");

        if (share != null) {
            handleSharedText(share);
        } else if (shareImg != null) {
            Log.d(TAG, "Share image intent: " + shareImg);
            Toast.makeText(this, shareImg, Toast.LENGTH_LONG).show();
            fns.aswm_view(SmartWebView.ASWV_URL, false, SmartWebView.asw_error_counter, getApplicationContext());
        } else if (uri != null) {
            Log.d(TAG, "Notification intent: " + uri);
            fns.aswm_view(uri, false, SmartWebView.asw_error_counter, getApplicationContext());
        } else if (intent.getData() != null) {
            String path = intent.getDataString();
            fns.aswm_view(path, false, SmartWebView.asw_error_counter, getApplicationContext());
        } else {
            Log.d(TAG, "Main intent: " + SmartWebView.ASWV_URL);
            fns.aswm_view(SmartWebView.ASWV_URL, false, SmartWebView.asw_error_counter, getApplicationContext());
        }
    }

    /**
     * Handle shared text content
     */
    private void handleSharedText(String share) {
        Log.d(TAG, "Share text intent: " + share);

        // Extract URL from shared text
        Matcher matcher = Functions.url_pattern().matcher(share);
        String urlStr = "";

        if (matcher.find()) {
            urlStr = matcher.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
        }

        // Create sharing URL
        String redirectUrl = SmartWebView.ASWV_SHARE_URL +
                "?text=" + share +
                "&link=" + urlStr +
                "&image_url=";

        fns.aswm_view(redirectUrl, false, SmartWebView.asw_error_counter, getApplicationContext());
    }

    // Standard activity lifecycle methods
    @Override
    public void onPause() {
        super.onPause();
        SmartWebView.asw_view.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        SmartWebView.asw_view.onResume();

        // Update recent apps appearance
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(
                getString(R.string.app_name), bm, getColor(R.color.colorPrimary));
        setTaskDescription(taskDesc);

        // Update location if enabled
        if (SmartWebView.ASWP_LOCATION) {
            fns.get_location(getApplicationContext());
        }
    }

    @Override
    protected void onDestroy() {
        SmartWebView.getPluginManager().onDestroy();
        super.onDestroy();
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

    /**
     * Handle back button press
     */
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if (SmartWebView.asw_view.canGoBack()) {
                SmartWebView.asw_view.goBack();
            } else {
                if (SmartWebView.ASWP_EXITDIAL) {
                    fns.ask_exit(this);
                } else {
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Handle permission request results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        SmartWebView.getPluginManager().onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SmartWebView.loc_perm) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (SmartWebView.ASWP_LOCATION) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            fns.get_location(getApplicationContext()));
                }
            }
        } else if (requestCode == SmartWebView.file_perm || requestCode == SmartWebView.cam_perm) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Could re-trigger file chooser here if needed, but for now we let the user re-initiate
            }
        } else if (requestCode == SmartWebView.noti_perm) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Send a test notification
                Firebase firebase = new Firebase();
                firebase.sendMyNotification(
                        "Yay! Firebase is working",
                        "This is a test notification in action.",
                        "OPEN_URI",
                        SmartWebView.ASWV_URL,
                        null,
                        String.valueOf(SmartWebView.ASWV_FCM_ID),
                        getApplicationContext());
            }
        }
    }

    /**
     * WebView client implementation
     */
    private class WebViewCallback extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            SmartWebView.getPluginManager().onPageStarted(url);
            fns.get_location(getApplicationContext());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            SmartWebView.getPluginManager().onPageFinished(url);

            findViewById(R.id.msw_welcome).setVisibility(View.GONE);
            findViewById(R.id.msw_view).setVisibility(View.VISIBLE);
            if(SmartWebView.ASWV_GTAG != null && !SmartWebView.ASWV_GTAG.isEmpty()) {
                fns.inject_gtag(view, SmartWebView.ASWV_GTAG);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();

            if (SmartWebView.getPluginManager().shouldOverrideUrlLoading(view, url)) {
                return true;
            }

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
                super.onReceivedSslError(view, handler, error);
            } else {
                handler.proceed();
                if (SmartWebView.SWV_DEBUGMODE) {
                    Toast.makeText(MainActivity.this, "SSL Error: " + error.getPrimaryError(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                        WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (SmartWebView.SWV_DEBUGMODE) {
                Log.e(TAG, "HTTP Error loading " + request.getUrl().toString() +
                        ": " + errorResponse.getStatusCode());
            }
        }
    }
}