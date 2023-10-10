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

    private val REQUEST_FINE_LOCATION = 1
    private val REQUEST_BACKGROUND_LOCATION = 2
    private val REQUEST_BLUETOOTH = 3
    private val REQUEST_NOTIFICATION = 4

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

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(101, notification)
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
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION
            )
        } else {
            checkBackgroundLocationPermission()
        }
    }

    private fun checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQUEST_BACKGROUND_LOCATION
            )
        } else {
            checkBluetoothPermissions()
        }
    }

    private fun checkBluetoothPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.BLUETOOTH,android.Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_BLUETOOTH
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
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
                    checkBackgroundLocationPermission()
                } else {
                    Toast.makeText(this, "Fine location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_BACKGROUND_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkBluetoothPermissions()
                } else {
                    Toast.makeText(this, "Background location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_BLUETOOTH -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Enable BLE
                } else {
                    Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}