package com.webgeoservices.woosmapgeofencingexample.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.webgeoservices.woosmapgeofencingcore.database.RegionLog
import com.webgeoservices.woosmapgeofencingcore.database.WoosmapDb
import com.webgeoservices.woosmapgeofencingexample.R
import com.webgeoservices.woosmapgeofencingexample.models.EventDataModel
import java.text.SimpleDateFormat
import java.util.Locale

class EventsDataAdapter(context: Context, events: ArrayList<EventDataModel>): ArrayAdapter<EventDataModel>(context, 0, events) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val event = getItem(position)
        val displayDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_item, null)
        }
        val eventNameTextView: TextView = view!!.findViewById(R.id.event_name)
        val dateTextView: TextView = view!!.findViewById(R.id.date)
        val poiNameTextView: TextView = view!!.findViewById(R.id.poi_name)
        val radiusTextView: TextView = view!!.findViewById(R.id.radius_poi_id)
        event?.let {
            eventNameTextView.text = event.eventName
            dateTextView.text = displayDateFormat.format(event.regionLog.dateTime)
            poiNameTextView.text = event.poi.name.trim()
            radiusTextView.text= "Radius is ${event.regionLog.radius} and POI id is ${event.regionLog.idStore}"
        }
        return view!!
    }
}