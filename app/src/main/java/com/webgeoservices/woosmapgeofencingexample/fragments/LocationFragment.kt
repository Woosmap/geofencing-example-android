package com.webgeoservices.woosmapgeofencingexample.fragments

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webgeoservices.woosmapgeofencingexample.R
import com.webgeoservices.woosmapgeofencingexample.adapters.LiveLocationAdapter


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
        liveLocationDataAdapter = LiveLocationAdapter(liveLocations)
        locationsList?.adapter = liveLocationDataAdapter
        return view
    }

    fun addLocation(location: Location){
        if (locationsList == null) {
            return
        }
        liveLocationDataAdapter.addLocation(location)
        locationsList.smoothScrollToPosition(0)
    }
}