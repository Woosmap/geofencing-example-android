package com.webgeoservices.woosmapgeofencingexample.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.webgeoservices.woosmapgeofencingexample.R

class EventFragment: Fragment() {
    private var eventList: RecyclerView? = null
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
        val view: View = inflater.inflate(R.layout.event_list_fragment, container, false)
        eventList = view.findViewById<View>(R.id.event_list) as RecyclerView
        return view
    }
}