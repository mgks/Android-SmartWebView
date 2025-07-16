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
import android.app.SearchManager;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ServiceWorkerClient;
import android.webkit.ServiceWorkerController;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * Main Activity for Smart WebView
 * Handles WebView configuration, lifecycle events and user interactions
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Class members
    private static final String TAG = "MainActivity";

    private boolean isPageLoaded = false;

    static Functions fns = new Functions();
    private FileProcessing fileProcessing;
    private LinearLayout adContainer;
    private PermissionManager permissionManager;
    private ActivityResultLauncher<Intent> fileUploadLauncher;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        SWVContext.getPluginManager().onActivityResult(requestCode, resultCode, intent);
    }

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SplashScreen splashScreen = androidx.core.splashscreen.SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // If extending splash is enabled, set up a listener
        if (SWVContext.ASWP_EXTEND_SPLASH) {
            final View content = findViewById(android.R.id.content);
            content.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            // Check if the page is loaded.
                            if (isPageLoaded) {
                                // The content is ready; remove the listener and draw the content.
                                content.getViewTreeObserver().removeOnPreDrawListener(this);
                                return true;
                            } else {
                                // The content is not ready; don't draw anything.
                                return false;
                            }
                        }
                    }
            );
        }

        permissionManager = new PermissionManager(this);
        SWVContext.loadPlugins(this);

        // Initialize the ActivityResultLauncher here, before it's needed
        fileUploadLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Uri[] results = null;
                if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // If the file request was cancelled, we must send a null value
                    if (SWVContext.asw_file_path != null) {
                        SWVContext.asw_file_path.onReceiveValue(null);
                        SWVContext.asw_file_path = null; // Clear path after use
                    }
                    return;
                }

                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (null == SWVContext.asw_file_path) {
                        return;
                    }

                    Intent data = result.getData();

                    // Scenario 1: User selected files from the gallery/file manager
                    if (data != null && (data.getDataString() != null || data.getClipData() != null)) {
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            // Multiple files selected
                            final int numSelectedFiles = clipData.getItemCount();
                            results = new Uri[numSelectedFiles];
                            for (int i = 0; i < numSelectedFiles; i++) {
                                results[i] = clipData.getItemAt(i).getUri();
                            }
                        } else if (data.getDataString() != null) {
                            // Single file selected
                            results = new Uri[]{Uri.parse(data.getDataString())};
                        }
                    }

                    // Scenario 2: User took a photo or video using the camera intent
                    // If results is still null, check if a camera file path was set before launching the intent
                    if (results == null) {
                        if (SWVContext.asw_pcam_message != null) {
                            results = new Uri[]{Uri.parse(SWVContext.asw_pcam_message)};
                        } else if (SWVContext.asw_vcam_message != null) {
                            results = new Uri[]{Uri.parse(SWVContext.asw_vcam_message)};
                        }
                    }
                }

                // Send the results back to the WebView
                if (SWVContext.asw_file_path != null) {
                    SWVContext.asw_file_path.onReceiveValue(results);
                    SWVContext.asw_file_path = null;
                }

                // Clear camera messages after use
                SWVContext.asw_pcam_message = null;
                SWVContext.asw_vcam_message = null;
            }
        );


        // Initialize app context
        SWVContext.setAppContext(getApplicationContext());

        // Initialize file processing, passing the new launcher
        fileProcessing = new FileProcessing(this, fileUploadLauncher);

        // Set screen orientation from cookie or default
        String cookie_orientation = !SWVContext.ASWP_OFFLINE ? fns.get_cookies("ORIENT") : "";
        fns.set_orientation((!Objects.equals(cookie_orientation, "") ? Integer.parseInt(cookie_orientation) : SWVContext.ASWV_ORIENTATION), false, this);

        // Setup layout based on configuration
        setupLayout();

        // Initialize Smart WebView components
        initializeWebView();

        // Setup features and handle intents
        setupFeatures();
        handleIncomingIntents();

        // Debug mode logging
        if(SWVContext.SWV_DEBUGMODE){
            Log.d(TAG, "URL: "+ SWVContext.CURR_URL+"DEVICE INFO: "+ Arrays.toString(fns.get_info(this)));
        }
    }

    /**
     * Setup the UI layout based on configuration
     */
    private void setupLayout() {
        // Set content view based on configuration
        if (SWVContext.ASWV_LAYOUT == 1) {
            setContentView(R.layout.drawer_main);
            // Conditionally show or hide the header based on config
            if (SWVContext.ASWP_DRAWER_HEADER) {
                // Header is enabled: Setup Toolbar and Toggle
                findViewById(R.id.app_bar).setVisibility(View.VISIBLE);
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                final SwipeRefreshLayout pullRefresh = findViewById(R.id.pullfresh);

                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close) {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        super.onDrawerSlide(drawerView, slideOffset);
                        // This is the key part: disable pull-to-refresh while drawer is opening.
                        if (slideOffset > 0 && pullRefresh.isEnabled()) {
                            pullRefresh.setEnabled(false);
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                        // Re-enable pull-to-refresh only if the feature is globally enabled.
                        if (!pullRefresh.isEnabled() && SWVContext.ASWP_PULLFRESH) {
                            pullRefresh.setEnabled(true);
                        }
                    }
                };
                drawer.addDrawerListener(toggle);
                toggle.syncState();
                drawer.addDrawerListener(toggle);
                toggle.syncState();

            } else {
                // Header is disabled: Hide the AppBarLayout completely
                findViewById(R.id.app_bar).setVisibility(View.GONE);
            }

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            // The footer is part of the NavigationView's own view hierarchy.
            MenuItem switchItem = navigationView.getMenu().findItem(R.id.nav_dark_mode_switch);
            SwitchCompat themeSwitch = (SwitchCompat) switchItem.getActionView().findViewById(R.id.drawer_theme_switch);
            if (themeSwitch != null) {
                int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                themeSwitch.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES);

                themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    AppCompatDelegate.setDefaultNightMode(
                            isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                    );
                });
            }

        } else {
            setContentView(R.layout.activity_main);
        }

        // Initialize UI components
        SWVContext.asw_view = findViewById(R.id.msw_view);
        adContainer = findViewById(R.id.msw_ad_container);
        SWVContext.print_view = findViewById(R.id.print_view);

        // Setup window appearance
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    /**
     * Initialize WebView and its settings
     */
    private void initializeWebView() {
        // Initialize Smart WebView with current context. This will set up the PluginManager.
        SWVContext.init(this, SWVContext.asw_view, fns);

        // Instantiate Playground and register it with the manager
        Playground playground = new Playground(this, SWVContext.asw_view, fns);
        SWVContext.getPluginManager().setPlayground(playground);

        // Configure WebView settings
        WebSettings webSettings = SWVContext.asw_view.getSettings();

        // Configure user agent
        if (SWVContext.OVERRIDE_USER_AGENT || SWVContext.POSTFIX_USER_AGENT) {
            String userAgent = webSettings.getUserAgentString();
            if (SWVContext.OVERRIDE_USER_AGENT) {
                userAgent = SWVContext.CUSTOM_USER_AGENT;
            }
            if (SWVContext.POSTFIX_USER_AGENT) {
                userAgent = userAgent + " " + SWVContext.USER_AGENT_POSTFIX;
            }
            webSettings.setUserAgentString(userAgent);
        }

        // Configure WebView settings
        // WARNING: setJavaScriptEnabled can introduce XSS vulnerabilities.
        // Ensure you are loading only trusted content (your own website) and
        // that you have sanitized any user-submitted content on your server.
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(SWVContext.ASWP_SFORM);
        webSettings.setSupportZoom(SWVContext.ASWP_ZOOM);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Disable copy-paste if configured
        if (!SWVContext.ASWP_COPYPASTE) {
            SWVContext.asw_view.setOnLongClickListener(v -> true);
        }

        // Set WebView properties
        SWVContext.asw_view.setHapticFeedbackEnabled(false);
        SWVContext.asw_view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        SWVContext.asw_view.setVerticalScrollBarEnabled(false);

        // Set WebView clients
        SWVContext.asw_view.setWebViewClient(new WebViewCallback());
        SWVContext.asw_view.setWebChromeClient(createWebChromeClient());
        SWVContext.asw_view.setBackgroundColor(getColor(R.color.colorPrimary));
        SWVContext.asw_view.addJavascriptInterface(new WebAppInterface(), "AndroidInterface");

        // Setup download listener
        setupDownloadListener();
    }

    /**
     * Setup the download listener for WebView
     */
    private void setupDownloadListener() {
        SWVContext.asw_view.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            // We only need storage permission for downloads on older Android versions.
            // On modern Android, DownloadManager handles it. But a check is still good practice.
            if (!permissionManager.isStoragePermissionGranted()) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionManager.STORAGE_REQUEST_CODE);
                Toast.makeText(this, "Storage permission is required to download files.", Toast.LENGTH_LONG).show();
            } else {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
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
                if(SWVContext.SWV_DEBUGMODE) {
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
                if (SWVContext.ASWP_PBAR) {
                    if (SWVContext.asw_progress == null) SWVContext.asw_progress = findViewById(R.id.msw_progress);
                    SWVContext.asw_progress.setProgress(p);
                    if (p == 100) {
                        SWVContext.asw_progress.setProgress(0);
                    }
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if (permissionManager.isLocationPermissionGranted()) {
                    callback.invoke(origin, true, false);
                } else {
                    // If permission is not granted, we should request it.
                    // We can re-use the initial request logic.
                    permissionManager.requestInitialPermissions();
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
        if (SWVContext.ASWP_PBAR) {
            SWVContext.asw_progress = findViewById(R.id.msw_progress);
        } else {
            findViewById(R.id.msw_progress).setVisibility(View.GONE);
        }
        SWVContext.asw_loading_text = findViewById(R.id.msw_loading_text);

        // Log device info and handle location permissions
        fns.get_info(this);

        // A Centralized Permission Request on Launch
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            permissionManager.requestInitialPermissions();
        }, 1500);

        // Get FCM token for notifications
        setupFirebaseMessaging();
    }

    // Options menu for drawer theme
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.search_hint));
        }
        assert searchView != null;
        searchView.setIconified(true);
        searchView.setIconifiedByDefault(true);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                fns.aswm_view(SWVContext.ASWV_SEARCH + query, false, SWVContext.asw_error_counter, MainActivity.this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            fns.exit_app(this);
            return true;
        }
        return onOptionsItemSelected(item);
    }

    /**
     * Navigation menu item setup, config in SmartWebView.java
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Look up the configuration for the clicked item
        SWVContext.NavItem navItem = SWVContext.ASWV_DRAWER_MENU.get(id);

        if (navItem != null) {
            String action = navItem.action;

            if (action.startsWith("mailto:")) {
                // Handle special mailto action
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(action));
                try {
                    startActivity(Intent.createChooser(intent, "Send Email"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle standard URL action
                fns.aswm_view(action, false, 0, this);
            }
        } else {
            // Optional: Log if a menu item is clicked but not configured
            Log.w(TAG, "No action configured for menu item ID: " + id);
        }

        // Close the drawer
        if (SWVContext.ASWV_LAYOUT == 1) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer != null) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }
        return true;
    }

    /**
     * Setup notification channel for Android Oreo and above
     */
    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel(
                    SWVContext.asw_fcm_channel,
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

        if (SWVContext.ASWP_PULLFRESH) {
            pullRefresh.setOnRefreshListener(() -> {
                // Pass the current activity context to the pull_fresh method
                fns.pull_fresh(MainActivity.this);
                pullRefresh.setRefreshing(false);
            });

            // Only enable pull-to-refresh when at the top of the page
            SWVContext.asw_view.getViewTreeObserver().addOnScrollChangedListener(
                    () -> pullRefresh.setEnabled(SWVContext.asw_view.getScrollY() == 0));
        } else {
            pullRefresh.setRefreshing(false);
            pullRefresh.setEnabled(false);
        }
    }

    /**
     * Sets the app's theme to light or dark and restarts the activity to apply changes.
     * @param isDarkMode true for dark mode, false for light mode.
     */
    private void setAppTheme(boolean isDarkMode) {
        int mode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);
        // No need to restart for modern apps, but if UI glitches appear, a restart can be forced.
        // Forcing a restart:
        // Intent intent = getIntent();
        // finish();
        // startActivity(intent);
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
            fns.aswm_view(SWVContext.ASWV_URL, false, SWVContext.asw_error_counter, this);
        } else if (uri != null) {
            Log.d(TAG, "Notification intent: " + uri);
            fns.aswm_view(uri, false, SWVContext.asw_error_counter, this);
        } else if (intent.getData() != null) {
            String path = intent.getDataString();
            fns.aswm_view(path, false, SWVContext.asw_error_counter, this);
        } else {
            Log.d(TAG, "Main intent: " + SWVContext.ASWV_URL);
            fns.aswm_view(SWVContext.ASWV_URL, false, SWVContext.asw_error_counter, this);
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
        String redirectUrl = SWVContext.ASWV_SHARE_URL +
                "?text=" + share +
                "&link=" + urlStr +
                "&image_url=";

        fns.aswm_view(redirectUrl, false, SWVContext.asw_error_counter, this);
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void setNativeTheme(String theme) {
            runOnUiThread(() -> {
                int newMode;
                if ("dark".equals(theme)) {
                    newMode = AppCompatDelegate.MODE_NIGHT_YES;
                } else if ("light".equals(theme)) {
                    newMode = AppCompatDelegate.MODE_NIGHT_NO;
                } else {
                    newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                }
                if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                    AppCompatDelegate.setDefaultNightMode(newMode);
                }
            });
        }
    }

    // Standard activity lifecycle methods
    @Override
    public void onPause() {
        super.onPause();
        SWVContext.asw_view.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        SWVContext.asw_view.onResume();
        SWVContext.getPluginManager().onResume();

        // Update recent apps appearance
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(
                getString(R.string.app_name), bm, getColor(R.color.colorPrimary));
        setTaskDescription(taskDesc);
    }

    @Override
    protected void onDestroy() {
        SWVContext.getPluginManager().onDestroy();
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
        String theme = (newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES ? "dark" : "light";
        String script = "if(typeof setTheme === 'function') { setTheme('" + theme + "', true); }";
        if (SWVContext.asw_view != null) {
            SWVContext.asw_view.evaluateJavascript(script, null);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SWVContext.asw_view.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        SWVContext.asw_view.restoreState(savedInstanceState);
    }

    /**
     * Handle back button press
     */
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if (SWVContext.asw_view.canGoBack()) {
                SWVContext.asw_view.goBack();
            } else {
                if (SWVContext.ASWP_EXITDIAL) {
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

        SWVContext.getPluginManager().onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionManager.INITIAL_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Location permission granted.");
                        // We can now safely get the location

                    } else {
                        Log.w(TAG, "Location permission denied.");
                    }
                } else if (permissions[i].equals(Manifest.permission.POST_NOTIFICATIONS)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Notification permission granted.");

                        // Send a test notification under debug mode
                        if(SWVContext.SWV_DEBUGMODE) {
                            Firebase firebase = new Firebase();
                            firebase.sendMyNotification(
                                    "Yay! Firebase is working",
                                    "This is a test notification in action.",
                                    "OPEN_URI",
                                    SWVContext.ASWV_URL,
                                    null,
                                    String.valueOf(SWVContext.ASWV_FCM_ID),
                                    getApplicationContext());
                        }
                    } else {
                        Log.w(TAG, "Notification permission denied.");
                    }
                }
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
            SWVContext.getPluginManager().onPageStarted(url);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            SWVContext.getPluginManager().onPageFinished(url);

            findViewById(R.id.msw_welcome).setVisibility(View.GONE);
            findViewById(R.id.msw_view).setVisibility(View.VISIBLE);
            isPageLoaded = true;

            // Inject Google Analytics if configured
            if (!url.startsWith("file://") && SWVContext.ASWV_GTAG != null && !SWVContext.ASWV_GTAG.isEmpty()) {
                fns.inject_gtag(view, SWVContext.ASWV_GTAG);
            }

            // Inject theme preference
            String theme = SWVContext.ASWP_DARK_MODE ? "dark" : "light";
            String script = "if(typeof applyInitialTheme === 'function') { applyInitialTheme('" + theme + "'); }";
            view.evaluateJavascript(script, null);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();

            if (SWVContext.getPluginManager().shouldOverrideUrlLoading(view, url)) {
                return true;
            }

            if (url.matches("^(https?|file)://.*$")) {
                SWVContext.CURR_URL = url;
            }
            return fns.url_actions(view, url, MainActivity.this);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            // This method is called when a page load fails.
            // We will ignore errors for non-main frame resources (like images or CSS).
            if (request.isForMainFrame()) {
                // Check if the error is a network-related issue.
                // A list of common network error codes for Android WebView.
                int errorCode = error.getErrorCode();
                if (errorCode == ERROR_HOST_LOOKUP ||
                        errorCode == ERROR_TIMEOUT ||
                        errorCode == ERROR_CONNECT ||
                        errorCode == ERROR_UNKNOWN ||
                        errorCode == ERROR_IO) {

                    Log.e(TAG, "Network Error Occurred: " + error.getDescription());

                    // Redirect to the custom offline URL.
                    // It's important to use post() to avoid issues with modifying the WebView
                    // while it's in the middle of a callback.
                    view.post(() -> {
                        // First, try to load the primary offline page
                        if (SWVContext.ASWV_OFFLINE_URL != null && !SWVContext.ASWV_OFFLINE_URL.isEmpty()) {
                            view.loadUrl(SWVContext.ASWV_OFFLINE_URL);
                        } else {
                            // As a final fallback, load the basic error page
                            view.loadUrl("file:///android_asset/error.html");
                        }
                    });
                }
            }
            super.onReceivedError(view, request, error);
        }

        @SuppressLint("WebViewClientOnReceivedSslError")
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (SWVContext.ASWP_CERT_VERI) {
                super.onReceivedSslError(view, handler, error);
            } else {
                handler.proceed();
                if (SWVContext.SWV_DEBUGMODE) {
                    Toast.makeText(MainActivity.this, "SSL Error: " + error.getPrimaryError(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                        WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (SWVContext.SWV_DEBUGMODE) {
                Log.e(TAG, "HTTP Error loading " + request.getUrl().toString() +
                        ": " + errorResponse.getStatusCode());
            }
        }
    }
}