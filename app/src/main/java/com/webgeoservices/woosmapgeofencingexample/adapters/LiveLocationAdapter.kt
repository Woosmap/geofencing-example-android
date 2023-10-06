package com.webgeoservices.woosmapgeofencingexample.adapters

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.webgeoservices.woosmapgeofencingexample.R
import java.text.SimpleDateFormat
import java.util.Locale

class LiveLocationAdapter(context: Context, objects: ArrayList<Location>) :
    ArrayAdapter<Location>(context, 0, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val location = getItem(position)
        val displayDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.location_item, null)
        }
        val coordinateTextView: TextView = view!!.findViewById(R.id.coordinate)
        val dateTextView: TextView = view!!.findViewById(R.id.date)
        location?.let {
            coordinateTextView.text = "${location.latitude}, ${location.longitude}"
            dateTextView.text = displayDateFormat.format(location.time)
        }
        return view!!
    }
}