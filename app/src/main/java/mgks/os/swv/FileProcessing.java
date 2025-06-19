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
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileProcessing {

	private final Activity activity;
	private final ActivityResultLauncher<Intent> resultLauncher; // Launcher is now passed in
	Functions fns = new Functions();

	// Modified constructor to accept the launcher
	public FileProcessing(Activity activity, ActivityResultLauncher<Intent> resultLauncher) {
		this.activity = activity;
		this.resultLauncher = resultLauncher;
	}

	// The registerActivityResultLauncher() method is now removed from this class.

	public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
		if (!SmartWebView.ASWP_FUPLOAD) {
			return false;
		}

		SmartWebView.asw_file_path = filePathCallback;
		Intent takePictureIntent = null;
		Intent takeVideoIntent = null;

		boolean needCamera = false;
		if (SmartWebView.ASWP_CAMUPLOAD) {
			needCamera = true;
		}

		if (needCamera) {
			// Request camera permission if needed
			if (!fns.check_permission(3, activity)) {
				fns.get_permissions(3, activity);
				SmartWebView.asw_file_path = null;
				return false;
			}
			// Create camera intent for photos
			takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
				File photoFile = null;
				try {
					photoFile = create_image(activity);
					takePictureIntent.putExtra("PhotoPath", SmartWebView.asw_pcam_message);
				} catch (IOException ex) {
					Log.e("FileProcessing", "Image file creation failed", ex);
					Toast.makeText(activity, "Error creating image file", Toast.LENGTH_SHORT).show();
				}
				if (photoFile != null) {
					SmartWebView.asw_pcam_message = "file:" + photoFile.getAbsolutePath();
					Uri photoURI = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", photoFile);
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				} else {
					takePictureIntent = null;
				}
			}

			// Create camera intent for videos
			takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			if (takeVideoIntent.resolveActivity(activity.getPackageManager()) != null) {
				File videoFile = null;
				try {
					videoFile = create_video(activity);
					takeVideoIntent.putExtra("VideoPath", SmartWebView.asw_vcam_message);
				} catch (IOException ex) {
					Log.e("FileProcessing", "Video file creation failed", ex);
					Toast.makeText(activity, "Error creating video file", Toast.LENGTH_SHORT).show();
				}
				if (videoFile != null) {
					SmartWebView.asw_vcam_message = "file:" + videoFile.getAbsolutePath();
					Uri videoURI = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", videoFile);
					takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
				} else {
					takeVideoIntent = null;
				}
			}
		}

		// Create file chooser intent
		Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
		contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
		contentSelectionIntent.setType("*/*"); // Allow all file types initially

		// Set multiple file selection if enabled
		if (SmartWebView.ASWP_MULFILE) {
			contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		}

		// Set accepted file types based on file chooser parameters
		String[] acceptTypes = fileChooserParams.getAcceptTypes();
		if (acceptTypes != null && acceptTypes.length > 0) {
			contentSelectionIntent.setType(String.join(",", acceptTypes));
		}

		// Intent array for the chooser
		Intent[] intentArray;
		if (takePictureIntent != null && takeVideoIntent != null) {
			intentArray = new Intent[]{takePictureIntent, takeVideoIntent};
		} else if (takePictureIntent != null) {
			intentArray = new Intent[]{takePictureIntent};
		} else if (takeVideoIntent != null) {
			intentArray = new Intent[]{takeVideoIntent};
		} else {
			intentArray = new Intent[0];
		}

		// Create and launch the chooser intent
		Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
		chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
		chooserIntent.putExtra(Intent.EXTRA_TITLE, activity.getString(R.string.fl_chooser));
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

		// Use the activity result launcher to start the intent
		if (resultLauncher != null) {
			resultLauncher.launch(chooserIntent);
		} else {
			Log.e("FileProcessing", "ResultLauncher is null. Cannot launch intent.");
			SmartWebView.asw_file_path.onReceiveValue(null);
			SmartWebView.asw_file_path = null;
			return false;
		}

		return true;
	}

	// Creating image file for upload
	public static File create_image(Context context) throws IOException {
		@SuppressLint("SimpleDateFormat")
		String file_name = new SimpleDateFormat("yyyyMMss").format(new Date());
		String new_name = "file_" + file_name + "_";
		File sd_directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		return File.createTempFile(new_name, ".jpg", sd_directory);
	}

	// Creating video file for upload
	public static File create_video(Context context) throws IOException {
		@SuppressLint("SimpleDateFormat")
		String file_name = new SimpleDateFormat("yyyyMMss").format(new Date());
		String new_name = "file_" + file_name + "_";
		File sd_directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		return File.createTempFile(new_name, ".3gp", sd_directory);
	}
}
