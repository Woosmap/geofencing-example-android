package com.webgeoservices.woosmapgeofencingexample.adapters

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webgeoservices.woosmapgeofencingexample.R
import java.text.SimpleDateFormat
import java.util.Locale

/***
 * Data adapter class to populate locations RecyclerView
 */
class LiveLocationAdapter(private val locations: ArrayList<Location>): RecyclerView.Adapter<LiveLocationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coordinateTextView: TextView = itemView.findViewById(R.id.coordinate)
        val dateTextView: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.location_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]
        val displayDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        location?.let {
            holder.coordinateTextView.text = "${location.latitude}, ${location.longitude}"
            holder.dateTextView.text = displayDateFormat.format(location.time)
        }
    }

    fun addLocation(location: Location) {
        locations.add(0, location)
        notifyItemInserted(0) // Notify adapter that an item was inserted at the last position
    }

}