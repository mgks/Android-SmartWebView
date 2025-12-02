---
title: 'Rating System Plugin'
description: 'Prompting users to rate your app on the Google Play store.'
icon: 'star'
---

The `RatingPlugin` prompts users to rate your application on the Google Play Store after certain usage conditions are met.

---

## How It Works

This plugin is self-activating. Once enabled, it automatically tracks:
*   The number of times the app has been launched.
*   The number of days that have passed since the app was first installed.

When the configured thresholds are met, the plugin displays a standard Android dialog asking the user to rate the app.

---

## Configuration

This plugin's behavior is controlled by properties in `app/src/main/assets/swv.properties`.

1.  **Enable the Plugin:** First, ensure `RatingPlugin` is listed in the `plugins.enabled` property.
    ```bash
    # In swv.properties
    plugins.enabled=RatingPlugin,ToastPlugin,...
    ```

2.  **Set Trigger Conditions:** Adjust the following properties to control when the dialog appears.
    ```bash
    # In swv.properties

    # Minimum days to wait after install before showing the dialog.
    rating.install.days=3

    # Minimum number of app launches required before showing.
    rating.launch.times=10

    # If the user selects "Later", days to wait before asking again.
    rating.remind.interval=2
    ```

---

## Dialog Options

The user is presented with a non-intrusive dialog with three choices:
*   **Rate Now:** Opens the app's page on the Google Play Store and permanently dismisses future prompts.
*   **Later:** Dismisses the dialog and waits for the `rating.remind.interval` before potentially showing it again.
*   **No, Thanks:** Permanently dismisses future prompts for the user.

---

## Customizing Dialog Text

You can change the text displayed in the rating dialog by editing the string resources in `app/src/main/res/values/strings.xml`:
*   `rate_dialog_title`
*   `rate_dialog_message`
*   `rate_dialog_ok` (for the "Rate Now" button)
*   `rate_dialog_cancel` (for the "Later" button)
*   `rate_dialog_no` (for the "No, Thanks" button)