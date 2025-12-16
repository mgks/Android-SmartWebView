---
title: 'Firebase Messaging'
description: 'Setting up and using Firebase push notifications.'
icon: 'bell'
---

Smart WebView integrates Firebase Cloud Messaging (FCM) to enable push notifications.

---

## Setup

Refer to the [Getting Started](/Android-SmartWebView/documentation/getting-started#step-2-add-firebase-configuration-important) guide for the initial step of adding the `google-services.json` file to your project. This is mandatory for FCM.

::: card
[Official FCM Android Setup Guide](https://firebase.google.com/docs/cloud-messaging/android/client)
:::

---

## How it Works

*   **Token Generation:** The Firebase SDK automatically generates a unique registration token. The `Firebase.java` service listens for new tokens (`onNewToken`) and stores the latest token in `SmartWebView.fcm_token`. The `Functions.fcm_token()` method attempts to retrieve this and set it as a cookie (`FCM_TOKEN=...`) for your web application to access.
*   **Receiving Messages:**
    *   **Foreground:** `Firebase.java`'s `onMessageReceived` is triggered, and a notification is manually displayed.
    *   **Background/Closed:** The Firebase SDK automatically handles displaying notifications sent with a `notification` payload.
*   **Handling Clicks:** Notifications can include a `data` payload with a `uri` key. When the user taps the notification, the app opens and loads the specified `uri`. If no `uri` is provided, it defaults to the main `ASWV_URL`.

---

## Sending Notifications

Use the Firebase Console or the FCM HTTP v1 API to send notifications.

**Example POST Request (FCM HTTP v1 API):**

```json
// POST https://fcm.googleapis.com/v1/projects/YOUR_PROJECT_ID/messages:send
{
  "message": {
    "token": "DEVICE_REGISTRATION_TOKEN", // <-- Get this from the device
    "notification": {
      "title": "Your Notification Title",
      "body": "This is the main message body."
    },
    "android": {
      "notification": {
        "click_action": "OPEN_URI"
      }
    },
    "data": { // Custom data payload
      "uri": "https://your-website.com/specific-page",
      "nid": "unique_notification_id_123"
    }
  }
}
```

**Headers:**

*   `Content-Type: application/json`
*   `Authorization: Bearer YOUR_OAUTH2_ACCESS_TOKEN`

::: callout tip
The `FCM_TOKEN` cookie can be read by your website's JavaScript to send the token to your server.
:::

---

## Customization

*   **Notification Channel:** Customize the channel ID (`SmartWebView.asw_fcm_channel`) and names/descriptions in `app/src/main/res/values/strings.xml`. This is required for Android 8.0+.
*   **Notification Icon:** Set the icon in `Firebase.java` via `.setSmallIcon()`.
*   **Data Handling:** Modify `onMessageReceived` in `Firebase.java` to process custom `data` payloads for more complex interactions.