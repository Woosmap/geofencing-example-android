package com.webgeoservices.woosmapgeofencingexample

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.webgeoservices.woosmapgeofencing.Woosmap
import com.webgeoservices.woosmapgeofencing.WoosmapSettings
import com.webgeoservices.woosmapgeofencingcore.WoosmapSettingsCore
import com.webgeoservices.woosmapgeofencingcore.database.MovingPosition
import com.webgeoservices.woosmapgeofencingcore.database.POI
import com.webgeoservices.woosmapgeofencingcore.database.RegionLog
import com.webgeoservices.woosmapgeofencingcore.database.WoosmapDb
import com.webgeoservices.woosmapgeofencingexample.adapters.ViewPagerAdapter
import com.webgeoservices.woosmapgeofencingexample.models.EventDataModel

class MainActivity : AppCompatActivity() {

    private val REQUEST_LOCATION_PERMISSIONS_CODE = 101
    private val REQUEST_BACKGROUND_LOCATION_PERMISSIONS_CODE = 102
    private val REQUEST_BLUETOOTH_PERMISSION_CODE = 103
    private val REQUEST_NOTIFICATIONS_PERMISSION_CODE = 104

    private lateinit var woosmap:Woosmap
    private lateinit var viewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var trackingStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeActivityComponents()
        initializeWoosmap()
    }

    override fun onStart() {
        super.onStart()
        requestLocationPermissions()
    }

    override fun onPause() {
        super.onPause()
        if (checkLocationPermissions()) {
            woosmap.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkLocationPermissions()) {
            woosmap.onResume()
        }
    }

    override fun onDestroy() {
        woosmap.onDestroy()
        super.onDestroy()
    };

    /***
     *
     */
    private fun initializeActivityComponents(){
        viewPager = findViewById(R.id.view_pager)
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

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_location -> viewPager.currentItem = 0
                R.id.navigation_events -> viewPager.currentItem = 1
            }
            true
        }

        //Toggle Woosmap tracking
        val toggleTackingBtn = findViewById<FloatingActionButton>(R.id.toggle_woosmap_tracking)
        toggleTackingBtn.setOnClickListener { view ->
            trackingStarted = !trackingStarted
            if (trackingStarted){
                view.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
                woosmap.startTracking(Woosmap.ConfigurationProfile.passiveTracking)
            }
            else{
                view.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
                woosmap.stopTracking()
            }
        }
    }

    private fun initializeWoosmap(){
        //Initialize Woosmap
        woosmap = Woosmap.getInstance().initializeWoosmap(applicationContext)

        // Set Keys
        WoosmapSettings.privateKeyWoosmapAPI = getString(R.string.woosmap_private_key)

        // Set location ready listener
        woosmap.setLocationReadyListener { location ->
            // Update the location in the list
            viewPagerAdapter.locationFragment.addLocation(location)
        }

        // Set regionlog ready listener
        woosmap.setRegionLogReadyListener { regionLog ->
            val poi = WoosmapDb.getInstance(applicationContext).poIsDAO.getPOIbyStoreId(regionLog.idStore)
            val event = EventDataModel()
            event.eventName = regionLog.eventName
            event.regionLog = regionLog
            event.poi = poi

            // Insert the event in the list
            viewPagerAdapter.eventFragment.addEvent(event)
            // Show notification
            showRegionNotification(event)
        }

        // For android version >= 8 you have to create a channel or use the woosmap's channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            woosmap.createWoosmapNotifChannel()
        }
    }

    /***
     *
     */
    private fun showRegionNotification(event: EventDataModel){

        val notification: Notification = NotificationCompat.Builder(applicationContext, WoosmapSettingsCore.WoosmapNotificationChannelID)
            .setContentTitle(event.eventName)
            .setTicker("POI Name: ${event.poi.name}")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentText("${event.poi.name}\nRadius is ${event.regionLog.radius} and POI id is ${event.regionLog.idStore}")
            .setOngoing(false)
            .build()

        val notifManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notifManager.notify(101, notification)
    }

    /***
     *
     */
    private fun loadEventsData(){
        val regionLogList = WoosmapDb.getInstance(
            applicationContext
        ).regionLogsDAO.allLiveRegionLogs

        regionLogList.observe(
            this
        ) { regionLogs ->
            Log.d("", regionLogs.size.toString() + "")
        }
    }

    /***
     *
     */
    private fun loadLocationData(){
        val movingPositionList = WoosmapDb.getInstance(
            applicationContext
        ).movingPositionsDao.getLiveDataMovingPositions(-1)

        movingPositionList.observe(
            this
        ) { movingPositions ->
            if (movingPositions.isNotEmpty()){
                val arrayList = ArrayList<MovingPosition>(movingPositions.toList())
                viewPagerAdapter.locationFragment.loadData(arrayList)
            }
        }
    }

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

    private fun requestLocationPermissions(){
        ///Request for location permissions
        if (!checkLocationPermissions()){
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_LOCATION_PERMISSIONS_CODE)
        }
        else{
            requestBackgroundLocationPermission()
        }
    }

    private fun requestBackgroundLocationPermission(){
        ///Request for background location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),REQUEST_BACKGROUND_LOCATION_PERMISSIONS_CODE)
            }
            else{
                requestBluetoothPermissions()
            }
        }
    }

    private fun requestBluetoothPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(arrayOf(android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_CONNECT),REQUEST_BLUETOOTH_PERMISSION_CODE)
        }
        else{
            requestPermissions(arrayOf(android.Manifest.permission.BLUETOOTH),REQUEST_BLUETOOTH_PERMISSION_CODE)
        }
    }

    private fun requestNotificationsPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),REQUEST_NOTIFICATIONS_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.isEmpty()){
            return
        }
        ///Check if the FINE_LOCATION permission is granted. If yes only then proceed further with other permissions.
        if (requestCode == REQUEST_LOCATION_PERMISSIONS_CODE){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Location permission not granted. App may not behave as expected", Toast.LENGTH_SHORT).show()
                return
            }

            ///If device OS is >= Q then ask for Background location access
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                requestBackgroundLocationPermission()
            }
            else{ ///Request Bluetooth access
                requestBluetoothPermissions()
            }
        }
        ///Check if the background location access is granted.
        if (requestCode == REQUEST_BACKGROUND_LOCATION_PERMISSIONS_CODE){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Background location permission not granted. App may not behave as expected", Toast.LENGTH_SHORT).show()
            }
            ///Request BLE permission.
            requestBluetoothPermissions()
        }

        if (requestCode == REQUEST_BLUETOOTH_PERMISSION_CODE){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Bluetooth permission not granted. App may not behave as expected", Toast.LENGTH_SHORT).show()
            }
            requestNotificationsPermissions()
        }

        if (requestCode == REQUEST_NOTIFICATIONS_PERMISSION_CODE){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Notification permission not granted. App may not behave as expected", Toast.LENGTH_SHORT).show()
            }
        }
    }
}