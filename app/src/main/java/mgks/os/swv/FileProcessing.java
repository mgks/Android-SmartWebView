package mgks.os.swv;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class FileProcessing {
	/*	@SuppressLint({"SetJavaScriptEnabled", "WrongViewCast", "JavascriptInterface"})
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityResultLauncher<Intent> act_result_launcher;
		final Functions fns = new Functions();

		act_result_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
			//Log.d("SLOG_TRUE_ONLINE", String.valueOf(true_online));

			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
			Uri[] results = null;
			if (result.getResultCode() == Activity.RESULT_CANCELED) {
				// If the file request was cancelled (i.e. user exited camera),
				// we must still send a null value in order to ensure that future attempts
				// to pick files will still work.
				SmartWebView.asw_file_path.onReceiveValue(null);
				return;

			} else if (result.getResultCode() == Activity.RESULT_OK) {
				if (null == SmartWebView.asw_file_path) {
					return;
				}
				ClipData clipData;
				String stringData;
				try {
					assert result.getData() != null;
					clipData = result.getData().getClipData();
					stringData = result.getData().getDataString();
				} catch (Exception e) {
					clipData = null;
					stringData = null;
				}

				if (clipData == null && stringData == null && (SmartWebView.asw_pcam_message != null || SmartWebView.asw_vcam_message != null)) {
					results = new Uri[]{Uri.parse(SmartWebView.asw_pcam_message != null ? SmartWebView.asw_pcam_message : SmartWebView.asw_vcam_message)};

				} else {
					if (null != clipData) { // checking if multiple files selected or not
						final int numSelectedFiles = clipData.getItemCount();
						results = new Uri[numSelectedFiles];
						for (int i = 0; i < clipData.getItemCount(); i++) {
							results[i] = clipData.getItemAt(i).getUri();
						}
					} else {
						try {
							assert result.getData() != null;
							Bitmap cam_photo = (Bitmap) result.getData().getExtras().get("data");
							ByteArrayOutputStream bytes = new ByteArrayOutputStream();
							cam_photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
							stringData = MediaStore.Images.Media.insertImage(getContentResolver(), cam_photo, null, null);
						} catch (Exception ignored) {
						}
						results = new Uri[]{Uri.parse(stringData)};
					}
				}
			}
			SmartWebView.asw_file_path.onReceiveValue(results);
			SmartWebView.asw_file_path = null;
		});
		SmartWebView.asw_view.setWebChromeClient(new WebChromeClient() {
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
				if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
					if (SmartWebView.ASWP_FUPLOAD) {
						SmartWebView.asw_file_path = filePathCallback;
						Intent takePictureIntent = null;
						Intent takeVideoIntent = null;
						if (SmartWebView.ASWP_CAMUPLOAD) {
							boolean includeVideo = false;
							boolean includePhoto = false;

							// Check the accept parameter to determine which intent(s) to include.
							paramCheck:
							for (String acceptTypes : fileChooserParams.getAcceptTypes()) {
								// Although it's an array, it still seems to be the whole value.
								// Split it out into chunks so that we can detect multiple values.
								String[] splitTypes = acceptTypes.split(", ?+");
								for (String acceptType : splitTypes) {
									switch (acceptType) {
										case "*\/*":
											includePhoto = true;
											includeVideo = true;
											break paramCheck;
										case "image/*":
											includePhoto = true;
											break;
										case "video/*":
											includeVideo = true;
											break;
									}
								}
							}

							// If no `accept` parameter was specified, allow both photo and video.
							if (fileChooserParams.getAcceptTypes().length == 0) {
								includePhoto = true;
								includeVideo = true;
							}

							if (includePhoto) {
								takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
									File photoFile = null;
									try {
										photoFile = fns.create_image(getApplicationContext());
										takePictureIntent.putExtra("PhotoPath", SmartWebView.asw_pcam_message);
									} catch (IOException ex) {
										Log.e("SLOG_ERROR", "Image file creation failed", ex);
									}
									if (photoFile != null) {
										SmartWebView.asw_pcam_message = "file:" + photoFile.getAbsolutePath();
										takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", photoFile));
									} else {
										takePictureIntent = null;
									}
								}
							}

							if (includeVideo) {
								takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
								if (takeVideoIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
									File videoFile = null;
									try {
										videoFile = fns.create_video(getApplicationContext());
									} catch (IOException ex) {
										Log.e("SLOG_ERROR", "Video file creation failed", ex);
									}
									if (videoFile != null) {
										SmartWebView.asw_vcam_message = "file:" + videoFile.getAbsolutePath();
										takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", videoFile));
									} else {
										takeVideoIntent = null;
									}
								}
							}
						}

						Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
						if (!SmartWebView.ASWP_ONLYCAM) {
							contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
							contentSelectionIntent.setType(SmartWebView.ASWV_F_TYPE);
							if (SmartWebView.ASWP_MULFILE) {
								contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
							}
						}
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

						Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
						chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
						chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.fl_chooser));
						chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
						//startActivityForResult(chooserIntent, asw_file_req);
						act_result_launcher.launch(chooserIntent);
					}
					return true;
				} else {
					//fns.get_file_perm(getApplicationContext());
					return false;
				}
			}
		});
	}*/
}
