package mgks.os.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import java.util.Date;

import static mgks.os.webview.DialogManager.create;
import static mgks.os.webview.PreferenceHelper.getInstallDate;
import static mgks.os.webview.PreferenceHelper.getIsAgreeShowDialog;
import static mgks.os.webview.PreferenceHelper.getLaunchTimes;
import static mgks.os.webview.PreferenceHelper.getRemindInterval;
import static mgks.os.webview.PreferenceHelper.isFirstLaunch;
import static mgks.os.webview.PreferenceHelper.setInstallDate;

public final class AppRate {

    @SuppressLint("StaticFieldLeak")
	private static AppRate singleton;

    private final Context context;

    private final DialogOptions options = new DialogOptions();

    private int installDate = 10;

    private int launchTimes = 10;

    private int remindInterval = 1;

    private boolean isDebug = false;

    private AppRate(Context context) {
        this.context = context.getApplicationContext();
    }

    public static AppRate with(Context context) {
        if (singleton == null) {
            synchronized (AppRate.class) {
                if (singleton == null) {
                    singleton = new AppRate(context);
                }
            }
        }
        return singleton;
    }

    static void showRateDialogIfMeetsConditions(Activity activity) {
        boolean isMeetsConditions = singleton.isDebug || singleton.shouldShowRateDialog();
        if (isMeetsConditions) {
            singleton.showRateDialog(activity);
        }
	}

    private static boolean isOverDate(long targetDate, int threshold) {
        return new Date().getTime() - targetDate >= threshold * 24 * 60 * 60 * 1000;
    }

    AppRate setLaunchTimes(int launchTimes) {
        this.launchTimes = launchTimes;
        return this;
    }

    AppRate setInstallDays(int installDate) {
        this.installDate = installDate;
        return this;
    }

    AppRate setRemindInterval(int remindInterval) {
        this.remindInterval = remindInterval;
        return this;
    }

    public AppRate setShowLaterButton(boolean isShowNeutralButton) {
        options.setShowNeutralButton(isShowNeutralButton);
        return this;
    }

    public AppRate setShowNeverButton(boolean isShowNeverButton) {
        options.setShowNegativeButton(isShowNeverButton);
        return this;
    }

    public AppRate setShowTitle(boolean isShowTitle) {
        options.setShowTitle(isShowTitle);
        return this;
    }

    public AppRate clearAgreeShowDialog() {
        PreferenceHelper.setAgreeShowDialog(context, true);
        return this;
    }

    public AppRate clearSettingsParam() {
        PreferenceHelper.setAgreeShowDialog(context, true);
        PreferenceHelper.clearSharedPreferences(context);
        return this;
    }

    public AppRate setAgreeShowDialog(boolean clear) {
        PreferenceHelper.setAgreeShowDialog(context, clear);
        return this;
    }

    public AppRate setView(View view) {
        options.setView(view);
        return this;
    }

    public AppRate setOnClickButtonListener(OnClickButtonListener listener) {
        options.setListener(listener);
        return this;
    }

    public AppRate setTitle(int resourceId) {
        options.setTitleResId(resourceId);
        return this;
    }

    public AppRate setTitle(String title) {
        options.setTitleText(title);
        return this;
    }

    AppRate setMessage(int resourceId) {
        options.setMessageResId(resourceId);
        return this;
    }

    public AppRate setMessage(String message) {
        options.setMessageText(message);
        return this;
    }

    AppRate setTextRateNow(int resourceId) {
        options.setTextPositiveResId(resourceId);
        return this;
    }

    public AppRate setTextRateNow(String positiveText) {
        options.setPositiveText(positiveText);
        return this;
    }

    AppRate setTextLater(int resourceId) {
        options.setTextNeutralResId(resourceId);
        return this;
    }

    public AppRate setTextLater(String neutralText) {
        options.setNeutralText(neutralText);
        return this;
    }

    AppRate setTextNever(int resourceId) {
        options.setTextNegativeResId(resourceId);
        return this;
    }

    public AppRate setTextNever(String negativeText) {
        options.setNegativeText(negativeText);
        return this;
    }

    public AppRate setCancelable(boolean cancelable) {
        options.setCancelable(cancelable);
        return this;
    }

    AppRate setStoreType(StoreType appstore) {
        options.setStoreType(appstore);
        return this;
    }

    void monitor() {
        if (isFirstLaunch(context)) {
            setInstallDate(context);
        }
        PreferenceHelper.setLaunchTimes(context, getLaunchTimes(context) + 1);
    }

    private void showRateDialog(Activity activity) {
        if (!activity.isFinishing()) {
            create(activity, options).show();
        }
    }

    private boolean shouldShowRateDialog() {
        return getIsAgreeShowDialog(context) &&
                isOverLaunchTimes() &&
                isOverInstallDate() &&
                isOverRemindDate();
    }

    private boolean isOverLaunchTimes() {
        return getLaunchTimes(context) >= launchTimes;
    }

    private boolean isOverInstallDate() {
        return isOverDate(getInstallDate(context), installDate);
    }

    private boolean isOverRemindDate() {
        return isOverDate(getRemindInterval(context), remindInterval);
    }

    public boolean isDebug() {
        return isDebug;
    }

    public AppRate setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

}
final class PreferenceHelper {

	private static final String PREF_FILE_NAME = "android_rate_pref_file";

	private static final String PREF_KEY_INSTALL_DATE = "android_rate_install_date";

	private static final String PREF_KEY_LAUNCH_TIMES = "android_rate_launch_times";

	private static final String PREF_KEY_IS_AGREE_SHOW_DIALOG = "android_rate_is_agree_show_dialog";

	private static final String PREF_KEY_REMIND_INTERVAL = "android_rate_remind_interval";

	private PreferenceHelper() {
	}

	private static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
	}

	private static SharedPreferences.Editor getPreferencesEditor(Context context) {
		return getPreferences(context).edit();
	}

	/**
	 * Clear data in shared preferences.<br/>
	 *
	 * @param context context
	 */
	static void clearSharedPreferences(Context context) {
		SharedPreferences.Editor editor = getPreferencesEditor(context);
		editor.remove(PREF_KEY_INSTALL_DATE);
		editor.remove(PREF_KEY_LAUNCH_TIMES);
		editor.apply();
	}

	/**
	 * Set agree flag about show dialog.<br/>
	 * If it is false, rate dialog will never shown unless data is cleared.
	 *
	 * @param context context
	 * @param isAgree agree with showing rate dialog
	 */
	static void setAgreeShowDialog(Context context, boolean isAgree) {
		SharedPreferences.Editor editor = getPreferencesEditor(context);
		editor.putBoolean(PREF_KEY_IS_AGREE_SHOW_DIALOG, isAgree);
		editor.apply();
	}

	static boolean getIsAgreeShowDialog(Context context) {
		return getPreferences(context).getBoolean(PREF_KEY_IS_AGREE_SHOW_DIALOG, true);
	}

	static void setRemindInterval(Context context) {
		SharedPreferences.Editor editor = getPreferencesEditor(context);
		editor.remove(PREF_KEY_REMIND_INTERVAL);
		editor.putLong(PREF_KEY_REMIND_INTERVAL, new Date().getTime());
		editor.apply();
	}

	static long getRemindInterval(Context context) {
		return getPreferences(context).getLong(PREF_KEY_REMIND_INTERVAL, 0);
	}

	static void setInstallDate(Context context) {
		SharedPreferences.Editor editor = getPreferencesEditor(context);
		editor.putLong(PREF_KEY_INSTALL_DATE, new Date().getTime());
		editor.apply();
	}

	static long getInstallDate(Context context) {
		return getPreferences(context).getLong(PREF_KEY_INSTALL_DATE, 0);
	}

	static void setLaunchTimes(Context context, int launchTimes) {
		SharedPreferences.Editor editor = getPreferencesEditor(context);
		editor.putInt(PREF_KEY_LAUNCH_TIMES, launchTimes);
		editor.apply();
	}

	static int getLaunchTimes(Context context) {
		return getPreferences(context).getInt(PREF_KEY_LAUNCH_TIMES, 0);
	}

	static boolean isFirstLaunch(Context context) {
		return getPreferences(context).getLong(PREF_KEY_INSTALL_DATE, 0) == 0L;
	}

}
