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

import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;
import java.util.Map;
import androidx.annotation.NonNull;

public interface PluginInterface {
	void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config);
	String getPluginName();
	void onActivityResult(int requestCode, int resultCode, Intent data);
	void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
	boolean shouldOverrideUrlLoading(WebView view, String url);
	void onPageStarted(String url);
	void onPageFinished(String url);
	void onDestroy();
	void evaluateJavascript(String script);
}