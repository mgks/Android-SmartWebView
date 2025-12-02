---
title: 'Configuration'
description: 'Configuring your Smart WebView app using the swv.properties file.'
icon: 'sliders'
---

All core behaviors and feature toggles in Smart WebView are controlled from a single configuration file: `app/src/main/assets/swv.properties`. This allows you to customize your app without touching any Java or Gradle code.

---

## How to Configure

1.  Open your project in Android Studio.
2.  Navigate to the `app/src/main/assets/` directory.
3.  Open the `swv.properties` file and modify the values as needed.
4.  Rebuild your project (`Build > Rebuild Project`) for the changes to take effect.

---

## Key Configuration Properties

### App & URLs
Define the web addresses your application will load.
```bash
# The main URL your app will load.
app.url=https://mgks.github.io/Android-SmartWebView/

# The local HTML file to show when the app is offline.
offline.url=file:///android_asset/web/offline.html

# The base URL for the search feature (used in Drawer Layout).
search.url=https://www.google.com/search?q=
```

### Feature Toggles
Enable or disable specific native features with `true` or `false`.
```bash
# Enable file uploads from a web form (<input type="file">).
feature.uploads=true

# Enable the pull-to-refresh gesture.
feature.pull.refresh=true

# Show a confirmation dialog before exiting the app.
feature.exit.dialog=true

# Open external links in a Chrome Custom Tab or external browser.
feature.open.external.urls=true
```

### Security
Control app-wide security settings.
```bash
# Verify SSL certificates for HTTPS connections. Set to false only for development with self-signed certs.
security.verify.ssl=true

# Block screenshots, screen recording, and content visibility in the "recents" screen.
# Default is false. Set to true to enforce for the entire app session.
security.block.screenshots=false

# Allow the WebView to accept third-party cookies. Required for some captcha (like Google reCAPTCHA) and social login providers.
# Default is false. Set to true to enable.
security.accept.thirdparty.cookies=false
```

### UI & Layout
Control screen orientation and the main navigation structure.
```bash
# Set the default device orientation. 0=Unspecified, 1=Portrait, 2=Landscape
ui.orientation=0

# Set the main app layout. 0=Fullscreen, 1=Drawer Layout
ui.layout=1

# Show/hide the toolbar when using the Drawer Layout.
ui.drawer.header=true

# Extend the splash screen until the first page is fully rendered.
ui.splash.extend=true
```

### Behavior & Advanced Features
```
# [In v8.0+] Enable injection of a custom stylesheet.
# The stylesheet must be placed at `app/src/main/assets/web/custom.css`.
feature.custom.css=false

# [In v8.0+] If true, pressing the back button will always exit the app
# instead of navigating back in WebView history. Default is false.
behavior.back.exits=false
```

### Plugins
Control which plugins are active and their core behaviors.
```bash
# Comma-separated list of plugins to enable. Case-sensitive.
plugins.enabled=AdMobPlugin,JSInterfacePlugin,ToastPlugin,QRScannerPlugin,BiometricPlugin,ImageCompressionPlugin

# Enable the Playground floating UI for testing plugins. Set to false for production.
plugins.playground.enabled=true

# [BiometricPlugin] Require authentication every time the app starts or resumes.
biometric.trigger.launch=false
```

### Permissions
Define which groups of permissions to request when the app starts.
```bash
# Comma-separated list of permission groups to request on launch.
# Available groups: LOCATION, NOTIFICATIONS, STORAGE
permissions.on.launch=NOTIFICATIONS,LOCATION
```

### Analytics
Configure your Google Analytics Measurement ID.
```bash
# Your Google Analytics Measurement ID (e.g., G-XXXXXXXXXX).
analytics.gtag.id=G-7XXC1C7CRQ
```