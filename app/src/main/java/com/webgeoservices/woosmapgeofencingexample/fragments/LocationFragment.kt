package com.webgeoservices.woosmapgeofencingexample.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.webgeoservices.woosmapgeofencingexample.R

class LocationFragment: Fragment() {
    private var locationsList: RecyclerView? = null
    private var mContext: Context? = null

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
        locationsList = view.findViewById<View>(R.id.location_list) as RecyclerView
        return view
    }
}