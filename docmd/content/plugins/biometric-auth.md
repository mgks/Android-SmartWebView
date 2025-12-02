---
title: 'Biometric Auth Plugin (Premium)'
description: 'Securing the app with fingerprint or face unlock.'
icon: 'fingerprint'
---

This premium plugin allows you to add an extra layer of security by requiring biometric authentication (fingerprint, face recognition) before granting access to your app or specific features.

::: callout tip
All Premium Plugins are now available for free and open source to developers. Consider becoming **[Project Sponsor](https://github.com/sponsors/mgks/sponsorships?sponsor=mgks&tier_id=468838)**.
:::

---

## Secure, Non-Bypassable Gate

The authentication flow is designed to be a true security gate, not just a dismissible prompt.

- **Total UI Lock:** When authentication is triggered, a full-screen overlay immediately blocks all app content. The native Toolbar, search menu, and navigation drawer are also hidden and disabled, preventing any interaction with the underlying UI.
- **Guided Security Setup:** If the user does not have a screen lock (PIN, fingerprint, etc.) set up, the overlay will prompt them to secure their device and a button will take them directly to the Android Security Settings.
- **Persistent Lock:** If the user sends the app to the background and then resumes it, the authentication flow is automatically re-triggered, preventing bypass through the "recents" screen.

---

## Setup and Configuration

1.  **Obtain the Plugin:** Acquire the `BiometricPlugin.java` file through a GitHub sponsorship.
2.  **Add to Project:** Place the file in the `plugins/` directory.
3.  **Enable Plugin:** Ensure `BiometricPlugin` is listed in the `plugins.enabled` property in `app/src/main/assets/swv.properties`.
4.  **Configure Auth on Launch:** To enable authentication every time the app starts, set the `biometric.trigger.launch` property to `true` in `swv.properties`.

    ```bash
    # In swv.properties
    biometric.trigger.launch=true
    ```
    If `false` (the default), authentication will only be triggered manually from your JavaScript.

---

## Usage

The plugin is primarily controlled via a JavaScript interface.

### Triggering Authentication from JavaScript

The plugin injects a `window.Biometric` object into your web page. You can call this to lock a specific feature or section of your app.

```javascript
// Request biometric authentication
window.Biometric.authenticate();
```

### Callbacks in JavaScript

Define callback functions in your JavaScript to handle the result of the authentication attempt.

```javascript
// Called on successful authentication
window.Biometric.onAuthSuccess = function() {
  console.log("Authentication successful!");
  // Unlock feature or proceed with login
};

// Called if there's an error (e.g., no hardware, lock screen not set up)
// This is NOT called for simple failed attempts (fingerprint not recognized).
window.Biometric.onAuthError = function(errorMessage) {
  console.error("Authentication error:", errorMessage);
};

// Called when the fingerprint/face is not recognized. The prompt will remain
// visible for the user to try again.
window.Biometric.onAuthFailed = function() {
  console.warn("Authentication failed. Please try again.");
};
```