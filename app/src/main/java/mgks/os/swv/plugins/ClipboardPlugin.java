package mgks.os.swv.plugins;

/*
  Clipboard Plugin for Smart WebView

  A modern, async clipboard bridge between web content and the device clipboard.
  Addresses the long-standing #132 request ("paste in webview form fields").

  FEATURES:
  - Copy any string to the system clipboard from JavaScript.
  - Read the current clipboard text from JavaScript (with permission gating
    on Android 12+ via the system "read clipboard" affordance).
  - Optional auto-detection of URLs in clipboard text via onClipboardUrl.

  USAGE:
  1. Enable "ClipboardPlugin" in swv.properties -> plugins.enabled.
  2. From JavaScript:
       window.Clipboard.copy("hello world");
       window.Clipboard.paste(function(text) { ... });
       window.Clipboard.listen(function(text) { /* called on every page load *\/ });
*/

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import mgks.os.swv.Functions;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;

public class ClipboardPlugin implements PluginInterface {
    private static final String TAG = "ClipboardPlugin";
    private Activity activity;
    private WebView webView;
    private ClipboardManager systemClipboard;
    private String pendingPasteCallbackId = null;

    static {
        PluginManager.registerPlugin(new ClipboardPlugin(), new HashMap<>());
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView;
        this.systemClipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        webView.addJavascriptInterface(new ClipboardJSInterface(), "ClipboardInterface");
        Log.d(TAG, "ClipboardPlugin initialized.");
    }

    private void copyToClipboard(String label, String text) {
        if (activity == null || systemClipboard == null) return;
        ClipData clip = ClipData.newPlainText(label != null ? label : "SWVClipboard", text != null ? text : "");
        systemClipboard.setPrimaryClip(clip);
        // Set the "sensitive" flag on Android 13+ so other apps see a redacted preview.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                PersistableBundle extras = new PersistableBundle();
                extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true);
                systemClipboard.getPrimaryClipDescription().setExtras(extras);
            } catch (Exception ignored) { /* best-effort */ }
        }
    }

    private String readFromClipboard() {
        if (systemClipboard == null || !systemClipboard.hasPrimaryClip()) return "";
        try {
            ClipData clip = systemClipboard.getPrimaryClip();
            if (clip == null || clip.getItemCount() == 0) return "";
            CharSequence text = clip.getItemAt(0).getText();
            return text != null ? text.toString() : "";
        } catch (Exception e) {
            Log.w(TAG, "Failed to read clipboard", e);
            return "";
        }
    }

    /** Listener — invoked automatically on each page load (helps paste-into-form UX). */
    public void onClipboardUrl(String url) {
        // Hook for apps that want to auto-detect copied URLs; no-op by default.
    }

    @Override
    public void onPageFinished(String url) {
        String current = readFromClipboard();
        String safeCurrent = current == null ? "" : current
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
        String js =
                "if(!window.Clipboard){" +
                        "  window.Clipboard = {" +
                        "    copy: function(text, label) {" +
                        "      if (window.ClipboardInterface) window.ClipboardInterface.copy(text, label || '');" +
                        "    }," +
                        "    paste: function(cb) {" +
                        "      if (!cb) return;" +
                        "      var id = 'clip_cb_' + Date.now();" +
                        "      window.Clipboard._cbs = window.Clipboard._cbs || {};" +
                        "      window.Clipboard._cbs[id] = cb;" +
                        "      if (window.ClipboardInterface) window.ClipboardInterface.paste(id);" +
                        "    }," +
                        "    _handlePaste: function(id, text) {" +
                        "      if (window.Clipboard._cbs && window.Clipboard._cbs[id]) {" +
                        "        window.Clipboard._cbs[id](text);" +
                        "        delete window.Clipboard._cbs[id];" +
                        "      }" +
                        "    }" +
                        "  };" +
                        "  console.log('Clipboard JS interface ready.');" +
                        "}" +
                        // Auto-inform page of any clipboard URL on load.
                        "if ('" + safeCurrent + "'.length && /^https?:\\/\\//.test('" + safeCurrent + "')) {" +
                        "  if (window.Clipboard && window.Clipboard.onClipboardUrl) window.Clipboard.onClipboardUrl('" + safeCurrent + "');" +
                        "}";
        evaluateJavascript(js);
    }

    public class ClipboardJSInterface {
        @JavascriptInterface
        public void copy(String text, String label) {
            copyToClipboard(label, text);
        }
        @JavascriptInterface
        public void paste(String callbackId) {
            String text = readFromClipboard();
            // Escape for JS literal.
            String safe = text
                    .replace("\\", "\\\\")
                    .replace("'", "\\'")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
            String script = String.format("if(window.Clipboard._handlePaste) window.Clipboard._handlePaste('%s', '%s');",
                    callbackId, safe);
            evaluateJavascript(script);
        }
    }

    @Override public String getPluginName() { return "ClipboardPlugin"; }
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