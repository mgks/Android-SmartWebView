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

import android.app.Application;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import dalvik.system.DexFile;

/**
 * Configuration and utility class for Smart WebView.
 * This class now loads its configuration from 'assets/swv.properties'
 * and contains nested classes for handling configuration loading and app initialization.
 */
public class SWVContext {

    private static final String TAG = "SmartWebView";
    public static boolean SWV_DEBUGMODE;

    // ===========================================
    // CONFIGURATION VARIABLES
    // These are populated at runtime by loadConfig()
    // ===========================================

    // URL configurations
    public static String ASWV_APP_URL;
    public static String ASWV_OFFLINE_URL;
    public static String ASWV_SEARCH;
    public static String ASWV_SHARE_URL_SUFFIX;
    public static String ASWV_EXC_LIST;

    // Feature Flags
    public static boolean ASWP_FUPLOAD;
    public static boolean ASWP_CAMUPLOAD;
    public static boolean ASWP_MULFILE;
    public static boolean ASWP_CUSTOM_CSS;
    public static boolean ASWP_COPYPASTE;
    public static boolean ASWP_PULLFRESH;
    public static boolean ASWP_PBAR;
    public static boolean ASWP_ZOOM;
    public static boolean ASWP_SFORM;
    public static boolean ASWP_EXTURL;
    public static boolean ASWP_TAB;
    public static boolean ASWP_EXIT_ON_BACK;
    public static boolean ASWP_EXITDIAL;

    // Security
    public static boolean ASWP_CERT_VERI;
    public static boolean ASWP_BLOCK_SCREENSHOTS;
    public static boolean ASWP_ACCEPT_THIRD_PARTY_COOKIES;

    // UI & Theme
    public static int ASWV_ORIENTATION;
    public static int ASWV_LAYOUT;
    public static boolean ASWP_DARK_MODE; // will be set dynamically
    public static boolean ASWP_DRAWER_HEADER;
    public static boolean ASWP_EXTEND_SPLASH;

    // User Agent
    public static boolean POSTFIX_USER_AGENT;
    public static String USER_AGENT_POSTFIX;
    public static boolean OVERRIDE_USER_AGENT;
    public static String CUSTOM_USER_AGENT;

    // Analytics
    public static String ASWV_GTAG;

    // Plugins & Permissions
    public static String[] ASWP_ENABLED_PLUGINS;
    public static boolean SWV_PLAYGROUND;
    public static String[] ASWP_REQUIRED_PERMISSIONS;

    // ===========================================
    // DERIVED & STATE VARIABLES
    // ===========================================
    public static String ASWV_URL;
    public static String ASWV_SHARE_URL;
    public static String ASWV_HOST;
    public static String CURR_URL;
    public static boolean ASWP_OFFLINE;

    // Shared UI components and state
    public static WebView asw_view;
    public static WebView print_view;
    public static CookieManager cookie_manager;
    public static ProgressBar asw_progress;
    public static TextView asw_loading_text;
    public static NotificationManager asw_notification;
    public static Notification asw_notification_new;
    public static ValueCallback<Uri[]> asw_file_path;
    public static String fcm_token;
    public static String asw_pcam_message;
    public static String asw_vcam_message;
    public static String asw_fcm_channel = "1";
    public static int ASWV_FCM_ID = (int) System.currentTimeMillis();
    public static int asw_error_counter = 0;
    public static boolean true_online = true;

    // ===========================================
    // PLUGINS CONFIGURATION
    // ===========================================
    public static int ASWR_DAYS;
    public static int ASWR_TIMES;
    public static int ASWR_INTERVAL;
    public static boolean ASWP_BIOMETRIC_ON_LAUNCH;

    // ===========================================
    // INITIALIZATION MANAGEMENT
    // ===========================================
    private static Context appContext;
    private static PluginManager pluginManagerInstance;
    private static boolean arePluginsInitialized = false;
    private static final List<Runnable> onInitCallbacks = new ArrayList<>();

    /**
     * Nested static class to handle loading the configuration file.
     * This is the "ConfigManager" logic, but living inside SmartWebView.
     */
    private static class ConfigLoader {
        private static final String TAG = "SWV_ConfigLoader";
        private static final String CONFIG_FILE = "swv.properties";
        private final Properties properties;

        private ConfigLoader(Context context) {
            properties = new Properties();
            AssetManager assetManager = context.getAssets();
            try (InputStream inputStream = assetManager.open(CONFIG_FILE)) {
                properties.load(inputStream);
                Log.d(TAG, "Configuration loaded successfully from " + CONFIG_FILE);
            } catch (IOException e) {
                Log.e(TAG, "WARNING: Could not load swv.properties from assets. Using default values.", e);
            }
        }

        public String getString(String key, String defaultValue) { return properties.getProperty(key, defaultValue); }
        public boolean getBoolean(String key, boolean defaultValue) { return Boolean.parseBoolean(properties.getProperty(key, Boolean.toString(defaultValue))); }
        public int getInt(String key, int defaultValue) {
            try { return Integer.parseInt(properties.getProperty(key, Integer.toString(defaultValue))); }
            catch (NumberFormatException e) { return defaultValue; }
        }
        public String[] getStringArray(String key, String[] defaultValue) {
            String value = properties.getProperty(key);
            if (value == null || value.trim().isEmpty()) { return defaultValue; }
            return value.split("\\s*,\\s*");
        }
    }

    /**
     * This is our new central initialization method.
     * It's called once from the custom Application class.
     */
    public static void loadConfig(Context context) {
        ConfigLoader config = new ConfigLoader(context);

        // --- Debug ---
        SWV_DEBUGMODE = config.getBoolean("debug.mode", true);

        // --- URL Configuration ---
        ASWV_APP_URL = config.getString("app.url", "https://mgks.github.io/Android-SmartWebView/");
        ASWV_OFFLINE_URL = config.getString("offline.url", "file:///android_asset/web/offline.html");
        ASWV_SEARCH = config.getString("search.url", "https://www.google.com/search?q=");
        ASWV_SHARE_URL_SUFFIX = config.getString("share.url.suffix", "/?share=");
        ASWV_EXC_LIST = config.getString("external.url.exception.list", "mgks.dev,docs.mgks.dev,mgks.github.io");

        // --- Feature Flags ---
        ASWP_FUPLOAD = config.getBoolean("feature.uploads", true);
        ASWP_CAMUPLOAD = config.getBoolean("feature.camera.uploads", true);
        ASWP_MULFILE = config.getBoolean("feature.multiple.uploads", true);
        ASWP_CUSTOM_CSS = config.getBoolean("feature.custom.css", false);
        ASWP_COPYPASTE = config.getBoolean("feature.copy.paste", true);
        ASWP_PULLFRESH = config.getBoolean("feature.pull.refresh", true);
        ASWP_PBAR = config.getBoolean("feature.progress.bar", true);
        ASWP_ZOOM = config.getBoolean("feature.zoom", false);
        ASWP_SFORM = config.getBoolean("feature.save.form", false);
        ASWP_EXTURL = config.getBoolean("feature.open.external.urls", true);
        ASWP_TAB = config.getBoolean("feature.chrome.tabs", true);
        ASWP_EXIT_ON_BACK = config.getBoolean("behavior.back.exits", false);
        ASWP_EXITDIAL = config.getBoolean("feature.exit.dialog", true);

        // --- Security ---
        ASWP_CERT_VERI = config.getBoolean("security.verify.ssl", true);
        ASWP_BLOCK_SCREENSHOTS = config.getBoolean("security.block.screenshots", false);
        ASWP_ACCEPT_THIRD_PARTY_COOKIES = config.getBoolean("security.accept.thirdparty.cookies", false);

        // --- UI & Theme ---
        ASWV_ORIENTATION = config.getInt("ui.orientation", 0);
        ASWV_LAYOUT = config.getInt("ui.layout", 1);
        ASWP_DRAWER_HEADER = config.getBoolean("ui.drawer.header", true);
        ASWP_EXTEND_SPLASH = config.getBoolean("ui.splash.extend", true);

        // --- User Agent ---
        POSTFIX_USER_AGENT = config.getBoolean("agent.postfix.enabled", true);
        USER_AGENT_POSTFIX = config.getString("agent.postfix.value", "SWVAndroid");
        OVERRIDE_USER_AGENT = config.getBoolean("agent.override.enabled", false);
        CUSTOM_USER_AGENT = config.getString("agent.override.value", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36");

        // --- Analytics ---
        ASWV_GTAG = config.getString("analytics.gtag.id", "G-7XXC1C7CRQ");

        // --- Plugins & Permissions ---
        ASWP_ENABLED_PLUGINS = config.getStringArray("plugins.enabled", new String[]{"AdMobPlugin","JSInterfacePlugin","ToastPlugin","QRScannerPlugin","BiometricPlugin","ImageCompressionPlugin"});
        SWV_PLAYGROUND = config.getBoolean("plugins.playground.enabled", true);
        ASWP_REQUIRED_PERMISSIONS = config.getStringArray("permissions.on.launch", new String[]{"NOTIFICATIONS", "LOCATION"});
        // Plugin Configurations
        // RatingsPlugin
        ASWR_DAYS = config.getInt("rating.install.days", 3);
        ASWR_TIMES = config.getInt("rating.launch.times", 10);
        ASWR_INTERVAL = config.getInt("rating.remind.interval", 2);
        //BiometricPlugin
        ASWP_BIOMETRIC_ON_LAUNCH = config.getBoolean("biometric.trigger.launch", false);

        // --- Initialize derived variables after loading config ---
        ASWP_OFFLINE = ASWV_APP_URL.matches("^(file)://.*$") && !Functions.isInternetAvailable(context);
        ASWV_URL = ASWP_OFFLINE ? ASWV_OFFLINE_URL : ASWV_APP_URL;
        ASWV_SHARE_URL = ASWV_URL + ASWV_SHARE_URL_SUFFIX;
        ASWV_HOST = Functions.aswm_host(ASWV_URL);
        CURR_URL = ASWV_URL;
        true_online = !ASWP_OFFLINE;
    }

    /**
     * Custom Application class to ensure configuration is loaded at the earliest moment.
     * This class MUST be registered in the AndroidManifest.xml file.
     */
    public static class App extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            // This is the first thing that runs when the app process is created.
            SWVContext.loadConfig(this);
            SWVContext.setAppContext(this);
        }
    }

    // --- The rest of the methods from the original SmartWebView.java ---

    public static void setAppContext(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static synchronized PluginManager getPluginManager() {
        if (pluginManagerInstance == null) {
            pluginManagerInstance = new PluginManager();
        }
        return pluginManagerInstance;
    }

    public static void init(Activity activity, WebView webView, Functions functions) {
        getPluginManager().setContext(activity, webView, functions);
        if (!arePluginsInitialized) {
            arePluginsInitialized = true;
            for (Runnable callback : onInitCallbacks) {
                callback.run();
            }
            onInitCallbacks.clear();
        }
    }

    public static void onPluginsInitialized(Runnable callback) {
        if (arePluginsInitialized) {
            callback.run();
        } else {
            onInitCallbacks.add(callback);
        }
    }

    public static void loadPlugins(Context context) {
        Map<String, Boolean> enabledPlugins = new HashMap<>();
        for (String pluginName : ASWP_ENABLED_PLUGINS) {
            enabledPlugins.put(pluginName, true);
        }

        try {
            String packageCodePath = context.getPackageCodePath();
            DexFile df = new DexFile(packageCodePath);
            String pluginPackageName = "mgks.os.swv.plugins";

            for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
                String className = iter.nextElement();
                if (className.startsWith(pluginPackageName) && !className.contains("$")) {
                    try {
                        final Class<?> pluginClass = Class.forName(className);
                        if (PluginInterface.class.isAssignableFrom(pluginClass)) {
                            // The static block of the plugin class will call PluginManager.registerPlugin()
                            // We need to check if it's enabled in our config.
                            // This part is tricky because the name is in the instance.
                            // The static block registration needs to be modified to check against the config.
                            // For now, this just loads the class.
                            Log.d(TAG, "Plugin class loaded: " + pluginClass.getSimpleName());
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        Log.e(TAG, "Could not load plugin class: " + className, e);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error scanning for plugins", e);
        }
    }

    // NavItem class remains the same
    public static class NavItem {
        public final int id;
        public final String action;
        public NavItem(int id, String action) { this.id = id; this.action = action; }
    }

    // The ASWV_DRAWER_MENU map should also be moved here if it's to be configured.
    // For now, keeping it as is.
    public static final Map<Integer, NavItem> ASWV_DRAWER_MENU = new HashMap<Integer, NavItem>() {{
        put(R.id.nav_home, new NavItem(R.id.nav_home, "https://mgks.github.io/Android-SmartWebView/"));
        put(R.id.nav_doc, new NavItem(R.id.nav_doc, "https://docs.mgks.dev/smart-webview/"));
        put(R.id.nav_plugins, new NavItem(R.id.nav_plugins, "https://docs.mgks.dev/smart-webview/plugins/"));
        put(R.id.nav_psg, new NavItem(R.id.nav_psg, "https://docs.mgks.dev/smart-webview/play-store-guide/"));
        put(R.id.nav_support, new NavItem(R.id.nav_support, "mailto:hello@mgks.dev?subject=Help: Smart WebView"));
    }};
}