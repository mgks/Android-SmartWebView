package mgks.os.webview;

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
			Toast.makeText(this, "Error occured, URI is invalid", Toast.LENGTH_LONG).show();
		}
	}

	void handleSendMultipleImages(Intent intent) {
		ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		if (imageUris != null) {
			// update UI to reflect multiple images being shared
		}
	}
}
