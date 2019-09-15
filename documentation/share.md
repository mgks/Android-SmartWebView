# Sharing external data with the app

Learn how to share external links and data with your app/server.

## Files in use

**ShareActivity**
```
Handles content received from other apps as intent
```

## Setting up share intent
* Change `ASWV_SHAREURL` as needed
* By deafault `ASWV_SHAREURL` is set to `ASWV_URL/share`
* Complete redirection URL is `ASWV_SHAREURL+"?text="+share+"&link="+urlStr` where `share` is complete intent data received and `urlStr` is a strip from intent data to look for any URLs available.
* If you don't want to use redirection, these variables can also be set as cookies
