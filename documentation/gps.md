# GPS Live Location Tracking

Here's how you can use SWV to get current or live location of your users, useful for geo mapping and location based services.

## Files in use

**GPSTrack**
```
Updates User location on every 1 metre distance and 5 seconds
```

## Getting GPS location
* Enable `ASWP_LOCATION` for updates
* Look for `lat` and `long` cookies set for `ASWV_URL`

## For Offline Files
Make a url (hyperlink) request starting with `offloc:refresh`, that reloads the page with additional query `?loc=lattitude,longitude` that can be broken with javascript to get lattitude and longitude separately in an array.
