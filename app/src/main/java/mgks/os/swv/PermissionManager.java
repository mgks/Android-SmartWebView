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

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private static final String TAG = "PermissionManager";

    // --- Permission Request Codes ---
    // We use a single code for the initial batch request for simplicity.
    // Individual requests (like from a plugin) can use their own codes.
    public static final int INITIAL_REQUEST_CODE = 100;
    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int STORAGE_REQUEST_CODE = 102;

    private final Activity activity;

    public PermissionManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * Checks all configured features in SmartWebView.java and requests the
     * required permissions in a single batch.
     * This should be called on app launch.
     */
    public void requestInitialPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        // Iterate through the permission groups defined in SmartWebView config.
        for (String permissionGroup : SmartWebView.ASWP_REQUIRED_PERMISSIONS) {
            switch (permissionGroup) {
                case "LOCATION":
                    if (SmartWebView.ASWP_LOCATION && !isLocationPermissionGranted()) {
                        permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                    break;

                case "NOTIFICATIONS":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNotificationPermissionGranted()) {
                        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
                    }
                    break;

                case "STORAGE":
                    // Note: It's often better to request storage/media contextually.
                    // But if required on launch, this handles it.
                    if (SmartWebView.ASWP_FUPLOAD && !isStoragePermissionGranted()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES);
                        } else {
                            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }
                    break;
            }
        }

        // If there are permissions to request, request them all at once.
        if (!permissionsToRequest.isEmpty()) {
            Log.d(TAG, "Requesting initial permissions: " + permissionsToRequest);
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toArray(new String[0]), INITIAL_REQUEST_CODE);
        } else {
            Log.d(TAG, "All initial permissions are already granted.");
        }
    }

    /**
     * A dedicated method to request camera permissions when needed.
     * This is better for user context than asking on launch.
     */
    public void requestCameraPermission() {
        if (!isCameraPermissionGranted()) {
            List<String> permissions = new ArrayList<>();
            permissions.add(Manifest.permission.CAMERA);
            if (!isStoragePermissionGranted()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
                } else {
                    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[0]), CAMERA_REQUEST_CODE);
        }
    }

    // --- Helper methods to check permission status ---

    public boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Notifications permission not required before Android 13
    }

    public boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        }
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}