package mgks.os.swv;

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

		// Get intent, action and MIME type
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if("text/plain".equals(type)){
				handleSendText(intent); // Handle text being sent
			}else if(type.startsWith("image/")){
				handleSendImage(intent); // Handle single image being sent
			}
		}else if(Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null){
			if(type.startsWith("image/")){
				handleSendMultipleImages(intent); // Handle multiple images being sent
			}
		}else{
			Intent to_main = new Intent(this, MainActivity.class);
			startActivity(to_main);
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

	// ~ This thing kinda not working at the moment -_-
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
