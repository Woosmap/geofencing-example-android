package com.webgeoservices.woosmapgeofencingexample.fragments

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.webgeoservices.woosmapgeofencingexample.R
import com.webgeoservices.woosmapgeofencingexample.adapters.LiveLocationAdapter


class LocationFragment: Fragment() {
    private lateinit var locationsListView: ListView
    private val liveLocations: ArrayList<Location> = ArrayList()
    private lateinit var liveLocationDataAdapter: LiveLocationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.location_list_fragment, container, false)
        locationsListView = view.findViewById(R.id.location_list)
        liveLocationDataAdapter = LiveLocationAdapter(requireContext(), liveLocations)
        locationsListView?.adapter = liveLocationDataAdapter
        return view
    }

    fun addLocation(location: Location){
        if (locationsListView == null) {
            return
        }
        liveLocationDataAdapter.insert(location, 0)
        liveLocationDataAdapter.notifyDataSetChanged()
        locationsListView.smoothScrollToPosition(0)
    }
}