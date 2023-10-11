# Woosmap Geofencing SDK Example for Android

The Woosmap Geofencing SDK is a mobile software development kit focused on gathering efficiently the users’ location, triggering events based on region monitoring, and providing categorized users’ zone of interest from geographical and temporal clusters.

The SDK simplifies the integration of the location context in your mobile application by taking care of lower-level functionalities such as data collection or battery management.

## Docuementation

All feature descriptions and guides to implement the Woosmap Geofencing Android SDK are available on the [Woosmap developers documentation](https://developers.woosmap.com/products/geofencing-sdk/get-started/).

## Getting Started

### Setup Your Account

When you [sign up](https://www.woosmap.com/en/sign_up?utm_campaign=Woosmap+Sign-up&utm_source=Developers-documentation) for a Woosmap account, you’ll enter your login/password and an email address, and we’ll send you an activation email.

In the activation email, click on the link to activate your account. Once you activate the account log in to [Woosmap Console](https://console.woosmap.com/) and follow the steps below.

* [Create An Organization](https://developers.woosmap.com/get-started/#create-an-organization)
* [Create A Project And API Keys](https://developers.woosmap.com/get-started/#create-a-project-and-api-keys)
* [Register a Woosmap Private API key](https://developers.woosmap.com/support/api-keys/#registering-a-woosmap-private-api-key)
* [Create Assets in Woosmap Console](#)

## Example

To run the example, first clone this repository and replace the private key in `res/strings.xml` with your own private key. Make sure you have secured your private key.

Sample application has three components. 

* List of locations obtained from `LocationReadyCallback` callback of `LocationReadyListener`.

* List of events obtained using `RegionLogReadyCallback` callback of `RegionLogReadyListener`.

* A floating action button which starts and stops tracking.


<img src="https://github.com/strider1981/geofencing-example-android/assets/1289966/cacddeac-d0de-4d62-90b4-4037832438c4"  width="300">

<img src="https://github.com/strider1981/geofencing-example-android/assets/1289966/3f45ca04-30ec-4aff-8cd0-e0429dc236cc"  width="300">

<img src="https://github.com/strider1981/geofencing-example-android/assets/1289966/b78bfe29-d3da-4a1f-974d-9273a148d838"  width="300">


### Permissions

Sample app also shows how to request `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION` permissions. For optimal experience it also desired that app has `ACCESS_BACKGROUND_LOCATION` permission granted. 

If you wish to [track BLE beacons](https://developers.woosmap.com/products/geofencing-sdk/android-sdk/guides/monitor-beacons/) then it is advised that the Bluetooth permissions are granted as well. 

Since the sample app posts notfications when user location transitions between Geofence Regions, it is advised that the app should also request `POST_NOTIFICATIONS` permissions (Required only on Android 13 and above).

### Fetching locations

In the sample app list of location is populated using `LocationReadyCallback`. Whenever a new location is reported by the SDK, it is appened to the list. However these locations are also stored in a local SQLIte database. To fetch these locations you can use following code.

```kotlin
// Get all positions
val movingPositions = WoosmapDb.getInstance(
            applicationContext
        ).movingPositionsDao.getMovingPositions(-1)


// Observe for only newly added locations
val movingPositionList = WoosmapDb.getInstance(applicationContext).movingPositionsDao.getLiveDataMovingPositions(-1)

movingPositionList.observe(this) { movingPositions ->
    Log.d(
        "MyApplication",
        "Newly obtained location length is ${movingPositions.size} "
    )
}
```

### Fetching region logs

Sample app depends on `LocationReadyCallback` of `RegionLogReadyListener` interface to determine the entry and exit events of the Geofence regions. When user location  transitions inside a Geofence regions, `LocationReadyCallback` is invoked along with an object of `RegionLog` which helps to determine if the region was entered or exited. Same callback is invoked when user location moves outside a Geofence region. These transition logs (`RegionLogs`) are also stored in local SQL database. To fetch these `RegionLogs` use following,

```kotlin
// Get all region logs
val regionLogs = WoosmapDb.getInstance(applicationContext).regionLogsDAO.allRegionLogs

// Observe newly added region logs
val liveRegionLogs = WoosmapDb.getInstance(applicationContext).regionLogsDAO.allLiveRegionLogs

liveRegionLogs.observe(
    this
) { regionLogs ->
    Log.d(
        "MyApplication",
        "Newly added region log length is ${regionLogs.size}"
    )
}
```
