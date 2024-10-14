package mgks.os.swv;

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
