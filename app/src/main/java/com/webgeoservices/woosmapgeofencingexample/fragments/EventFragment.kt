package com.webgeoservices.woosmapgeofencingexample.fragments

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webgeoservices.woosmapgeofencingcore.database.POI
import com.webgeoservices.woosmapgeofencingcore.database.RegionLog
import com.webgeoservices.woosmapgeofencingexample.R
import com.webgeoservices.woosmapgeofencingexample.adapters.EventsDataAdapter
import com.webgeoservices.woosmapgeofencingexample.models.EventDataModel

/***
 * The fragment which populates the event list obtained from Woosmap SDK
 */
class EventFragment: Fragment() {
    private lateinit var eventList: RecyclerView
    private var eventsDataAdapter: EventsDataAdapter? = null
    private val events: ArrayList<EventDataModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.event_list_fragment, container, false)
        eventList = view.findViewById(R.id.event_list)
        eventList.layoutManager = LinearLayoutManager(context)
        eventsDataAdapter = EventsDataAdapter(events)
        eventList.adapter = eventsDataAdapter
        return view
    }

    fun addEvent(eventData: EventDataModel){
        if (eventList == null) {
            return
        }
        activity?.runOnUiThread{
            eventsDataAdapter?.addEvent(eventData)
            eventList?.smoothScrollToPosition(0)
        }
    }

    fun clearList(){
        eventsDataAdapter?.clearData()
    }
}