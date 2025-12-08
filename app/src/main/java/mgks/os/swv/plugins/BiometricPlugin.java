package mgks.os.swv.plugins;

/*
  Biometric Authentication Plugin for Smart WebView

  This plugin provides access to fingerprint or face authentication.

  Features:
  - High-level API for biometric checks.
  - Securely prompts user for authentication.
  - Provides success/failure callbacks to JavaScript.
  - Fallback to lower level of security measures if hardware security not supported.
*/

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import mgks.os.swv.Functions;
import mgks.os.swv.PluginInterface;
import mgks.os.swv.PluginManager;
import mgks.os.swv.R;
import mgks.os.swv.SWVContext;

public class BiometricPlugin implements PluginInterface {
    private static final String TAG = "BiometricPlugin";
    private AppCompatActivity activity;
    private WebView webView;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private View securityOverlay;
    private Button retryAuthButton;

    // --- NEW: References to UI elements to be controlled ---
    private View appBar;
    private DrawerLayout drawerLayout;
    // --- END NEW ---

    private boolean isAuthenticated = false;
    private boolean authIsRequiredOnLaunch = false;
    private boolean deviceHasSecurity = false;

    static {
        Map<String, Object> config = new HashMap<>();
        config.put("authOnAppLaunch", false);
        PluginManager.registerPlugin(new BiometricPlugin(), config);
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        if (!(activity instanceof AppCompatActivity)) {
            Log.e(TAG, "BiometricPlugin requires an AppCompatActivity.");
            return;
        }
        this.activity = (AppCompatActivity) activity;
        this.webView = webView;

        this.securityOverlay = this.activity.findViewById(R.id.security_overlay);
        if (this.securityOverlay == null) {
            Log.e(TAG, "Security overlay with ID 'security_overlay' not found! Biometric lock screen will not function.");
        }
        this.retryAuthButton = this.activity.findViewById(R.id.retry_auth_button);

        // These will be null in fullscreen mode, which is handled gracefully later.
        this.appBar = this.activity.findViewById(R.id.app_bar);
        this.drawerLayout = this.activity.findViewById(R.id.drawer_layout);

        this.authIsRequiredOnLaunch = SWVContext.ASWP_BIOMETRIC_ON_LAUNCH;

        executor = ContextCompat.getMainExecutor(this.activity);
        biometricPrompt = new BiometricPrompt(this.activity, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e(TAG, "Authentication error: " + errString + " Code: " + errorCode);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON || errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    Log.w(TAG, "User cancelled authentication. Overlay remains, UI remains locked.");
                    String script = String.format("if(window.Biometric && window.Biometric.onAuthError) { window.Biometric.onAuthError('%s'); }", errString);
                    evaluateJavascript(script);
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d(TAG, "Authentication succeeded!");
                isAuthenticated = true;
                hideOverlay(); // This will now also restore the UI
                String script = "if(window.Biometric && window.Biometric.onAuthSuccess) { window.Biometric.onAuthSuccess(); }";
                evaluateJavascript(script);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.w(TAG, "Authentication failed. User can retry.");
                String script = "if(window.Biometric && window.Biometric.onAuthFailed) { window.Biometric.onAuthFailed(); }";
                evaluateJavascript(script);
            }
        });

        BiometricPrompt.PromptInfo.Builder promptBuilder = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication Required")
                .setSubtitle("Log in to continue");

        promptBuilder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
        promptInfo = promptBuilder.build();

        if (retryAuthButton != null) {
            retryAuthButton.setOnClickListener(v -> {
                if (deviceHasSecurity) {
                    if (biometricPrompt != null) {
                        biometricPrompt.authenticate(promptInfo);
                    }
                } else {
                    Log.d(TAG, "No device security set. Redirecting to security settings.");
                    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                    activity.startActivity(intent);
                }
            });
        }

        webView.addJavascriptInterface(new BiometricJSInterface(), "BiometricInterface");
        Log.d(TAG, "BiometricPlugin initialized.");
    }

    private boolean isDeviceSecure() {
        if (activity == null) return false;
        BiometricManager biometricManager = BiometricManager.from(activity);
        int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL;
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public void authenticate() {
        if (activity == null || activity.isFinishing() || isAuthenticated) return;
        this.deviceHasSecurity = isDeviceSecure();
        showOverlay(); // This will now also lock the UI
        if (this.deviceHasSecurity) {
            if (biometricPrompt != null) {
                biometricPrompt.authenticate(promptInfo);
            } else {
                Log.e(TAG, "Cannot authenticate, BiometricPrompt was not initialized.");
            }
        } else {
            Log.w(TAG, "Device is not secure. Waiting for user to click button to go to settings.");
        }
    }

    @Override
    public void onResume() {
        if (authIsRequiredOnLaunch && !isAuthenticated) {
            authenticate();
        }
    }

    @Override
    public void onPause() {
        if (authIsRequiredOnLaunch) {
            isAuthenticated = false;
            Log.d(TAG, "App paused, biometric session invalidated.");
        }
    }

    private void showOverlay() {
        if (activity == null) return;
        // Call the helper method in MainActivity to enable FLAG_SECURE
        if (activity instanceof mgks.os.swv.MainActivity) {
            ((mgks.os.swv.MainActivity) activity).setWindowSecure(true);
        }
        activity.runOnUiThread(() -> {
            if (securityOverlay != null) securityOverlay.setVisibility(View.VISIBLE);
            if (appBar != null) appBar.setVisibility(View.INVISIBLE);
            if (drawerLayout != null) drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        });
    }

    private void hideOverlay() {
        if (activity == null) return;
        // Call the helper method in MainActivity to disable FLAG_SECURE
        if (activity instanceof mgks.os.swv.MainActivity) {
            ((mgks.os.swv.MainActivity) activity).setWindowSecure(false);
        }
        activity.runOnUiThread(() -> {
            if (securityOverlay != null) securityOverlay.setVisibility(View.GONE);
            if (appBar != null && SWVContext.ASWP_DRAWER_HEADER) appBar.setVisibility(View.VISIBLE);
            if (drawerLayout != null) drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        });
    }

    public class BiometricJSInterface {
        @JavascriptInterface
        public void authenticate() {
            activity.runOnUiThread(BiometricPlugin.this::authenticate);
        }
    }

    // --- Unchanged Methods ---
    @Override public String getPluginName() { return "BiometricPlugin"; }
    @Override public void onPageFinished(String url) {
        String js = "if(!window.Biometric){window.Biometric={authenticate:function(){if(window.BiometricInterface)window.BiometricInterface.authenticate();},onAuthSuccess:null,onAuthError:null,onAuthFailed:null};console.log('Biometric JS interface ready.');}";
        evaluateJavascript(js);
        if (authIsRequiredOnLaunch) {
            authenticate();
        }
    }
    @Override public void onActivityResult(int r, int c, Intent d) {}
    @Override public void onRequestPermissionsResult(int r, @NonNull String[] p, @NonNull int[] g) {}
    @Override public boolean shouldOverrideUrlLoading(WebView v, String u) { return false; }
    @Override public void onPageStarted(String url) {}
    @Override public void onDestroy() {}
    @Override public void evaluateJavascript(String script) { if (webView != null) webView.evaluateJavascript(script, null); }
}