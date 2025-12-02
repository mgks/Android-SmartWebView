package mgks.os.swv.plugins;

/*
  AdMob Plugin for Smart WebView

  PROPRIETARY LICENSE - NOT OPEN SOURCE
  * This plugin is a premium component and is NOT covered by the MIT license of the core Smart WebView project. Usage requires a valid license from the author.

  This premium plugin enables easy integration of Google AdMob ads into your Smart WebView app.
  It handles banner ads, interstitial ads, and rewarded ads with minimal configuration.

  FEATURES:
  - Banner ads with customizable sizes
  - Interstitial ads for natural transition points
  - Rewarded ads for user incentives
  - JavaScript interface for triggering ads from web content
  - Event callbacks for ad loading, showing, and closing
  - Test mode for development

  USAGE:
  1. Configure your AdMob app ID in the Android manifest
  2. Add the ad container to your layout
  3. Use this plugin to show ads with just a few lines of code
*/

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import mgks.os.swv.Functions;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;
import mgks.os.swv.R;

public class AdMobPlugin implements PluginInterface {
    private static final String TAG = "AdMobPlugin";
    private Activity activity;
    private WebView webView;
    private Map<String, Object> config;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Ad units
    private String bannerAdUnitId;
    private String interstitialAdUnitId;
    private String rewardedAdUnitId;

    // Ad instances
    private AdView bannerAd;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;

    // Flags
    private boolean isInitialized = false;
    private final AtomicBoolean isInterstitialLoading = new AtomicBoolean(false);
    private final AtomicBoolean isRewardedLoading = new AtomicBoolean(false);

    // Static initializer block for self-registration
    static {
        Map<String, Object> config = new HashMap<>();

        // Default configuration
        config.put("testMode", true);  // Use test ads for development
        config.put("bannerAdUnitId", "ca-app-pub-3940256099942544/6300978111");  // Test Banner Ad Unit ID
        config.put("interstitialAdUnitId", "ca-app-pub-3940256099942544/1033173712");  // Test Interstitial Ad Unit ID
        config.put("rewardedAdUnitId", "ca-app-pub-3940256099942544/5224354917");  // Test Rewarded Ad Unit ID
        config.put("enableJsInterface", true);  // Enable JavaScript interface for calling from web
        config.put("autoLoadInterstitial", true);  // Auto-load interstitial after showing
        config.put("autoLoadRewarded", true);  // Auto-load rewarded after showing

        PluginManager.registerPlugin(new AdMobPlugin(), config);
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView;
        this.config = config;

        // Get configuration - safely handle possible null values
        bannerAdUnitId = (String) config.getOrDefault("bannerAdUnitId", "ca-app-pub-3940256099942544/6300978111");
        interstitialAdUnitId = (String) config.getOrDefault("interstitialAdUnitId", "ca-app-pub-3940256099942544/1033173712");
        rewardedAdUnitId = (String) config.getOrDefault("rewardedAdUnitId", "ca-app-pub-3940256099942544/5224354917");

        // Initialize MobileAds
        MobileAds.initialize(activity, this::onMobileAdsInitialized);

        // Add JavaScript interface if enabled
        if (Boolean.TRUE.equals(config.getOrDefault("enableJsInterface", true))) {
            webView.addJavascriptInterface(new AdMobJSInterface(), "AdMobInterface");
        }

        Log.d(TAG, "AdMobPlugin initialized with config: " + config);
    }

    private void onMobileAdsInitialized(InitializationStatus initializationStatus) {
        isInitialized = true;
        Log.d(TAG, "Mobile Ads initialization complete: " + initializationStatus);

        // Preload ads
        loadInterstitialAd();
        loadRewardedAd();
    }

    @Override
    public String getPluginName() {
        return "AdMobPlugin";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {}

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    @Override
    public void onPageStarted(String url) {}

    @Override
    public void onPageFinished(String url) {
        // Inject Ad-related JavaScript if JS interface is enabled
        if (Boolean.TRUE.equals(config.getOrDefault("enableJsInterface", true))) {
            injectAdSupportJs();
        }
    }

    private void injectAdSupportJs() {
        String adSupportJs =
                "if (!window.AdMob) {\n" +
                        "    window.AdMob = {\n" +
                        "        showBanner: function() { if(window.AdMobInterface) return window.AdMobInterface.showBannerAd(); },\n" +
                        "        hideBanner: function() { if(window.AdMobInterface) return window.AdMobInterface.hideBannerAd(); },\n" +
                        "        showInterstitial: function() { if(window.AdMobInterface) return window.AdMobInterface.showInterstitialAd(); },\n" +
                        "        showRewarded: function() { if(window.AdMobInterface) return window.AdMobInterface.showRewardedAd(); },\n" +
                        "        isInterstitialReady: function() { if(window.AdMobInterface) return window.AdMobInterface.isInterstitialAdReady(); },\n" +
                        "        isRewardedReady: function() { if(window.AdMobInterface) return window.AdMobInterface.isRewardedAdReady(); }\n" +
                        "    };\n" +
                        "    console.log('AdMob JavaScript interface initialized');\n" +
                        "}\n";

        evaluateJavascript(adSupportJs);
    }

    @Override public void onResume() {}

    @Override public void onPause() {}

    @Override
    public void onDestroy() {
        if (bannerAd != null) {
            bannerAd.destroy();
            bannerAd = null;
        }
        interstitialAd = null;
        rewardedAd = null;
    }

    @Override
    public void evaluateJavascript(String script) {
        if (webView != null) {
            webView.evaluateJavascript(script, null);
        }
    }

    public void showBannerAd(ViewGroup adContainer) {
        if (!isInitialized || activity == null) {
            Log.w(TAG, "AdMob not ready or activity is null.");
            return;
        }

        mainHandler.post(() -> {
            if (bannerAd != null) {
                adContainer.removeView(bannerAd);
                bannerAd.destroy();
            }

            bannerAd = new AdView(activity);
            bannerAd.setAdUnitId(bannerAdUnitId);
            bannerAd.setAdSize(AdSize.BANNER);
            adContainer.addView(bannerAd);

            AdRequest adRequest = new AdRequest.Builder().build();
            bannerAd.loadAd(adRequest);
            Log.d(TAG, "Requested banner ad.");
        });
    }

    public void hideBannerAd() {
        mainHandler.post(() -> {
            if (bannerAd != null && bannerAd.getParent() != null) {
                ((ViewGroup) bannerAd.getParent()).removeView(bannerAd);
                bannerAd.destroy();
                bannerAd = null;
                Log.d(TAG, "Banner ad hidden and destroyed.");
            }
        });
    }

    public void loadInterstitialAd() {
        if (!isInitialized || activity == null || isInterstitialLoading.getAndSet(true)) {
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, interstitialAdUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                interstitialAd = ad;
                isInterstitialLoading.set(false);
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        interstitialAd = null;
                        if (Boolean.TRUE.equals(config.getOrDefault("autoLoadInterstitial", true))) {
                            loadInterstitialAd();
                        }
                    }
                });
                Log.d(TAG, "Interstitial ad loaded.");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                interstitialAd = null;
                isInterstitialLoading.set(false);
                Log.e(TAG, "Failed to load interstitial ad: " + loadAdError.getMessage());

                // If the error is due to the JS engine, schedule a retry after a delay.
                if (loadAdError.getCode() == 0) { // Code 0 is often an internal error
                    mainHandler.postDelayed(() -> {
                        Log.d(TAG, "Retrying to load interstitial ad after JS engine failure.");
                        loadInterstitialAd();
                    }, 15000); // Retry after 15 seconds
                }
            }
        });
    }

    public boolean showInterstitialAd() {
        if (interstitialAd == null || activity == null) {
            Log.w(TAG, "Interstitial ad not ready.");
            if (!isInterstitialLoading.get()) loadInterstitialAd();
            return false;
        }

        mainHandler.post(() -> interstitialAd.show(activity));
        return true;
    }

    public void loadRewardedAd() {
        if (!isInitialized || activity == null || isRewardedLoading.getAndSet(true)) {
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(activity, rewardedAdUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                isRewardedLoading.set(false);
                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        rewardedAd = null;
                        if (Boolean.TRUE.equals(config.getOrDefault("autoLoadRewarded", true))) {
                            loadRewardedAd();
                        }
                    }
                });
                Log.d(TAG, "Rewarded ad loaded.");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                rewardedAd = null;
                isRewardedLoading.set(false);
                Log.e(TAG, "Failed to load rewarded ad: " + loadAdError.getMessage());

                // If the error is due to the JS engine, schedule a retry after a delay.
                if (loadAdError.getCode() == 0) {
                    mainHandler.postDelayed(() -> {
                        Log.d(TAG, "Retrying to load rewarded ad after JS engine failure.");
                        loadRewardedAd();
                    }, 15000); // Retry after 15 seconds
                }
            }
        });
    }

    public boolean showRewardedAd() {
        if (rewardedAd == null || activity == null) {
            Log.w(TAG, "Rewarded ad not loaded yet.");
            if (!isRewardedLoading.get()) loadRewardedAd();
            return false;
        }

        mainHandler.post(() -> rewardedAd.show(activity, rewardItem -> {
            Log.d(TAG, "User earned reward: " + rewardItem.getAmount() + " " + rewardItem.getType());
            try {
                JSONObject rewardData = new JSONObject();
                rewardData.put("amount", rewardItem.getAmount());
                rewardData.put("type", rewardItem.getType());
                evaluateJavascript("if (window.AdMob && window.AdMob.onUserEarnedReward) window.AdMob.onUserEarnedReward(" + rewardData.toString() + ");");
            } catch (JSONException e) {
                Log.e(TAG, "Error creating reward JSON", e);
            }
        }));

        return true;
    }

    public boolean isInterstitialAdReady() {
        return interstitialAd != null;
    }

    public boolean isRewardedAdReady() {
        return rewardedAd != null;
    }

    public class AdMobJSInterface {
        @JavascriptInterface
        public void showBannerAd() {
            mainHandler.post(() -> {
                ViewGroup adContainer = activity.findViewById(R.id.msw_ad_container);
                if (adContainer != null) {
                    AdMobPlugin.this.showBannerAd(adContainer);
                } else {
                    Log.e(TAG, "Ad container with ID 'msw_ad_container' not found in layout! Cannot show banner ad.");
                }
            });
        }

        @JavascriptInterface
        public void hideBannerAd() {
            AdMobPlugin.this.hideBannerAd();
        }

        @JavascriptInterface
        public boolean showInterstitialAd() {
            return AdMobPlugin.this.showInterstitialAd();
        }

        @JavascriptInterface
        public boolean showRewardedAd() {
            return AdMobPlugin.this.showRewardedAd();
        }

        @JavascriptInterface
        public boolean isInterstitialAdReady() {
            return AdMobPlugin.this.isInterstitialAdReady();
        }

        @JavascriptInterface
        public boolean isRewardedAdReady() {
            return AdMobPlugin.this.isRewardedAdReady();
        }
    }
}