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
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager {
	private static final String TAG = "PluginManager";
	public static Activity activity;
	public static WebView webView;
	private static final Map<String, PluginInterface> plugins = new HashMap<>();
	private static final List<String> overriddenUrls = new ArrayList<>();

	public PluginManager(Activity activity, WebView webView) {
		this.activity = activity;
		this.webView = webView;
		// Plugins will have registered themselves by now
		logRegisteredPlugins();
	}

	public static void registerPlugin(String pluginName, PluginInterface plugin, String[] urlsToOverride) {
		if (plugins.containsKey(pluginName)) {
			Log.w(TAG, "Plugin already registered: " + pluginName);
			return;
		}
		plugins.put(pluginName, plugin);
		for (String url : urlsToOverride) {
			overriddenUrls.add(url);
		}
		plugin.initialize(activity, webView);
		Log.d(TAG, "Plugin registered: " + pluginName);
	}

	private void logRegisteredPlugins() {
		Log.d(TAG, "Registered plugins:");
		for (String pluginName : plugins.keySet()) {
			Log.d(TAG, "- " + pluginName);
		}
	}

	public void onPageStarted(String url) {
		for (PluginInterface plugin : plugins.values()) {
			plugin.onPageStarted(url);
		}
	}

	public void onPageFinished(String url) {
		for (PluginInterface plugin : plugins.values()) {
			plugin.onPageFinished(url);
		}
	}

	// Updated to use the shouldOverrideUrlLoading method of each plugin
	public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
		String url = request.getUrl().toString();
		for (PluginInterface plugin : plugins.values()) {
			if (plugin.shouldOverrideUrlLoading(view, url)) {
				return true;
			}
		}
		return false;
	}

	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		for (PluginInterface plugin : plugins.values()) {
			plugin.handlePermissionRequest(requestCode, permissions, grantResults);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		for (PluginInterface plugin : plugins.values()) {
			plugin.handleActivityResult(requestCode, resultCode, data);
		}
	}
}
