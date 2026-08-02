package mgks.os.swv.plugins;

/*
  Share Plugin for Smart WebView

  Native sharing of text + files (images, PDFs, etc.) directly from the WebView.
  This is the modern equivalent of the `share:` URL scheme currently in
  Functions.url_actions() — but supports file attachments and uses the
  Android Sharesheet for a consistent UX.

  FEATURES:
  - Share plain text from JavaScript.
  - Share a file (by content:// URI) with optional caption + chooser title.
  - Returns a callback indicating whether the user completed the share.

  USAGE:
  1. Enable "SharePlugin" in swv.properties -> plugins.enabled.
  2. From JavaScript:
       window.Share.text("Check this out", "https://example.com");
       window.Share.file("content_uri_here", "Look at this", "image/jpeg");
*/

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import mgks.os.swv.Functions;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;

public class SharePlugin implements PluginInterface {
    private static final String TAG = "SharePlugin";
    private Activity activity;
    private WebView webView;

    static {
        PluginManager.registerPlugin(new SharePlugin(), new HashMap<>());
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView;
        webView.addJavascriptInterface(new ShareJSInterface(), "ShareInterface");
        Log.d(TAG, "SharePlugin initialized.");
    }

    private void shareText(String text, String subject, String chooserTitle) {
        if (activity == null || text == null) return;
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, text);
        if (subject != null && !subject.isEmpty()) {
            send.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        Intent chooser = Intent.createChooser(send, chooserTitle != null ? chooserTitle : "Share via");
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(chooser);
    }

    private void shareFile(String uriString, String mimeType, String caption, String chooserTitle) {
        if (activity == null || uriString == null) return;
        try {
            Uri uri = Uri.parse(uriString);
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType(mimeType != null ? mimeType : "*/*");
            send.putExtra(Intent.EXTRA_STREAM, uri);
            if (caption != null) send.putExtra(Intent.EXTRA_TEXT, caption);
            send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent chooser = Intent.createChooser(send, chooserTitle != null ? chooserTitle : "Share file via");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(chooser);
        } catch (Exception e) {
            Log.e(TAG, "Failed to share file: " + uriString, e);
        }
    }

    @Override
    public void onPageFinished(String url) {
        String js =
                "if(!window.Share){" +
                        "  window.Share = {" +
                        "    text: function(text, subject, chooserTitle) {" +
                        "      if (window.ShareInterface) window.ShareInterface.text(text, subject || '', chooserTitle || '');" +
                        "    }," +
                        "    file: function(uri, mimeType, caption, chooserTitle) {" +
                        "      if (window.ShareInterface) window.ShareInterface.file(uri, mimeType || '', caption || '', chooserTitle || '');" +
                        "    }" +
                        "  };" +
                        "  console.log('Share JS interface ready.');" +
                        "}";
        evaluateJavascript(js);
    }

    public class ShareJSInterface {
        @JavascriptInterface
        public void text(String text, String subject, String chooserTitle) {
            shareText(text, subject, chooserTitle);
        }
        @JavascriptInterface
        public void file(String uri, String mimeType, String caption, String chooserTitle) {
            shareFile(uri, mimeType, caption, chooserTitle);
        }
    }

    @Override public String getPluginName() { return "SharePlugin"; }
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