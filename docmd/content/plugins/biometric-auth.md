title: 'Biometric Auth Plugin'
description: 'Securing the app with fingerprint or face unlock.'
icon: 'fingerprint'
---

This plugin allows you to add an extra layer of security by requiring biometric authentication (fingerprint, face recognition) before granting access to your app.

::: callout tip
All Premium Plugins are now available for free and open source to developers. Consider becoming **[Project Sponsor](https://github.com/sponsors/mgks)**.
:::

---

## Secure, Non-Bypassable Gate

The authentication flow is designed to be a true security gate.

- **Total UI Lock:** When authentication is triggered, a full-screen overlay immediately blocks all app content. The native Toolbar and navigation drawer are also hidden and disabled.
- **Guided Security Setup:** If the user has no screen lock, they are prompted to set one up and are guided to the Android Security Settings.
- **Persistent Lock:** Resuming the app from the background will re-trigger authentication, preventing bypass.

---

## Setup and Configuration

1.  **Enable Plugin:** Ensure `BiometricPlugin` is listed in the `plugins.enabled` property in `app/src/main/assets/swv.properties`.
2.  **Configure Auth on Launch:** To enable authentication every time the app starts, set the `biometric.trigger.launch` property to `true` in `swv.properties`.

    ```properties
    # In swv.properties
    plugins.enabled=BiometricPlugin,...
    
    # Require authentication every time the app starts or resumes.
    biometric.trigger.launch=true
    ```
    If `false` (the default), authentication will only be triggered manually from your JavaScript.

---

## Usage

### Triggering Authentication from JavaScript

The plugin injects a `window.Biometric` object. You can call this to lock a specific feature or section of your app.

```javascript
// Request biometric authentication
window.Biometric.authenticate();
```

### Callbacks in JavaScript

Define callback functions to handle the result of the authentication attempt.

```javascript
// Called on successful authentication
window.Biometric.onAuthSuccess = function() {
  console.log("Authentication successful!");
};

// Called if there's an error (e.g., no hardware, lock screen not set up)
window.Biometric.onAuthError = function(errorMessage) {
  console.error("Authentication error:", errorMessage);
};

// Called when the fingerprint/face is not recognized.
window.Biometric.onAuthFailed = function() {
  console.warn("Authentication failed. Please try again.");
};
```