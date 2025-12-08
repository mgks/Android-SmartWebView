package mgks.os.swv.plugins;

/*
  QR/Barcode Scanner Plugin for Smart WebView

  This plugin provides an interface to scan QR codes and barcodes using the device's camera.

  FEATURES:
  - Initiates a camera scanning view.
  - Returns scanned data back to JavaScript.
  - Configurable scanner prompt and options.
*/

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;
import java.util.Map;

import mgks.os.swv.Functions;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;
import mgks.os.swv.R;

public class QRScannerPlugin implements PluginInterface {
    private static final String TAG = "QRScannerPlugin";
    private Activity activity;
    private WebView webView;
    // MODIFY the type parameter from Intent to ScanOptions
    private ActivityResultLauncher<ScanOptions> launcher;

    static {
        PluginManager.registerPlugin(new QRScannerPlugin(), new HashMap<>());
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView;

        webView.addJavascriptInterface(new QRScannerJSInterface(), "QRScannerInterface");
        Log.d(TAG, "QRScannerPlugin initialized.");
    }

    // MODIFY the method signature to accept the correct launcher type
    public void setLauncher(ActivityResultLauncher<ScanOptions> launcher) {
        this.launcher = launcher;
    }

    // MODIFY this method to accept the new result type
    public void handleScanResult(ScanIntentResult result) {
        String contents = result.getContents();
        if (contents == null) {
            Log.d(TAG, "Scan cancelled");
            String script = "if(window.QRScanner && window.QRScanner.onScanCancelled) { window.QRScanner.onScanCancelled(); }";
            evaluateJavascript(script);
        } else {
            Log.d(TAG, "Scanned QR/Barcode: " + contents);
            // Use String.format to prevent JS injection issues
            String script = String.format("if(window.QRScanner && window.QRScanner.onScanSuccess) { window.QRScanner.onScanSuccess('%s'); }", contents);
            evaluateJavascript(script);
        }
    }

    @Override
    public String getPluginName() { return "QRScannerPlugin"; }

    @Override
    public void onPageFinished(String url) {
        String js = "if(!window.QRScanner){window.QRScanner={scan:function(){if(window.QRScannerInterface)window.QRScannerInterface.startScan();},onScanSuccess:null,onScanCancelled:null};console.log('QRScanner JS interface ready.');}";
        evaluateJavascript(js);
    }

    public void startScan() {
        if (activity == null || launcher == null) {
            Log.e(TAG, "Plugin not ready or launcher not set by MainActivity.");
            return;
        }
        // Use the modern ScanOptions builder
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt("Scan a barcode or QR code");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false); // Allow orientation changes

        launcher.launch(options);
    }

    public class QRScannerJSInterface {
        @JavascriptInterface
        public void startScan() {
            // Ensure this runs on the main thread
            activity.runOnUiThread(QRScannerPlugin.this::startScan);
        }
    }

    // --- Unused interface methods ---
    @Override public void onActivityResult(int r, int c, Intent d) {}
    @Override public void onRequestPermissionsResult(int r, @NonNull String[] p, @NonNull int[] g) {}
    @Override public boolean shouldOverrideUrlLoading(WebView v, String u) { return false; }
    @Override public void onPageStarted(String url) {}
    @Override public void onResume() {}
    @Override public void onPause() {}
    @Override public void onDestroy() {}
    @Override public void evaluateJavascript(String script) {
        if (webView != null) webView.evaluateJavascript(script, null);
    }
}