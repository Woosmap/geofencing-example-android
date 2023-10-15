package com.webgeoservices.woosmapgeofencingexample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webgeoservices.woosmapgeofencingcore.database.POI
import com.webgeoservices.woosmapgeofencingcore.database.WoosmapDb
import com.webgeoservices.woosmapgeofencingexample.R
import com.webgeoservices.woosmapgeofencingexample.adapters.EventsDataAdapter
import com.webgeoservices.woosmapgeofencingexample.models.EventDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        fetchRegionLogsFromDB()
        return view
    }

    /**
     * Adds a new item in the list when `onRegionLogReady` callback is invoked
     */
    fun addEvent(eventData: EventDataModel){
        if (eventList == null) {
            return
        }
        activity?.runOnUiThread{
            eventsDataAdapter?.addEvent(eventData)
            eventList?.smoothScrollToPosition(0)
        }
    }

    /***
     * Clears the list
     */
    fun clearList(){
        eventsDataAdapter?.clearData()
    }

    /***
     * Populates the list with the previously reported region logs.
     */
    private fun fetchRegionLogsFromDB(){
        lifecycleScope.launch{
            withContext(Dispatchers.IO){
                var eventData:EventDataModel
                var poi: POI
                // Fetch all the region logs from the DB
                val regionLogs = WoosmapDb.getInstance(context).regionLogsDAO.allRegionLogs

                // Sort them by date in descending order since we need to show them in descending order
                val sortedRegionLogs = regionLogs.sortedWith(compareByDescending { it.dateTime })

                // Loop through each region log in the list to create a custom EventDataModel
                for(regionLog in sortedRegionLogs){
                    eventData = EventDataModel()

                    // Fetch the related POI from region log
                    poi = WoosmapDb.getInstance(context).poIsDAO.getPOIbyStoreId(regionLog.idStore)

                    // Initialize EventDataModel with proper values and add it to the list
                    eventData.eventName = regionLog.eventName
                    eventData.regionLog = regionLog
                    eventData.poi = poi

                    events.add(eventData)
                }
            }
            // Initialize the list adapter
            eventsDataAdapter = EventsDataAdapter(events)
            eventList.adapter = eventsDataAdapter
        }
    }
}