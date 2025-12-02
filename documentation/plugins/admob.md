---
title: 'AdMob Plugin (Premium)'
description: 'Integrating Google AdMob advertisements.'
icon: 'dollar-sign'
---

This premium plugin facilitates the integration of Google AdMob ads (Banner, Interstitial, and Rewarded) into your Smart WebView application.

::: callout tip
All Premium Plugins are now available for free and open source to developers. Consider becoming **[Project Sponsor](https://github.com/sponsors/mgks/sponsorships?sponsor=mgks&tier_id=468838)**.
:::

---

## Setup and Configuration

1.  **Obtain the Plugin:** Acquire the plugin files through a GitHub sponsorship.
2.  **Add to Project:** Place the `AdMobPlugin.java` file in the `plugins/` directory.
3.  **AdMob App ID:**
    *   Add your AdMob App ID to `AndroidManifest.xml`:
        ```xml
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="YOUR_ADMOB_APP_ID"/>
        ```
    *   Also, update the `app_id` string in `res/values/ads.xml`.
4.  **Enable Plugin:** Ensure the plugin is enabled in `SmartWebView.java`:
    ```java
    put("AdMobPlugin", true);
    ```
5.  **Configure Ad Units:** In `AdMobPlugin.java`, replace the default test ad unit IDs in the `static` block with your real ad unit IDs for production.
    ```java
    // In AdMobPlugin.java
    static {
        Map<String, Object> config = new HashMap<>();
        // ...
        config.put("bannerAdUnitId", "YOUR_BANNER_ID");
        config.put("interstitialAdUnitId", "YOUR_INTERSTITIAL_ID");
        config.put("rewardedAdUnitId", "YOUR_REWARDED_ID");
        // ...
    }
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

// Check if an ad is ready to be shown
if (window.AdMob.isInterstitialReady()) {
  // Safe to call showInterstitial()
}
```

### Callbacks in JavaScript

You can define callback functions in your JavaScript to react to ad events.

```javascript
// Called when a user earns a reward from a rewarded ad
window.AdMob.onUserEarnedReward = function(reward) {
  console.log("User earned reward!", reward.amount, reward.type);
  // Grant the user their reward in the web app
};