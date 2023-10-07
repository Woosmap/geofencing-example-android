package com.webgeoservices.woosmapgeofencingexample.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.webgeoservices.woosmapgeofencingcore.database.RegionLog
import com.webgeoservices.woosmapgeofencingexample.R
import java.text.SimpleDateFormat
import java.util.Locale

class EventsDataAdapter(context: Context, regionLogs: ArrayList<RegionLog>): ArrayAdapter<RegionLog>(context, 0, regionLogs) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val regionLog = getItem(position)
        val displayDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_item, null)
        }
        val eventNameTextView: TextView = view!!.findViewById(R.id.event_name)
        val dateTextView: TextView = view!!.findViewById(R.id.date)
        regionLog?.let {
            eventNameTextView.text = regionLog.eventName
            dateTextView.text = displayDateFormat.format(regionLog.dateTime)
        }
        return view!!
    }
}