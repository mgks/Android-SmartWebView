---
title: 'Dialog Plugin'
description: 'Showing native Android alert dialogs from JavaScript.'
icon: 'message-square'
---

The `DialogPlugin` provides a generic interface for showing native Android alert dialogs from your web content, ensuring a consistent and platform-native user experience.

---

## How It Works

The plugin injects a JavaScript object (`window.Dialog`) into your WebView. You can call its `show` method with a set of options and a callback function to display a dialog and handle the user's response asynchronously.

---

## Enabling the Plugin

Ensure `DialogPlugin` is listed in the `plugins.enabled` property in `app/src/main/assets/swv.properties`.

```bash
# In swv.properties
plugins.enabled=DialogPlugin,ToastPlugin,...
```

---

## Usage from JavaScript

The `window.Dialog.show()` function is the primary way to interact with this plugin.

### Basic Alert Dialog (One Button)

```javascript
window.Dialog.show({
  title: 'Update Complete',
  message: 'Your profile has been saved successfully.',
  positiveText: 'OK' // 'positiveText' is the only required button text
}, function(result) {
  // Callback receives 'positive' when the button is clicked.
  console.log('Alert dialog was closed.');
});
```

### Confirmation Dialog (Two Buttons)

```javascript
window.Dialog.show({
  title: 'Confirm Deletion',
  message: 'Are you sure you want to delete this item? This action cannot be undone.',
  positiveText: 'Delete',
  negativeText: 'Cancel'
}, function(result) {
  if (result === 'positive') {
    // User clicked 'Delete'
    console.log('Proceeding with deletion...');
  } else {
    // User clicked 'Cancel' or dismissed the dialog
    console.log('Deletion cancelled.');
  }
});
```

### Full Dialog (Three Buttons)

```javascript
window.Dialog.show({
  title: 'Save Changes',
  message: 'You have unsaved changes. What would you like to do?',
  positiveText: 'Save',

  negativeText: 'Discard',
  neutralText: 'Save as Draft'
}, function(result) {
  switch (result) {
    case 'positive':
      // User clicked 'Save'
      break;
    case 'negative':
      // User clicked 'Discard'
      break;
    case 'neutral':
      // User clicked 'Save as Draft'
      break;
    case 'cancel':
      // User dismissed the dialog (e.g., back button)
      break;
  }
});
```

### Available Options

| Key            | Type   | Default | Description                               |
|----------------|--------|---------|-------------------------------------------|
| `title`        | String | "Alert" | The title of the dialog.                    |
| `message`      | String | ""      | The main body text of the dialog.         |
| `positiveText` | String | "OK"    | Text for the positive (confirm) button.   |
| `negativeText` | String | `null`  | Text for the negative (cancel) button.    |
| `neutralText`  | String | `null`  | Text for the neutral (alternative) button.|

### Callback Results

The callback function receives a single string argument indicating how the dialog was closed:
*   `'positive'`
*   `'negative'`
*   `'neutral'`
*   `'cancel'` (if the dialog is dismissed by tapping outside or using the back button)