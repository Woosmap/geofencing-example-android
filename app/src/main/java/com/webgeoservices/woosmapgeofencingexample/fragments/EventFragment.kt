package com.webgeoservices.woosmapgeofencingexample.fragments

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.webgeoservices.woosmapgeofencingcore.database.RegionLog
import com.webgeoservices.woosmapgeofencingexample.R
import com.webgeoservices.woosmapgeofencingexample.adapters.EventsDataAdapter

class EventFragment: Fragment() {
    private var eventList: ListView? = null
    private var mContext: Context? = null
    private var eventsDataAdapter: EventsDataAdapter? = null
    private val regionLogs: ArrayList<RegionLog> = ArrayList()
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
        eventList = view.findViewById(R.id.event_list) as ListView
        eventsDataAdapter = EventsDataAdapter(requireContext(), regionLogs)
        eventList?.adapter = eventsDataAdapter
        return view
    }

    fun addRegionLog(regionLog: RegionLog){
        if (eventList == null) {
            return
        }
        activity?.runOnUiThread{
            eventsDataAdapter?.insert(regionLog, 0)
            eventsDataAdapter?.notifyDataSetChanged()
            eventList?.smoothScrollToPosition(0)
        }
    }
}