package mgks.os.swv.plugins;

/*
  Image Compression Plugin for Smart WebView

  This plugin compresses images before they are uploaded.

  FEATURES:
  - Compresses base64 encoded images.
  - Configurable quality and size.
  - Returns a compressed base64 string to JavaScript.
*/

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import mgks.os.swv.Functions;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;

public class ImageCompressionPlugin implements PluginInterface {
    private static final String TAG = "ImageCompressionPlugin";
    private Activity activity;
    private WebView webView;
    private int quality;

    static {
        Map<String, Object> config = new HashMap<>();
        config.put("quality", 80); // Default compression quality (0-100)
        PluginManager.registerPlugin(new ImageCompressionPlugin(), config);
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView;
        this.quality = (int) config.getOrDefault("quality", 80);
        webView.addJavascriptInterface(new ImageCompressionJSInterface(), "ImageCompressionInterface");
        Log.d(TAG, "ImageCompressionPlugin initialized with quality: " + this.quality);
    }

    @Override
    public String getPluginName() {
        return "ImageCompressionPlugin";
    }

    @Override
    public void onPageFinished(String url) {
        String js = "if(!window.ImageCompressor){window.ImageCompressor={compress:function(base64,cb){if(window.ImageCompressionInterface){window.ImageCompressor.callback=cb;window.ImageCompressionInterface.compress(base64);}},callback:null};console.log('ImageCompressor JS interface ready.');}";
        evaluateJavascript(js);
    }

    public void compress(String base64String) {
        // This is a simplified example. For large images, this should be done on a background thread.
        try {
            // Remove header: "data:image/jpeg;base64,"
            String pureBase64 = base64String.substring(base64String.indexOf(",") + 1);
            byte[] decodedString = Base64.decode(pureBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, this.quality, outputStream);
            byte[] compressedBytes = outputStream.toByteArray();
            String compressedBase64 = Base64.encodeToString(compressedBytes, Base64.DEFAULT);

            // Prepend the data URL scheme header back
            String finalBase64 = "data:image/jpeg;base64," + compressedBase64;

            Log.d(TAG, "Image compressed from " + base64String.length() + " to " + finalBase64.length() + " bytes.");
            String script = String.format("if(window.ImageCompressor && window.ImageCompressor.callback) { window.ImageCompressor.callback('%s'); }", finalBase64);
            evaluateJavascript(script);

        } catch (Exception e) {
            Log.e(TAG, "Image compression failed", e);
            String script = String.format("if(window.ImageCompressor && window.ImageCompressor.callback) { window.ImageCompressor.callback(null, '%s'); }", e.getMessage());
            evaluateJavascript(script);
        }
    }

    public class ImageCompressionJSInterface {
        @JavascriptInterface
        public void compress(String base64String) {
            // Run compression on the main thread for simplicity, but a background thread is recommended.
            new Handler(Looper.getMainLooper()).post(() -> ImageCompressionPlugin.this.compress(base64String));
        }
    }

    // Unused interface methods
    @Override public void onActivityResult(int r, int c, Intent d) {}
    @Override public void onRequestPermissionsResult(int r, @NonNull String[] p, @NonNull int[] g) {}
    @Override public boolean shouldOverrideUrlLoading(WebView v, String u) { return false; }
    @Override public void onResume() {}
    @Override public void onPause() {}
    @Override public void onPageStarted(String url) {}
    @Override public void onDestroy() {}
    @Override public void evaluateJavascript(String script) {
        if (webView != null) webView.evaluateJavascript(script, null);
    }
}