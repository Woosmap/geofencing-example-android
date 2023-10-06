package com.webgeoservices.woosmapgeofencingexample.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.webgeoservices.woosmapgeofencingcore.database.MovingPosition
import com.webgeoservices.woosmapgeofencingexample.R
import com.webgeoservices.woosmapgeofencingexample.adapters.LocationDataAdapter
import java.util.Collections


class LocationFragment: Fragment() {
    private var locationsList: ListView? = null
    private var mContext: Context? = null
    private lateinit var adapter: LocationDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = context;
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.location_list_fragment, container, false)
        locationsList = view.findViewById<View>(R.id.location_list) as ListView
        return view
    }

    fun loadData(arrayOfPlaceData: ArrayList<MovingPosition>) {
        adapter = LocationDataAdapter(mContext!!, arrayOfPlaceData)
        ///Collections.sort(arrayOfPlaceData, PlaceDataComparator())
        locationsList?.adapter ?: adapter
    }

    fun clearData() {
        adapter.clear()
    }
}