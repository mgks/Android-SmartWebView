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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;

public class ShareActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get intent, action and MIME type
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if("text/plain".equals(type)){
				handleSendText(intent);		// handle text being sent
			}else if(type.startsWith("image/")){
				handleSendImage(intent);	// handle single image being sent
			}
		}else if(Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null){
			if(type.startsWith("image/")){
				handleSendMultipleImages(intent); 	// handle multiple images being sent
			}
		}else{
			Intent tomain = new Intent(this, MainActivity.class);
			startActivity(tomain);
		}
	}

	void handleSendText(Intent intent) {
		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (sharedText != null) {
			Intent i = new Intent(getBaseContext(), MainActivity.class);
			i.putExtra("s_uri", sharedText);
			startActivity(i);
			finish();
		}
	}

	// ~ this thing kinda not working at the moment; anybody want to help?
	private void handleSendImage(Intent intent) {
		Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null) {
			Intent i = new Intent(this, MainActivity.class);
			i.putExtra("s_img", imageUri.toString());
			startActivity(i);
			finish();
		} else {
			Toast.makeText(this, "Error occurred, URI is invalid", Toast.LENGTH_LONG).show();
		}
	}

	void handleSendMultipleImages(Intent intent) {
		ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		if (imageUris != null) {
			// update UI to reflect multiple images being shared
		}
	}
}
