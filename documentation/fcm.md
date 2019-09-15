# Firebase Cloud Messaging

Find more about FCM implemented in the project and instructions to set-up firebase for your server.

## Files in use

**FBInstanceIDService**
```
Updates User Token/ID and logs fresh tokens
```

**FBMessagingService**
```
Receives data and builds notifications
```

## Setting up firebase
* Signup for Firebase account
* Add project and fill required information
* Open `Settings > Project settings > Add firebase to your Android app`
* Enter package name you're using currently
* Download config file `google-services.json` and save it to `\app` directory

## Sending notifications from your server
*Create a POST request with headers*
URL `https://fcm.googleapis.com/fcm/send`
content-type: `application/json`
authorization: `key=____your_server_key_here___` (Firebase > Settings > Cloud Messaging > Server key)

*Required JSON Data*
```
{ "notification": {
    "title": "___title_string___",
    "text": "___text_string___",
     "click_action": "Open_URI"
  },
    "data": {
    "uri": "___the_URL_where_you_want_users_to_send__"
    },
  "to" : "___user_token___"
}
```

## How to acquire fresh User Token
SWV creates a cookie `FCM_TOKEN` with latest token eveytime app opened that can be saved to your server.

Token is also saved as Log.d `FCM_TOKEN` for testing.
