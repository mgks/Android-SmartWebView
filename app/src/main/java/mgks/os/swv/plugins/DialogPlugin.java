package mgks.os.swv.plugins;

/*
  Dialog Plugin for Smart WebView

  This plugin provides a generic interface for showing native Android alert dialogs
  from JavaScript.

  FEATURES:
  - Highly configurable: set title, message, and button texts.
  - Supports one, two, or three buttons (positive, negative, neutral).
  - Asynchronous, non-blocking callbacks to handle user actions.
  - Centralizes dialog creation for a consistent look and feel.

  USAGE:
  1. From JavaScript, call `window.Dialog.show(options, callback)`.
  2. The `options` object can contain `title`, `message`, `positiveText`, etc.
  3. The `callback` function receives the result ('positive', 'negative', 'neutral', 'cancel').

  // Example JavaScript:
  window.Dialog.show({
    title: 'Confirmation',
    message: 'Do you want to proceed?',
    positiveText: 'Yes',
    negativeText: 'No'
  }, function(result) {
    if (result === 'positive') {
      // User clicked 'Yes'
    }
  });
*/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mgks.os.swv.Functions;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;

public class DialogPlugin implements PluginInterface {
    private static final String TAG = "DialogPlugin";
    private Activity activity;
    private WebView webView;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    static {
        PluginManager.registerPlugin(new DialogPlugin(), new HashMap<>());
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView;
        webView.addJavascriptInterface(new DialogJSInterface(), "DialogInterface");
        Log.d(TAG, "DialogPlugin initialized.");
    }

    public void showDialog(String optionsJson) {
        if (activity == null || activity.isFinishing()) return;

        try {
            JSONObject options = new JSONObject(optionsJson);
            String title = options.optString("title", "Alert");
            String message = options.optString("message", "");
            String positiveText = options.optString("positiveText", "OK");
            String negativeText = options.optString("negativeText", null);
            String neutralText = options.optString("neutralText", null);
            final String callbackId = options.optString("callbackId", null);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMessage(message);

            builder.setPositiveButton(positiveText, (dialog, which) -> {
                if (callbackId != null) {
                    triggerCallback(callbackId, "positive");
                }
            });

            if (negativeText != null) {
                builder.setNegativeButton(negativeText, (dialog, which) -> {
                    if (callbackId != null) {
                        triggerCallback(callbackId, "negative");
                    }
                });
            }

            if (neutralText != null) {
                builder.setNeutralButton(neutralText, (dialog, which) -> {
                    if (callbackId != null) {
                        triggerCallback(callbackId, "neutral");
                    }
                });
            }

            builder.setOnCancelListener(dialog -> {
                if (callbackId != null) {
                    triggerCallback(callbackId, "cancel");
                }
            });

            mainHandler.post(() -> builder.create().show());

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing dialog options JSON", e);
        }
    }

    private void triggerCallback(String callbackId, String result) {
        String script = String.format("if(window.Dialog && window.Dialog.handleCallback) { window.Dialog.handleCallback('%s', '%s'); }", callbackId, result);
        evaluateJavascript(script);
    }

    public class DialogJSInterface {
        @JavascriptInterface
        public void show(String optionsJson) {
            DialogPlugin.this.showDialog(optionsJson);
        }
    }

    @Override
    public void onPageFinished(String url) {
        // Inject JS helper
        String js =
                "if(!window.Dialog){" +
                        "  window.Dialog = {" +
                        "    callbacks: {}," +
                        "    show: function(options, callback) {" +
                        "      var callbackId = 'dialogCallback_' + Date.now();" +
                        "      if (callback) this.callbacks[callbackId] = callback;" +
                        "      options.callbackId = callbackId;" +
                        "      if(window.DialogInterface) window.DialogInterface.show(JSON.stringify(options));" +
                        "    }," +
                        "    handleCallback: function(callbackId, result) {" +
                        "      if (this.callbacks[callbackId]) {" +
                        "        this.callbacks[callbackId](result);" +
                        "        delete this.callbacks[callbackId];" +
                        "      }" +
                        "    }" +
                        "  };" +
                        "  console.log('Dialog JS interface ready.');" +
                        "}";
        evaluateJavascript(js);
    }

    // --- Standard Plugin Interface Methods ---
    @Override public String getPluginName() { return "DialogPlugin"; }
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