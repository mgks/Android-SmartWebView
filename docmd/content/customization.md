---
title: 'Customization'
description: 'Tailoring the appearance and resources of your Smart WebView app.'
icon: 'palette'
---

Smart WebView is designed to be easily customizable. You can modify various aspects of the app, from visual styles to text strings and assets.

---

## App Name and Package ID

*   **App Name:** Change the `app_name` string value in `app/src/main/res/values/strings.xml`.
*   **Package ID:** The `applicationId` is now configured directly from `swv.properties`. Change the `build.application.id` property and rebuild. Android Studio will handle the refactoring.

::: callout danger
Changing the Package ID after release complicates app updates on Google Play.
:::

---

## Launcher Icons

Replace icons in `app/src/main/res/mipmap-*` directories. Use Android Studio's "Image Asset Studio" (Right-click `res` > `New` > `Image Asset`) for generating adaptive icons.

---

## UI Appearance

*   **Colors:** Define your palette in `app/src/main/res/values/colors.xml` and `app/src/main/res/values-night/colors.xml`.
*   **Themes:** Modify app themes in `app/src/main/res/values/themes.xml`. Core theme structure is now based on Material 3.
*   **Splash Screen:** The new Android 12+ splash screen is configured in `app/src/main/res/values/themes.xml` under the `Theme.App.Starting` style.

---

## Layouts

Modify the XML layout files in `app/src/main/res/layout/` (e.g., `activity_main.xml`, `drawer_main.xml`) to change the native UI structure.

---

## Navigation Drawer

When using the drawer layout (`ui.layout=1`), you can customize it:

*   **Menu Items:** Define items in `app/src/main/res/menu/activity_main_drawer.xml`.
*   **Header:** Customize the header view in `app/src/main/res/layout/drawer_main_header.xml`.
*   **Item Click Handling:** Modify `onNavigationItemSelected` in `MainActivity.java`.

---

## Splash Screen and Welcome Screen

Customizing the app's startup appearance involves two steps:

**1. The Android 12+ Splash Screen (App Launch)**

This is the very first screen shown by the operating system. Its icon is your app's launcher icon.

*   In Android Studio, right-click the `res` folder → `New` → `Image Asset`.
*   Select "Launcher Icons (Adaptive and Legacy)".
*   In the "Foreground Layer" tab, provide your logo asset.
*   **Crucially, ensure you overwrite the existing `ic_launcher_foreground` asset.** This is the asset the OS uses for the splash screen.

**2. The In-App Welcome Screen (Web Page Loading)**

This screen appears *after* the initial OS splash screen while your web page is loading in the background.

*   The logo for this screen is located at: `app/src/main/res/raw/front_splash.png`.
*   To change it, simply **replace the `front_splash.png` file** with your own logo, keeping the filename the same.

By customizing both `ic_launcher_foreground` (via Image Asset Studio) and `front_splash.png` (by direct replacement), you can fully brand the app's startup experience.

---

## Local Assets

Place files in `app/src/main/assets/web/` to bundle them with your app. Access them in the WebView using the path `file:///android_asset/web/YOUR_FILENAME`. The project includes:
- `error.html`
- `offline.html`
- `script.js`
- `style.css`

The root `assets` directory contains the main `swv.properties` configuration file.

---

## Text Strings

Centralize user-facing text for easy modification and localization in `app/src/main/res/values/strings.xml`. To add translations, create new resource folders like `values-es/strings.xml`.

---

## Android Manifest

The core application configuration file is `app/src/main/AndroidManifest.xml`.

::: callout warning
Modify this file with care. It is used for declaring permissions, registering activities/services, defining intent filters for deep linking, and specifying hardware features.
:::