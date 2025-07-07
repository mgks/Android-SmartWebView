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
import android.util.Log;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager {
	private static final String TAG = "PluginManager";
	private Activity activity;
	private WebView webView;
	private Functions functions;
	private final List<PluginInterface> plugins = new ArrayList<>();
	private final Map<String, Map<String, Object>> pluginConfigs = new HashMap<>();

	private Playground playground;
	public void setPlayground(Playground playground) {
		this.playground = playground;
	}

	// No constructor needed.  Initialization happens via SmartWebView.init().

	public static void registerPlugin(PluginInterface plugin, Map<String, Object> config) {
		PluginManager instance = SmartWebView.getPluginManager();
		String pluginName = plugin.getPluginName();

		// Check if plugins are globally enabled and if this specific plugin is enabled
		if (!SmartWebView.ASWP_PLUGIN_SETTINGS.getOrDefault(pluginName, false)) {
			Log.w(TAG, "Plugin registration skipped: '" + pluginName + "' is disabled in configuration.");
			return;
		}

		if (instance.getPlugin(pluginName) != null) {
			Log.w(TAG, "Plugin already registered: " + pluginName);
			return;
		}
		instance.plugins.add(plugin);
		instance.pluginConfigs.put(pluginName, config);

		// If context is already available, initialize immediately.
		if (instance.activity != null) {
			plugin.initialize(instance.activity, instance.webView, instance.functions, config);
			Log.d(TAG, "Plugin initialized immediately: " + pluginName);
		} else {
			Log.d(TAG, "Plugin registration queued: " + pluginName + ". Waiting for context...");
		}
	}

	/**
	 * Sets the context and initializes all queued plugins.
	 */
	public void setContext(Activity activity, WebView webView, Functions functions) {
		this.activity = activity;
		this.webView = webView;
		this.functions = functions;

		// Initialize any plugins that were registered before the context was available.
		for (PluginInterface plugin : plugins) {
			Map<String, Object> config = pluginConfigs.get(plugin.getPluginName());
			// Check if the plugin was already initialized (can happen if context is set late)
			// A simple check could be to see if a key member like 'activity' is already set in the plugin if it were public,
			// but for now, we re-initialize. A more robust system might have an isInitialized() flag in the plugin interface.
			plugin.initialize(activity, webView, functions, config);
			Log.d(TAG, "Delayed plugin initialization completed for: " + plugin.getPluginName());
		}
	}

	private PluginInterface getPlugin(String pluginName) {
		for (PluginInterface plugin : plugins) {
			if (plugin.getPluginName().equals(pluginName)) {
				return plugin;
			}
		}
		return null;
	}

	/**
	 * Retrieves the configuration map for a given plugin.
	 * This is used by Playground to apply configurations externally.
	 * @param pluginName The name of the plugin.
	 * @return The configuration map, or null if the plugin is not found.
	 */
	public Map<String, Object> getPluginConfig(String pluginName) {
		return pluginConfigs.get(pluginName);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		for (PluginInterface plugin : plugins) {
			plugin.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		for (PluginInterface plugin : plugins) {
			plugin.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		for (PluginInterface plugin : plugins) {
			if (plugin.shouldOverrideUrlLoading(view, url)) {
				return true;
			}
		}
		return false;
	}

	public void onPageStarted(String url) {
		for (PluginInterface plugin : plugins) {
			plugin.onPageStarted(url);
		}
	}

	public void onPageFinished(String url) {
		for (PluginInterface plugin : plugins) {
			plugin.onPageFinished(url);
		}
		if (this.playground != null) { // Add this check and call
			this.playground.onPageFinished();
		}
	}

	public void onResume() {
		for (PluginInterface plugin : plugins) {
			plugin.onResume();
		}
	}

	public void onDestroy() {
		for (PluginInterface plugin : plugins) {
			plugin.onDestroy();
		}
		plugins.clear();
		pluginConfigs.clear();
	}

	public PluginInterface getPluginInstance(String pluginName) {
		for (PluginInterface plugin : plugins) {
			if (plugin.getPluginName().equals(pluginName)) {
				return plugin;
			}
		}
		return null;
	}

	public void evaluateJavascript(String script) {
		if (webView != null) {
			webView.evaluateJavascript(script, null);
		}
	}
}