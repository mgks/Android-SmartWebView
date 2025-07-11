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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Handle the splash screen transition.
		// This is the correct syntax for Java.
		androidx.core.splashscreen.SplashScreen.installSplashScreen(this);

		super.onCreate(savedInstanceState);

		// Immediately start the main activity.
		Intent i = new Intent(SplashScreen.this, MainActivity.class);
		startActivity(i);
		finish(); // closing splash screen
	}
}