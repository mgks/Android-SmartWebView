---
title: 'Location Access Plugin'
description: 'Accessing device GPS coordinates and sending them to your web app.'
icon: 'map-pin'
---

Smart WebView's `LocationPlugin` provides a modern, secure, and battery-efficient way to access the device's location from your web application.

---

## Enabling Location Services

1.  **Enable in Configuration:** In `swv.properties`, ensure the `LOCATION` permission group is requested on launch.
    ```bash
    # swv.properties
    permissions.on.launch=NOTIFICATIONS,LOCATION
    ```
2.  **Enable the Plugin:** Make sure `LocationPlugin` is in the `plugins.enabled` list.
    ```bash
    # swv.properties
    plugins.enabled=LocationPlugin,ToastPlugin,...
    ```

---

## Permissions

The app declares and requests `ACCESS_FINE_LOCATION`. The user must grant this permission at runtime for the feature to work.

---

## How it Works

The `LocationPlugin` provides a JavaScript interface that your web code can call on demand. This is more efficient than constantly tracking the user's location.

1.  **JavaScript Call:** Your web app calls `window.SWVLocation.getCurrentPosition()`, passing a callback function.
2.  **Native Request:** The plugin receives the request and asks the Android system for the current location.
3.  **Callback Execution:** Once the location is retrieved (or if an error occurs), the plugin executes your JavaScript callback, passing the latitude, longitude, and any error message as arguments.

**Accessing Coordinates in JavaScript:**

This is the recommended way to get location data.

```javascript
// Check if the location feature is available
if (window.SWVLocation) {
  
  // Request the current position
  window.SWVLocation.getCurrentPosition(function(lat, lng, error) {
    if (error) {
      console.error("Location Error:", error);
      // e.g., display an error message to the user
      return;
    }

    console.log(`Latitude: ${lat}, Longitude: ${lng}`);
    // Use the coordinates in your web app
    // e.g., show a marker on a map
  });

} else {
  console.log('Location feature not available.');
}
```

::: callout warning
The JavaScript object is `window.SWVLocation`, not `window.Location`. This is to avoid a critical conflict with the browser's built-in `window.location` object.
:::