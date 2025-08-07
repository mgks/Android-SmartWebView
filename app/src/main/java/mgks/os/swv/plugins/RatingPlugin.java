package mgks.os.swv.plugins;

/*
  Rating Plugin for Smart WebView

  This is a core plugin and is covered by the MIT license of the Smart WebView project.

  This plugin prompts users to rate the application on the Google Play Store after
  certain usage conditions (e.g., number of launches, days since install) are met.

  FEATURES:
  - Configurable conditions for showing the dialog.
  - Standard three-button dialog: "Rate Now", "Later", "No, Thanks".
  - Remembers user's choice to avoid repeated prompts.
  - Opens the app's Google Play Store page.

  USAGE:
  1. This plugin is typically self-activating based on its configuration.
  2. To configure, modify the default values in the plugin's static initializer
     or use Playground.java to set them dynamically.
  3. Enable/disable the entire feature with the `ASWP_RATINGS` flag in SWVContext.java.
*/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mgks.os.swv.Functions;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;
import mgks.os.swv.R;
import mgks.os.swv.SWVContext;

public class RatingPlugin implements PluginInterface {
    private static final String TAG = "RatingPlugin";
    private Activity activity;
    private Map<String, Object> config;

    // Configuration defaults
    private int installDays = 3;
    private int launchTimes = 10;
    private int remindInterval = 2;

    // SharedPreferences keys
    private static final String PREF_NAME = "swv_rating_plugin_prefs";
    private static final String KEY_INSTALL_DATE = "install_date";
    private static final String KEY_LAUNCH_TIMES = "launch_times";
    private static final String KEY_DONT_SHOW_AGAIN = "dont_show_again";
    private static final String KEY_REMIND_LATER_DATE = "remind_later_date";

    // Static initializer for self-registration
    static {
        Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put("installDays", 3);
        defaultConfig.put("launchTimes", 10);
        defaultConfig.put("remindInterval", 2);
        PluginManager.registerPlugin(new RatingPlugin(), defaultConfig);
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.config = config;

        // Load configuration, use defaults if not provided
        installDays = (int) config.getOrDefault("installDays", installDays);
        launchTimes = (int) config.getOrDefault("launchTimes", launchTimes);
        remindInterval = (int) config.getOrDefault("remindInterval", remindInterval);

        Log.d(TAG, "RatingPlugin initialized. Config: Days=" + installDays + ", Times=" + launchTimes);

        // Monitor usage and check conditions
        monitor();
        if (shouldShowRateDialog()) {
            showRateDialog();
        }
    }

    // --- Core Logic (Migrated from AppRate) ---

    private SharedPreferences getPrefs() {
        return activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private void monitor() {
        SharedPreferences prefs = getPrefs();
        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch times
        int currentLaunchTimes = prefs.getInt(KEY_LAUNCH_TIMES, 0) + 1;
        editor.putInt(KEY_LAUNCH_TIMES, currentLaunchTimes);

        // Set install date if not set
        if (prefs.getLong(KEY_INSTALL_DATE, 0) == 0) {
            editor.putLong(KEY_INSTALL_DATE, System.currentTimeMillis());
        }

        editor.apply();
    }

    private boolean shouldShowRateDialog() {
        SharedPreferences prefs = getPrefs();

        if (prefs.getBoolean(KEY_DONT_SHOW_AGAIN, false)) {
            return false;
        }

        // Check launch times
        if (prefs.getInt(KEY_LAUNCH_TIMES, 0) < SWVContext.ASWR_TIMES) { // Use global config
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long installDate = prefs.getLong(KEY_INSTALL_DATE, 0);
        long remindLaterDate = prefs.getLong(KEY_REMIND_LATER_DATE, 0);

        // Check install days
        if (currentTime < installDate + (long) SWVContext.ASWR_DAYS * 24 * 60 * 60 * 1000) { // Use global config
            return false;
        }

        // Check remind interval
        if (currentTime < remindLaterDate + (long) SWVContext.ASWR_INTERVAL * 24 * 60 * 60 * 1000) { // Use global config
            return false;
        }

        return true;
    }

    // --- Dialog Logic (Migrated from DialogManager) ---

    @SuppressLint("NewApi")
    private void showRateDialog() {
        if (activity == null || activity.isFinishing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(R.string.rate_dialog_title);
        builder.setMessage(R.string.rate_dialog_message);

        // Rate Now (Positive)
        builder.setPositiveButton(R.string.rate_dialog_ok, (dialog, which) -> {
            rateApp();
            getPrefs().edit().putBoolean(KEY_DONT_SHOW_AGAIN, true).apply();
        });

        // Later (Neutral)
        builder.setNeutralButton(R.string.rate_dialog_cancel, (dialog, which) -> {
            getPrefs().edit().putLong(KEY_REMIND_LATER_DATE, System.currentTimeMillis()).apply();
        });

        // Don't Ask Again (Negative)
        builder.setNegativeButton(R.string.rate_dialog_no, (dialog, which) -> {
            getPrefs().edit().putBoolean(KEY_DONT_SHOW_AGAIN, true).apply();
        });

        builder.create().show();
    }

    private void rateApp() {
        final String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    // --- Standard Plugin Interface Methods ---
    @Override public String getPluginName() { return "RatingPlugin"; }
    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {}
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {}
    @Override public boolean shouldOverrideUrlLoading(WebView view, String url) { return false; }
    @Override public void onPageStarted(String url) {}
    @Override public void onPageFinished(String url) {}
    @Override public void onResume() {}
    @Override public void onPause() {}
    @Override public void onDestroy() {}
    @Override public void evaluateJavascript(String script) {}
}