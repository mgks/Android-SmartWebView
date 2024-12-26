package mgks.os.swv;

/*
 * Smart WebView 7.0
 * Smart WebView is an Open Source project that integrates native features into webview to help create advanced hybrid applications. Original source (https://github.com/mgks/Android-SmartWebView)
 * This program is free to use for private and commercial purposes under MIT License (https://opensource.org/licenses/MIT)
 * Join the discussion (https://github.com/mgks/Android-SmartWebView/discussions)
 * Support Smart WebView (https://github.com/sponsors/mgks)
 * Acknowledging project sources and developers helps them continue their valuable work. Thank you for your support :)
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

		int SPLASH_TIME_OUT = 5000; // timer helps showcasing your logo or banner while main activity loads
		new Handler().postDelayed(() -> {
			Intent i = new Intent(SplashScreen.this, MainActivity.class);
			startActivity(i);
			finish(); // closing splash screen
		}, SPLASH_TIME_OUT);
    }
}
