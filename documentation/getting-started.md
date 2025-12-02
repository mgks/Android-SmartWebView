---
title: 'Getting Started'
description: 'Setting up your Smart WebView project in minutes.'
icon: 'rocket'
---

Follow these steps to get your Smart WebView project up and running.

## Prerequisites

*   **Android Studio:** The official IDE for Android development. Download from the [Android Developers site](https://developer.android.com/studio).
*   **Android SDK:** Minimum API Level 24 (Android 7.0 Nougat) or higher installed via the Android Studio SDK Manager.

## Step 1: Download and Open

1.  **(Recommended)** Download the latest source code (`.zip` or `.tar.gz`) from the [GitHub Releases](https://github.com/mgks/Android-SmartWebView/releases) page.
2.  Unzip the project and open the folder in Android Studio.
    *   Alternatively, clone the repository: `git clone https://github.com/mgks/Android-SmartWebView.git`

## Step 2: Configure Your App

All major configuration is now done in a single properties file.

1.  In the Android Studio project view, navigate to `app/src/main/assets/`.
2.  Open the `swv.properties` file.
3.  Edit the values, especially `app.url`, to match your project's requirements.

::: callout tip
See the **[Configuration Guide](/smart-webview/configuration)** for a detailed explanation of all available options in `swv.properties`.
:::

## Step 3: Add Firebase Configuration (Optional)

If you plan to use Firebase Cloud Messaging (Push Notifications), you need your project's `google-services.json` file.

1.  Go to your [Firebase Console](https://console.firebase.google.com/) and follow the steps to add an Android app.
2.  Download the `google-services.json` file.
3.  Place this file directly into the `app/` directory of your Smart WebView project.
    ```bash
    Android-SmartWebView/
    ├── app/
    │   ├── google-services.json  <-- Place it here
    │   ├── src/
    │   └── ...
    └── ...
    ```

## Step 4: Build and Run

1.  Allow Gradle to sync and download all dependencies. This may take a few moments.
2.  Click the `Run 'app'` button (the green play icon) to build and launch the app on an emulator or a connected device.

Your Smart WebView app should now launch! If you encounter issues, double-check that `swv.properties` is configured and that the build process completed without errors.