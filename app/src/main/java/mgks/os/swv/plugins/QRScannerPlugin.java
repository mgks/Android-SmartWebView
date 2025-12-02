package mgks.os.swv.plugins;

/*
  QR/Barcode Scanner Plugin for Smart WebView

  PROPRIETARY LICENSE - NOT OPEN SOURCE
  * This plugin is a premium component and is NOT covered by the MIT license of the core Smart WebView project. Usage requires a valid license from the author.

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
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
    private ActivityResultLauncher<Intent> qrCodeLauncher;

    static {
        PluginManager.registerPlugin(new QRScannerPlugin(), new HashMap<>());
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView;

        if (activity instanceof AppCompatActivity) {
            qrCodeLauncher = ((AppCompatActivity) activity).registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), result.getData());
                        if (intentResult != null) {
                            String contents = intentResult.getContents();
                            if (contents != null) {
                                Log.d(TAG, "Scanned QR/Barcode: " + contents);
                                String script = String.format("if(window.QRScanner && window.QRScanner.onScanSuccess) { window.QRScanner.onScanSuccess('%s'); }", contents);
                                evaluateJavascript(script);
                            } else {
                                Log.d(TAG, "Scan cancelled");
                                String script = "if(window.QRScanner && window.QRScanner.onScanCancelled) { window.QRScanner.onScanCancelled(); }";
                                evaluateJavascript(script);
                            }
                        }
                    });
        }

        webView.addJavascriptInterface(new QRScannerJSInterface(), "QRScannerInterface");
        Log.d(TAG, "QRScannerPlugin initialized.");
    }

    @Override
    public String getPluginName() {
        return "QRScannerPlugin";
    }

    @Override
    public void onPageFinished(String url) {
        String js = "if(!window.QRScanner){window.QRScanner={scan:function(){if(window.QRScannerInterface)window.QRScannerInterface.startScan();},onScanSuccess:null,onScanCancelled:null};console.log('QRScanner JS interface ready.');}";
        evaluateJavascript(js);
    }

    public void startScan() {
        if (activity == null || qrCodeLauncher == null) {
            Log.e(TAG, "Plugin not ready or not initialized in an AppCompatActivity.");
            return;
        }
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode or QR code");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        qrCodeLauncher.launch(integrator.createScanIntent());
    }

    public class QRScannerJSInterface {
        @JavascriptInterface
        public void startScan() {
            activity.runOnUiThread(() -> QRScannerPlugin.this.startScan());
        }
    }

    // Unused interface methods
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