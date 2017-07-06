# Location and MVP

Use MVP to solve location and UI-Handling. 

## Different ways to get location

> Play-service after v11.0.1
   
    Great progress is that we don't need Google GoogleApiClient explict to initilize the location-service.
    
    Info:
    1. https://android-developers.googleblog.com/2017/06/reduce-friction-with-new-location-apis.html
    2. https://developer.android.com/training/location/receive-location-updates.html
    3. https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API
    
Checkout branch: feature/playservice-since-11.0.1/new-location-service

> Play-service before v11.0.1

    Old style, don't recommended. Before using service we have to maintain the GoogleApiClient.
     
Checkout branch: feature/playservice-old/location-service

> Easiest way with smart-location-lib
    
    To save time and ignore a lot details of service, you need this 3rd-lib,
    however, before you use you must check whether the library has been updated and
    especially when the provider is Google Play-Service.
    
    Info:
    https://github.com/mrmans0n/smart-location-lib

Checkout branch: feature/3rd/smart-location-lib
    
