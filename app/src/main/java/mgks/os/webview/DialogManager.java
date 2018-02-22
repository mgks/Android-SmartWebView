package mgks.os.webview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;

import static mgks.os.webview.IntentHelper.createIntentForAmazonAppstore;
import static mgks.os.webview.IntentHelper.createIntentForGooglePlay;
import static mgks.os.webview.PreferenceHelper.setAgreeShowDialog;
import static mgks.os.webview.PreferenceHelper.setRemindInterval;
import static mgks.os.webview.UriHelper.getAmazonAppstore;
import static mgks.os.webview.UriHelper.getGooglePlay;
import static mgks.os.webview.UriHelper.isPackageExists;
import static mgks.os.webview.Utils.getDialogBuilder;

final class DialogManager {

    private DialogManager() {
    }

    static Dialog create(final Context context, final DialogOptions options) {
        AlertDialog.Builder builder = getDialogBuilder(context);
        builder.setMessage(options.getMessageText(context));

        if (options.shouldShowTitle()) builder.setTitle(options.getTitleText(context));

        builder.setCancelable(options.getCancelable());

        View view = options.getView();
        if (view != null) builder.setView(view);

        final OnClickButtonListener listener = options.getListener();

        builder.setPositiveButton(options.getPositiveText(context), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Intent intentToAppstore = options.getStoreType() == StoreType.GOOGLEPLAY ?
                createIntentForGooglePlay(context) : createIntentForAmazonAppstore(context);
                context.startActivity(intentToAppstore);
                setAgreeShowDialog(context, false);
                if (listener != null) listener.onClickButton(which);
            }
        });

        if (options.shouldShowNeutralButton()) {
            builder.setNeutralButton(options.getNeutralText(context), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setRemindInterval(context);
                    if (listener != null) listener.onClickButton(which);
                }
            });
        }

        if (options.shouldShowNegativeButton()) {
            builder.setNegativeButton(options.getNegativeText(context), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setAgreeShowDialog(context, false);
                    if (listener != null) listener.onClickButton(which);
                }
            });
        }

        return builder.create();
    }

}
final class DialogOptions {

	private boolean showNeutralButton = true;

	private boolean showNegativeButton = true;

	private boolean showTitle = true;

	private boolean cancelable = false;

	private StoreType storeType = StoreType.GOOGLEPLAY;

	private int titleResId = R.string.rate_dialog_title;

	private int messageResId = R.string.rate_dialog_message;

	private int textPositiveResId = R.string.rate_dialog_ok;

	private int textNeutralResId = R.string.rate_dialog_cancel;

	private int textNegativeResId = R.string.rate_dialog_no;

	private String titleText = null;

	private String messageText = null;

	private String positiveText = null;

	private String neutralText = null;

	private String negativeText = null;

	private View view;

	private Reference<OnClickButtonListener> listener;

	boolean shouldShowNeutralButton() {
		return showNeutralButton;
	}

	void setShowNeutralButton(boolean showNeutralButton) {
		this.showNeutralButton = showNeutralButton;
	}

	boolean shouldShowNegativeButton() {
		return showNegativeButton;
	}

	void setShowNegativeButton(boolean showNegativeButton) {
		this.showNegativeButton = showNegativeButton;
	}

	boolean shouldShowTitle() {
		return showTitle;
	}

	void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	boolean getCancelable() {
		return cancelable;
	}

	void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
	}

	StoreType getStoreType() {
		return storeType;
	}

	void setStoreType( StoreType appstore ) {
		storeType = appstore;
	}

	void setTitleResId(int titleResId) {
		this.titleResId = titleResId;
	}

	void setMessageResId(int messageResId) {
		this.messageResId = messageResId;
	}

	void setTextPositiveResId(int textPositiveResId) {
		this.textPositiveResId = textPositiveResId;
	}

	void setTextNeutralResId(int textNeutralResId) {
		this.textNeutralResId = textNeutralResId;
	}

	void setTextNegativeResId(int textNegativeResId) {
		this.textNegativeResId = textNegativeResId;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	OnClickButtonListener getListener() {
		return listener != null ? listener.get() : null;
	}

	void setListener(OnClickButtonListener listener) {
		this.listener = new WeakReference<>(listener);
	}

	String getTitleText(Context context) {
		if (titleText == null) {
			return context.getString(titleResId);
		}
		return titleText;
	}

	void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	String getMessageText(Context context) {
		if (messageText == null) {
			return context.getString(messageResId);
		}
		return messageText;
	}

	void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	String getPositiveText(Context context) {
		if (positiveText == null) {
			return context.getString(textPositiveResId);
		}
		return positiveText;
	}

	void setPositiveText(String positiveText) {
		this.positiveText = positiveText;
	}

	String getNeutralText(Context context) {
		if (neutralText == null) {
			return context.getString(textNeutralResId);
		}
		return neutralText;
	}

	void setNeutralText(String neutralText) {
		this.neutralText = neutralText;
	}

	String getNegativeText(Context context) {
		if (negativeText == null) {
			return context.getString(textNegativeResId);
		}
		return negativeText;
	}

	void setNegativeText(String negativeText) {
		this.negativeText = negativeText;
	}
}
final class IntentHelper {

	private static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";

	private IntentHelper() {
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

}
interface OnClickButtonListener {
	void onClickButton(int which);
}
final class UriHelper {

	private static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=";

	private static final String AMAZON_APPSTORE = "amzn://apps/android?p=";

	private UriHelper() {
	}

	static Uri getGooglePlay(String packageName) {
		return packageName == null ? null : Uri.parse(GOOGLE_PLAY + packageName);
	}

	static Uri getAmazonAppstore(String packageName) {
		return packageName == null ? null : Uri.parse(AMAZON_APPSTORE + packageName);
	}

	static boolean isPackageExists(Context context, String targetPackage) {
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(0);
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.equals(targetPackage)) return true;
		}
		return false;
	}
}
final class Utils {

	private Utils() {
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
