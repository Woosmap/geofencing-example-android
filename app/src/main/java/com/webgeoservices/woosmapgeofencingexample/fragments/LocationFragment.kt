package com.webgeoservices.woosmapgeofencingexample.fragments

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webgeoservices.woosmapgeofencingcore.database.WoosmapDb
import com.webgeoservices.woosmapgeofencingexample.R
import com.webgeoservices.woosmapgeofencingexample.adapters.LiveLocationAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/***
 * The fragment which populates the location list obtained from Woosmap SDK
 */
class LocationFragment: Fragment() {
    private lateinit var locationsList: RecyclerView
    private val liveLocations: ArrayList<Location> = ArrayList()
    private lateinit var liveLocationDataAdapter: LiveLocationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.location_list_fragment, container, false)
        locationsList = view.findViewById(R.id.location_list)
        locationsList.layoutManager = LinearLayoutManager(context)
        fetchLocationsFromDB()
        return view
    }

    /***
     * Adds a new item in the list when `onLocationReady` callback is invoked
     */
    fun addLocation(location: Location){
        if (locationsList == null) {
            return
        }
        liveLocationDataAdapter.addLocation(location)
        locationsList.smoothScrollToPosition(0)
    }

    fun clearList(){
        liveLocationDataAdapter.clearData()
    }

    /***
     * Populates the list with the previously reported locations.
     */
    private fun fetchLocationsFromDB(){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                // Get all positions from the DB
                val movingPositions = WoosmapDb.getInstance(context).movingPositionsDao.getMovingPositions(-1)
                var location: Location

                // Sort them by date in descending order since we need to show the locations in descending order
                val sortedPositions = movingPositions.sortedWith(compareByDescending { it.dateTime })

                // Convert from `MovingPosition` to `Location` object
                for (movingPosition in sortedPositions){
                    location = Location("WGS")
                    location.time = movingPosition.dateTime
                    location.latitude = movingPosition.lat
                    location.longitude = movingPosition.lng
                    location.accuracy = movingPosition.accuracy
                    liveLocations.add(location)
                }
            }
            // Initialize the list adapter
            liveLocationDataAdapter = LiveLocationAdapter(liveLocations)
            locationsList?.adapter = liveLocationDataAdapter
        }
    }

}