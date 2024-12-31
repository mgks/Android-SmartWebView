package mgks.os.swv;

/*
  Smart WebView 7.0

  MIT License (https://opensource.org/licenses/MIT)

  Smart WebView is an Open Source project that integrates native features into
  WebView to help create advanced hybrid applications (https://github.com/mgks/Android-SmartWebView).

  Explore plugins and enhanced capabilities: (https://mgks.dev/blog/smart-webview-plugins)
  Join the discussion: (https://github.com/mgks/Android-SmartWebView/discussions)
  Support Smart WebView: (https://github.com/sponsors/mgks)

  Your support and acknowledgment of the project's source are greatly appreciated.
  Giving credit to developers encourages them to create better projects.
*/

import android.app.Activity;
import android.content.Intent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.NonNull;

public interface PluginInterface {
	void initialize(Activity activity, WebView webView);
	String getPluginName();
	String[] getOverriddenUrls();
	void handlePermissionRequest(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
	void handleActivityResult(int requestCode, int resultCode, Intent data);
	boolean shouldOverrideUrlLoading(WebView view, String url);
	void onPageStarted(String url);
	void onPageFinished(String url);

	boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request);

	void onQRCodeScanResult(String result);
}
