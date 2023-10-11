package com.webgeoservices.woosmapgeofencingexample.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webgeoservices.woosmapgeofencingexample.R
import com.webgeoservices.woosmapgeofencingexample.models.EventDataModel
import java.text.SimpleDateFormat
import java.util.Locale

/***
 * Data adapter class to populate events RecyclerView
 */
class EventsDataAdapter(private val events: ArrayList<EventDataModel>): RecyclerView.Adapter<EventsDataAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventNameTextView: TextView = itemView.findViewById(R.id.event_name)
        val dateTextView: TextView = itemView.findViewById(R.id.date)
        val poiNameTextView: TextView = itemView.findViewById(R.id.poi_name)
        val radiusTextView: TextView = itemView.findViewById(R.id.radius_poi_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return EventsDataAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        val displayDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        event?.let {
            holder.eventNameTextView.text = event.eventName
            holder.dateTextView.text = displayDateFormat.format(event.regionLog.dateTime)
            holder.poiNameTextView.text = event.poi.name.trim()
            holder.radiusTextView.text= "Radius is ${event.regionLog.radius} and POI id is ${event.regionLog.idStore}"
        }
    }

    fun addEvent(event: EventDataModel) {
        events.add(0, event)
        notifyItemInserted(0) // Notify adapter that an item was inserted at the last position
    }

    fun clearData(){
        val size = events.size
        events.clear()
        notifyItemRangeRemoved(0, size)
    }
}