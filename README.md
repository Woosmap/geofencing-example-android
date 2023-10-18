# Woosmap Geofencing SDK Example for Android

The Woosmap Geofencing SDK is a mobile software development kit focused on gathering efficiently the users’ location, triggering events based on region monitoring, and providing categorized users’ zones of interest from geographical and temporal clusters.

The SDK simplifies the integration of the location context in your mobile application by taking care of lower-level functionalities such as data collection or battery management.

## Documentation

All feature descriptions and guides to implement the Woosmap Geofencing Android SDK are available on the [Woosmap developers documentation](https://developers.woosmap.com/products/geofencing-sdk/get-started/).

## Getting Started

### Setup Your Account

When you [sign up](https://www.woosmap.com/en/sign_up?utm_campaign=Woosmap+Sign-up&utm_source=Developers-documentation) for a Woosmap account, you’ll enter your login/password and an email address, and we’ll send you an activation email.

In the activation email, click on the link to activate your account. Once you activate the account login to [Woosmap Console](https://console.woosmap.com/) and follow the steps below.

* [Create An Organization](https://developers.woosmap.com/get-started/#create-an-organization)
* [Create A Project And API Keys](https://developers.woosmap.com/get-started/#create-a-project-and-api-keys)
* [Register a Woosmap Private API key](https://developers.woosmap.com/support/api-keys/#registering-a-woosmap-private-api-key)

## Example

### Load assets in the Woosmap platform and enable Store Search API

In this repository, a sample code is provided for testing quickly the Geofencing Android SDK. Once this code built, a sample app allows you to monitor Point of Interest (previously loaded in the Woosmap Platform). Before runing the example, each POI has to be created as an asset in the Woosmap Console and the Store Search API must be enabled:

**Create an asset for each POI you want to monitor with a geofence**
<img width="800" alt="image" src="https://github.com/Woosmap/geofencing-example-android/assets/79836861/c907d76b-b5e0-449b-8ac6-7a24d69dd6c3">

**Enable Woosmap Store Search API**
<img width="800" alt="image" src="https://github.com/Woosmap/geofencing-example-android/assets/79836861/4f143c5f-d72f-4eb5-a828-7fe661d74f38">

### Run the sample app

To run the example, first clone this repository and replace the private key in `res/strings.xml` with your own private key. Make sure you have secured your private key.

The sample application has four components. 

* List of locations obtained from `LocationReadyCallback` callback of `LocationReadyListener`.

* List of events obtained using `RegionLogReadyCallback` callback of `RegionLogReadyListener`.

* A floating action button that starts and stops tracking.

* A floating action button that clears all the data from local SQLite DB.


<img src="https://github.com/Woosmap/geofencing-example-android/assets/1289966/e1f4c498-3088-4360-b5ef-c5eb2ae18850"  width="300">

<img src="https://github.com/Woosmap/geofencing-example-android/assets/1289966/0326f979-9b35-45db-a50f-9dd97a418188"  width="300">

<img src="https://github.com/Woosmap/geofencing-example-android/assets/1289966/9d52d21e-7b12-4874-a999-ce92a5fe3af3"  width="300">


### Permissions

Sample app also shows how to request `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION` permissions. For optimal experience, it is also desired that the app has `ACCESS_BACKGROUND_LOCATION` permission granted. 

If you wish to [track BLE beacons](https://developers.woosmap.com/products/geofencing-sdk/android-sdk/guides/monitor-beacons/) then it is advised that the Bluetooth permissions are granted as well. 

Since the sample app posts notifications when user location transitions between Geofence Regions, it is advised that the app should also request `POST_NOTIFICATIONS` permissions (Required only on Android 13 and above).

### Fetching locations

In the sample app, a list of locations is populated using `LocationReadyCallback`. Whenever the SDK reports a new location, it is appended to the list. However, these locations are also stored in a local SQLite database. To fetch these locations you can use the following code.

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

Sample app depends on `LocationReadyCallback` of `RegionLogReadyListener` interface to determine the entry and exit events of the Geofence regions. When user location  transitions inside a Geofence region, `LocationReadyCallback` is invoked along with an object of `RegionLog` which helps to determine if the region was entered or exited. The same callback is invoked when the user location moves outside a Geofence region. These transition logs (`RegionLogs`) are also stored in the local SQLite database. To fetch these `RegionLogs` use the following,

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
