package com.webgeoservices.woosmapgeofencingexample

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.webgeoservices.woosmapgeofencing.Woosmap
import com.webgeoservices.woosmapgeofencing.WoosmapSettings
import com.webgeoservices.woosmapgeofencingcore.database.POI
import com.webgeoservices.woosmapgeofencingcore.database.WoosmapDb
import com.webgeoservices.woosmapgeofencingexample.adapters.ViewPagerAdapter
import com.webgeoservices.woosmapgeofencingexample.models.EventDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val REQUEST_FINE_LOCATION = 1
    private val REQUEST_BACKGROUND_LOCATION = 2
    private val REQUEST_BLUETOOTH = 3
    private val REQUEST_NOTIFICATION = 4

    private val NOTIFICATION_CHANNEL_ID = "woosmap-notification-id"
    private val NOTIFICATION_CHANNEL_NAME = "Woosmap Example notifications"

    private lateinit var woosmap:Woosmap
    private lateinit var viewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var trackingStarted = false
    private var fabMenuExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeActivityComponents()
        initializeWoosmap()
    }

    override fun onStart() {
        super.onStart()
        /// Check and request location permissions
        requestLocationPermissions()
    }

    override fun onPause() {
        super.onPause()
        // It is advised to check location permissions before you pause or resume
        // Woosmap tracking
        if (checkLocationPermissions()) {
            woosmap.onPause()
        }
    }

    private fun addCustomGeofence(){
        ///Add your custom geofence here.
        woosmap.addGeofence("Dhruv", LatLng(19.211846595823815, 72.86474449182067), 50.0f)
        woosmap.addGeofence("Aster", LatLng(19.21330314920925, 72.87658588088794), 50.0f)
        woosmap.addGeofence("Plaza", LatLng(19.210231548214246, 72.86601191545428), 50.0f)
    }

    override fun onResume() {
        super.onResume()
        // It is advised to check location permissions before you pause or resume
        // Woosmap tracking
        if (checkLocationPermissions()) {
            woosmap.onResume()
        }
    }

    override fun onDestroy() {
        // Make sure you destroy the Woosmap instance
        woosmap.onDestroy()
        super.onDestroy()
    };

    /***
     *  Initializes the ViewPager and FloatingActionButton on the activity.
     *  Sets event handlers of these components.
     */
    private fun initializeActivityComponents(){

        // Initialize the ViewPager and bottom navigation view and handle their events
        viewPager = findViewById(R.id.view_pager)

        // ViewPager's adapter which will have two fragments. One with location list and other with event list.
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        viewPager.adapter = viewPagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottomNavigationView.menu.findItem(R.id.navigation_location).isChecked = true
                    1 -> bottomNavigationView.menu.findItem(R.id.navigation_events).isChecked = true
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}
        })
        bottomNavigationView.setOnItemSelectedListener{item ->
            when(item.itemId){
                R.id.navigation_location -> viewPager.currentItem = 0
                R.id.navigation_events -> viewPager.currentItem = 1
            }
            true
        }

        // Initialize floating action button. Which will start and stop Woosmap tracking (toggle)
        val toggleTackingBtn = findViewById<FloatingActionButton>(R.id.toggle_woosmap_tracking)
        toggleTackingBtn.setOnClickListener { view ->
            trackingStarted = !trackingStarted
            if (trackingStarted){
                view.backgroundTintList = resources.getColorStateList(R.color.colorPrimary, applicationContext.theme)
                addCustomGeofence()
                woosmap.startTracking(Woosmap.ConfigurationProfile.passiveTracking)
                Toast.makeText(applicationContext, "Tracking started", Toast.LENGTH_SHORT).show()
            }
            else{
                view.backgroundTintList = resources.getColorStateList(R.color.colorAccent, applicationContext.theme)
                woosmap.stopTracking()
                Toast.makeText(applicationContext, "Tracking stopped", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize floating action button. Which will clear all the entries in the Woosmap database
        val clearDbButton = findViewById<FloatingActionButton>(R.id.clear_db)
        clearDbButton.setOnClickListener{
            // Clear the database tables and remove geofence regions
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    WoosmapDb.getInstance(applicationContext).clearAllTables()
                    Woosmap.getInstance().removeGeofence()
                }
                //Clear the data in the lists
                viewPagerAdapter.clearLists()
                Toast.makeText(applicationContext, "Clearing all the database values", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize floating action button. Which will clear all the entries in the Woosmap database
        val fabMenuButton = findViewById<FloatingActionButton>(R.id.fab_menu)
        fabMenuButton.setOnClickListener{
            if (!fabMenuExpanded){
                clearDbButton.animate().translationY(-400f)
                toggleTackingBtn.animate().translationY(-200f)
            }else{
                clearDbButton.animate().translationY(0f)
                toggleTackingBtn.animate().translationY(0f)
            }
            fabMenuExpanded = !fabMenuExpanded
        }
    }

    /***
     * Initializes Woosmap Geofencing SDK
     */
    private fun initializeWoosmap(){
        //Initialize Woosmap
        woosmap = Woosmap.getInstance().initializeWoosmap(applicationContext)

        // Set the private key
        WoosmapSettings.privateKeyWoosmapAPI = getString(R.string.woosmap_private_key)

        // Set location ready listener
        woosmap.setLocationReadyListener { location ->
            // This callback is invoked whenever a new location is obtained by the SDK
            // along with the new `location` object

            // Pass the new location to location fragment which will add the new location in the list.
            viewPagerAdapter.locationFragment.addLocation(location)
        }

        // Set regionlog ready listener
        woosmap.setRegionLogReadyListener { regionLog ->
            // This callback is invoked whenever a region is changed (Enter/Exited)
            // along with the transition detail in `regionLog` object

            // Fetch the related POI using `idStore` property from SDK's local database
            // and create a new `EventDataModel` object to populate event list with the new event
            var poi:POI
            if (regionLog.idStore!=null && !regionLog.idStore.equals("")){
                poi = WoosmapDb.getInstance(applicationContext).poIsDAO.getPOIbyStoreId(regionLog.idStore)
            }
            else{
                poi = POI()
                poi.name = "Custom Region - ${regionLog.identifier}"
            }
            val event = EventDataModel()
            event.eventName = regionLog.eventName
            event.regionLog = regionLog
            event.poi = poi

            // Pass the new event to event fragment which will add the new event in the list.
            viewPagerAdapter.eventFragment.addEvent(event)

            // Show notification of the event
            showRegionNotification(event)
        }


        // We will show a notification whenever user enters or exits a region.
        // For android version >= 8 you first have to create a notification channel to show the notifications.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            woosmap.createWoosmapNotifChannel()
        }
    }

    /***
     * Creates a notification channel to display Region entry/exit notifications.
     * Applicable if your app runs on Android 8 or above.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // Create a Notification channel
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Woosmap Geofencing Example notifications"
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.enableLights(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

        // Register the channel with the system.
        notificationManager.createNotificationChannel(channel)
    }

    /***
     * Shows a notification whenever a region entry/exit event occurs
     */
    private fun showRegionNotification(event: EventDataModel){

        val notification: Notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(event.eventName)
            .setTicker("POI Name: ${event.poi.name}")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentText("${event.poi.name}\nRadius is ${event.regionLog.radius} and POI id is ${event.regionLog.idStore+""}")
            .setOngoing(false)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(101, notification)
    }

    /***
     * Checks if `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` permissions are granted to the application.
     */
    private fun checkLocationPermissions(): Boolean {
        val finePermissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarsePermissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return finePermissionState == PackageManager.PERMISSION_GRANTED || coarsePermissionState == PackageManager.PERMISSION_GRANTED
    }

    /***
     * Requests `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION` permissions
     */
    private fun requestLocationPermissions(){
        // Check if ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION permissions are granted
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { // Not granted. So request them.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION
            )
        } else { // Granted. We now need to request ACCESS_BACKGROUND_LOCATION permission
            requestBackgroundLocationPermission()
        }
    }

    /***
     * Requests `ACCESS_BACKGROUND_LOCATION` permission
     */
    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && // Check for this permission only if you are running on Android Q and above
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { // Permission not granted. So request it.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQUEST_BACKGROUND_LOCATION
            )
        } else { // Permission granted. Not we need to ask the permission to access Bluetooth.
            requestBluetoothPermissions()
        }
    }

    /***
     * Requests BLUETOOTH, BLUETOOTH_CONNECT and BLUETOOTH_SCAN permissions.
     * This is an optional step. These permissions are required only
     * if you are using Woosmap SDK tracking with `beaconTracking` profile.
     */
    private fun requestBluetoothPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // BLUETOOTH_CONNECT and BLUETOOTH_SCAN are applicable only Android S and above
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                    REQUEST_BLUETOOTH
                )
            }
            else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH),
                    REQUEST_BLUETOOTH
                )
            }
        } else {
            // Bluetooth permissions are granted. We not need to ask permission to POST_NOTIFICATIONS
            // This is applicable only if the app is running on Android TIRAMISU and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        REQUEST_NOTIFICATION
                    )
                }
            } else {
                // All permissions are granted, proceed with your logic here
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Access to fine location is granted. Now request background location access.
                    requestBackgroundLocationPermission()
                } else {
                    Toast.makeText(this, "Fine location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_BACKGROUND_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Access to background location is granted. Now request Bluetooth access.
                    requestBluetoothPermissions()
                } else {
                    Toast.makeText(this, "Background location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_BLUETOOTH -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}