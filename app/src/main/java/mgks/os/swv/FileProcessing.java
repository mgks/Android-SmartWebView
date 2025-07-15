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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		if (!SWVContext.ASWP_FUPLOAD) {
			return false;
		}

		SWVContext.asw_file_path = filePathCallback;
		String[] acceptTypes = fileChooserParams.getAcceptTypes();
		boolean allowImage = false;
		boolean allowVideo = false;

		// Determine allowed types from the `accept` attribute
		if (acceptTypes.length > 0 && !acceptTypes[0].isEmpty()) {
			for (String type : acceptTypes) {
				if (type.startsWith("image/")) {
					allowImage = true;
				}
				if (type.startsWith("video/")) {
					allowVideo = true;
				}
			}
		} else {
			// If no specific type is defined, allow both
			allowImage = true;
			allowVideo = true;
		}


		Intent takePictureIntent = null;
		Intent takeVideoIntent = null;

		if (SWVContext.ASWP_CAMUPLOAD) {
			PermissionManager permissionManager = new PermissionManager(activity); // Create instance here
			if (!permissionManager.isCameraPermissionGranted()) {
				permissionManager.requestCameraPermission();
				SWVContext.asw_file_path = null;
				return false;
			}
			// Only add camera intent if images are allowed
			if (allowImage) {
				takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
					File photoFile = null;
					try {
						photoFile = create_image(activity);
						takePictureIntent.putExtra("PhotoPath", SWVContext.asw_pcam_message);
					} catch (IOException ex) {
						Log.e("FileProcessing", "Image file creation failed", ex);
					}
					if (photoFile != null) {
						SWVContext.asw_pcam_message = "file:" + photoFile.getAbsolutePath();
						Uri photoURI = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", photoFile);
						takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
					} else {
						takePictureIntent = null;
					}
				}
			}

			// Only add video recorder intent if videos are allowed
			if (allowVideo) {
				takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				if (takeVideoIntent.resolveActivity(activity.getPackageManager()) != null) {
					File videoFile = null;
					try {
						videoFile = create_video(activity);
					} catch (IOException ex) {
						Log.e("FileProcessing", "Video file creation failed", ex);
					}
					if (videoFile != null) {
						SWVContext.asw_vcam_message = "file:" + videoFile.getAbsolutePath();
						Uri videoURI = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", videoFile);
						takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
					} else {
						takeVideoIntent = null;
					}
				}
			}
		}

		Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
		contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
		contentSelectionIntent.setType("*/*"); // Set general type
		if (acceptTypes.length > 0) {
			contentSelectionIntent.putExtra(Intent.EXTRA_MIME_TYPES, acceptTypes); // And specific types
		}

		if (SWVContext.ASWP_MULFILE) {
			contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		}

		List<Intent> intentList = new ArrayList<>();
		if (takePictureIntent != null) intentList.add(takePictureIntent);
		if (takeVideoIntent != null) intentList.add(takeVideoIntent);
		Intent[] intentArray = intentList.toArray(new Intent[0]);

		Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
		chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
		chooserIntent.putExtra(Intent.EXTRA_TITLE, activity.getString(R.string.fl_chooser));
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

		if (resultLauncher != null) {
			resultLauncher.launch(chooserIntent);
		} else {
			Log.e("FileProcessing", "ResultLauncher is null. Cannot launch intent.");
			if (SWVContext.asw_file_path != null) {
				SWVContext.asw_file_path.onReceiveValue(null);
				SWVContext.asw_file_path = null;
			}
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
