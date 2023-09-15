package mgks.os.swv;

/*
 * Smart WebView 7.0 (May 2023)
 * Smart WebView is an Open Source project that integrates native features into webview to help create advanced hybrid applications. Available on GitHub (https://github.com/mgks/Android-SmartWebView).
 * Initially developed by Ghazi Khan (https://github.com/mgks) under MIT Open Source License.
 * This program is free to use for private and commercial purposes under MIT License (https://opensource.org/licenses/MIT).
 * Please mention project source or developer credits in your Application's License(s) Wiki.
 * Contribute to the project (https://github.com/mgks/Android-SmartWebView/discussions)
 * Sponsor the project (https://github.com/sponsors/mgks)
 * Giving right credits to developers encourages them to keep improving their projects :)
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

		int SPLASH_TIME_OUT = 5000;
		/*
		 * Showing splash screen with a timer. This will be useful when you
		 * want to show case your app logo / company
		 */
		new Handler().postDelayed(() -> {
			// This method will be executed once the timer is over
			// Start your app main activity
			Intent i = new Intent(SplashScreen.this, MainActivity.class);
			startActivity(i);

			// close this activity
			finish();
		}, SPLASH_TIME_OUT);
    }

}
