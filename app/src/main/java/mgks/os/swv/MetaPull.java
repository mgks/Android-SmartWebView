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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.multidex.BuildConfig;

public class MetaPull {

	private final Context context;
	private static final String TAG = "MetaPull";

	public MetaPull(Context context) {
		this.context = context;
	}

	String swv() {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			ApplicationInfo appInfo = context.getApplicationInfo();

			String buildType = BuildConfig.DEBUG ? "debug" : "release";
			int minSdk = appInfo.minSdkVersion;
			int targetSdk = appInfo.targetSdkVersion;

			return "SWV.RELEASE : " + pInfo.versionName
					+ "\nSWV.BUILD : " + pInfo.versionCode
					+ "\nSWV.SDK.MIN : " + minSdk
					+ "\nSWV.SDK.MAX : " + targetSdk
					+ "\nSWV.BUILD.TYPE : " + buildType
					+ "\nSWV.BUILD.NAME : " + pInfo.versionName
					+ "\nSWV.PACKAGE.NAME : " + context.getPackageName();

		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "Could not get package info", e);
			return "Error fetching app info.";
		}
	}

	String device() {
		return "VERSION.RELEASE : " + Build.VERSION.RELEASE
				+ "\nVERSION.SDK.NUMBER : " + Build.VERSION.SDK_INT
				+ "\nMANUFACTURER : " + Build.MANUFACTURER
				+ "\nMODEL : " + Build.MODEL;
	}
}