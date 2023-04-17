package mgks.os.swv;

// following source code is taken from - @hotchemi (https://github.com/hotchemi/Android-Rate)

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;

final class DialogManager {

	private static boolean showNeutralButton = true;
	private static boolean showNegativeButton = true;
	private static boolean showTitle = true;
	private static boolean cancelable = false;

	private static StoreType storeType = StoreType.GOOGLEPLAY;

	private static int titleResId = R.string.rate_dialog_title;
	private static int messageResId = R.string.rate_dialog_message;
	private static int textPositiveResId = R.string.rate_dialog_ok;
	private static int textNeutralResId = R.string.rate_dialog_cancel;
	private static int textNegativeResId = R.string.rate_dialog_no;

	private static String titleText = null;
	private static String messageText = null;
	private static String positiveText = null;
	private static String neutralText = null;
	private static String negativeText = null;

	private static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";
	private static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=";
	private static final String AMAZON_APPSTORE = "amzn://apps/android?p=";

	private View view;

	private static Reference<OnClickButtonListener> listener;

    Dialog create(final Context context, DialogManager options) {
        AlertDialog.Builder builder = getDialogBuilder(context);
        builder.setMessage(getMessageText(context));

        if (shouldShowTitle()) builder.setTitle(getTitleText(context));

        builder.setCancelable(getCancelable());

        View view = getView();
        if (view != null) builder.setView(view);

        final OnClickButtonListener listener = getListener();

        builder.setPositiveButton(getPositiveText(context), (dialog, which) -> {
			final Intent intentToAppstore = getStoreType() == StoreType.GOOGLEPLAY ?
			createIntentForGooglePlay(context) : createIntentForAmazonAppstore(context);
			context.startActivity(intentToAppstore);
			AppRate.setAgreeShowDialog(context, false);
			if (listener != null) listener.onClickButton(which);
		});

        if (shouldShowNeutralButton()) {
            builder.setNeutralButton(getNeutralText(context), (dialog, which) -> {
				AppRate.setRemindInterval(context);
				if (listener != null) listener.onClickButton(which);
			});
        }

        if (shouldShowNegativeButton()) {
            builder.setNegativeButton(getNegativeText(context), (dialog, which) -> {
				AppRate.setAgreeShowDialog(context, false);
				if (listener != null) listener.onClickButton(which);
			});
        }
        return builder.create();
    }

	boolean shouldShowNeutralButton() {
		return showNeutralButton;
	}

	void setShowNeutralButton(boolean showNeutralButton) {
		DialogManager.showNeutralButton = showNeutralButton;
	}

	boolean shouldShowNegativeButton() {
		return showNegativeButton;
	}

	void setShowNegativeButton(boolean showNegativeButton) {
		DialogManager.showNegativeButton = showNegativeButton;
	}

	boolean shouldShowTitle() {
		return showTitle;
	}

	void setShowTitle(boolean showTitle) {
		DialogManager.showTitle = showTitle;
	}

	boolean getCancelable() {
		return cancelable;
	}

	void setCancelable(boolean cancelable) {
		DialogManager.cancelable = cancelable;
	}

	static StoreType getStoreType() {
		return storeType;
	}

	void setStoreType( StoreType appstore ) {
		storeType = appstore;
	}

	void setTitleResId(int titleResId) {
		DialogManager.titleResId = titleResId;
	}

	void setMessageResId(int messageResId) {
		DialogManager.messageResId = messageResId;
	}

	void setTextPositiveResId(int textPositiveResId) {
		DialogManager.textPositiveResId = textPositiveResId;
	}

	void setTextNeutralResId(int textNeutralResId) {
		DialogManager.textNeutralResId = textNeutralResId;
	}

	void setTextNegativeResId(int textNegativeResId) {
		DialogManager.textNegativeResId = textNegativeResId;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
	}

	static OnClickButtonListener getListener() {
		return listener != null ? listener.get() : null;
	}

	void setListener(OnClickButtonListener listener) {
		DialogManager.listener = new WeakReference<>(listener);
	}

	static String getTitleText(Context context) {
		if (titleText == null) {
			return context.getString(titleResId);
		}
		return titleText;
	}

	void setTitleText(String titleText) {
		DialogManager.titleText = titleText;
	}

	static String getMessageText(Context context) {
		if (messageText == null) {
			return context.getString(messageResId);
		}
		return messageText;
	}

	void setMessageText(String messageText) {
		DialogManager.messageText = messageText;
	}

	static String getPositiveText(Context context) {
		if (positiveText == null) {
			return context.getString(textPositiveResId);
		}
		return positiveText;
	}

	void setPositiveText(String positiveText) {
		DialogManager.positiveText = positiveText;
	}

	static String getNeutralText(Context context) {
		if (neutralText == null) {
			return context.getString(textNeutralResId);
		}
		return neutralText;
	}

	void setNeutralText(String neutralText) {
		DialogManager.neutralText = neutralText;
	}

	static String getNegativeText(Context context) {
		if (negativeText == null) {
			return context.getString(textNegativeResId);
		}
		return negativeText;
	}

	void setNegativeText(String negativeText) {
		DialogManager.negativeText = negativeText;
	}

	static Intent createIntentForGooglePlay(Context context) {
		String packageName = context.getPackageName();
		Intent intent = new Intent(Intent.ACTION_VIEW, getGooglePlay(packageName));
		if (isPackageExists(context, GOOGLE_PLAY_PACKAGE_NAME)) {
			intent.setPackage(GOOGLE_PLAY_PACKAGE_NAME);
		}
		return intent;
	}

	static Intent createIntentForAmazonAppstore(Context context) {
		String packageName = context.getPackageName();
		return new Intent(Intent.ACTION_VIEW, getAmazonAppstore(packageName));
	}

	static Uri getGooglePlay(String packageName) {
		return packageName == null ? null : Uri.parse(GOOGLE_PLAY + packageName);
	}

	static Uri getAmazonAppstore(String packageName) {
		return packageName == null ? null : Uri.parse(AMAZON_APPSTORE + packageName);
	}

	static boolean isPackageExists(Context context, String targetPackage) {
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.equals(targetPackage)) return true;
		}
		return false;
	}

	private static boolean underHoneyComb() {
		return false;
	}

	private static boolean isLollipop() {
		return Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1;
	}

	private static int getDialogTheme() {
		return isLollipop() ? R.style.CustomLollipopDialogStyle : 0;
	}

	@SuppressLint("NewApi")
	static AlertDialog.Builder getDialogBuilder(Context context) {
		if (underHoneyComb()) {
			return new AlertDialog.Builder(context);
		} else {
			return new AlertDialog.Builder(context, getDialogTheme());
		}
	}
}

enum StoreType {
	GOOGLEPLAY,
	AMAZON
}

interface OnClickButtonListener {
	void onClickButton(int which);
}
