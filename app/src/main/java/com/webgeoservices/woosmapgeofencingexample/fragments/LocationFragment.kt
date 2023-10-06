package com.webgeoservices.woosmapgeofencingexample.fragments

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.webgeoservices.woosmapgeofencingcore.database.MovingPosition
import com.webgeoservices.woosmapgeofencingexample.R
import com.webgeoservices.woosmapgeofencingexample.adapters.LiveLocationAdapter
import com.webgeoservices.woosmapgeofencingexample.adapters.LocationDataAdapter


class LocationFragment: Fragment() {
    private lateinit var locationsListView: ListView
    private val movingPositions: ArrayList<MovingPosition> = ArrayList()
    private val liveLocations: ArrayList<Location> = ArrayList()
    private lateinit var dataAdapter: LocationDataAdapter
    private lateinit var liveLocationDataAdapter: LiveLocationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.location_list_fragment, container, false)
        locationsListView = view.findViewById(R.id.location_list)
//        dataAdapter = LocationDataAdapter(requireContext(), movingPositions)
//        locationsListView?.adapter = dataAdapter
        liveLocationDataAdapter = LiveLocationAdapter(requireContext(), liveLocations)
        locationsListView?.adapter = liveLocationDataAdapter
        return view
    }

    fun loadData(movingPositions: ArrayList<MovingPosition>) {
        if (locationsListView == null) {
            return
        }
        dataAdapter.clear()
        dataAdapter.addAll(movingPositions)
        dataAdapter.notifyDataSetChanged()
    }

    fun addLocation(location: Location){
        if (locationsListView == null) {
            return
        }
        liveLocationDataAdapter.insert(location, 0)
        liveLocationDataAdapter.notifyDataSetChanged()
        locationsListView.smoothScrollToPosition(0)
    }

    fun clearData() {
        dataAdapter.clear()
    }
}