---
title: 'AdMob Plugin'
description: 'Integrating Google AdMob advertisements.'
icon: 'dollar-sign'
---

This plugin facilitates the integration of Google AdMob ads (Banner, Interstitial, and Rewarded) into your Smart WebView application.

::: callout tip
All Premium Plugins are now available for free and open source to developers. Consider becoming **[Project Sponsor](https://github.com/sponsors/mgks)**.
:::

---

## Setup and Configuration

1.  **AdMob App ID:**
    *   Add your AdMob App ID to the `admob_app_id` string in `app/src/main/res/values/ads.xml`.
2.  **Enable Plugin:** Ensure `AdMobPlugin` is listed in the `plugins.enabled` property in `app/src/main/assets/swv.properties`.
    ```bash
    # In swv.properties
    plugins.enabled=AdMobPlugin,DialogPlugin,...
    ```
3.  **Configure Ad Units:** In `Playground.java`, replace the default test ad unit IDs with your real ad unit IDs for production. This keeps your production keys out of the plugin's source code.
    ```java
    // In Playground.java
    runPluginAction("AdMobPlugin", plugin -> {
        Map<String, Object> config = SWVContext.getPluginManager().getPluginConfig("AdMobPlugin");
        if (config != null) {
            config.put("bannerAdUnitId", "YOUR_BANNER_ID");
            config.put("interstitialAdUnitId", "YOUR_INTERSTITIAL_ID");
            config.put("rewardedAdUnitId", "YOUR_REWARDED_ID");
        }
    });
    ```

---
## Usage

The plugin can be controlled from native code or via a JavaScript interface.

### Displaying Ads from JavaScript

The plugin injects a `window.AdMob` object into your web page.

```javascript
// Show a banner ad at the bottom of the screen
window.AdMob.showBanner();

// Hide the banner ad
window.AdMob.hideBanner();

// Show an interstitial ad (if one is loaded)
window.AdMob.showInterstitial();

// Show a rewarded ad (if one is loaded)
window.AdMob.showRewarded();
```

### Safe Calling Pattern (Avoiding Race Conditions)
If you try to call `window.AdMob` immediately when your page loads (e.g., in a footer script), the object might not be injected yet. Use a timeout loop to ensure it is ready:

```javascript
function loadBanner() {
    if (window.AdMob) {
        window.AdMob.showBanner();
    } else {
        setTimeout(loadBanner, 500); // Retry in 500ms
    }
}
loadBanner();
```