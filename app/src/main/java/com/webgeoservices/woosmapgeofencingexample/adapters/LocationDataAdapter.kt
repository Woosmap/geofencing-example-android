package com.webgeoservices.woosmapgeofencingexample.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.webgeoservices.woosmapgeofencingcore.database.MovingPosition
import com.webgeoservices.woosmapgeofencingexample.R
import java.text.SimpleDateFormat
import java.util.Locale

class LocationDataAdapter(context: Context, objects: ArrayList<MovingPosition?>?) :
    ArrayAdapter<MovingPosition>(context, 0, objects!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val movingPosition = getItem(position)
        val displayDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.location_item, parent)
        }
        val coordinateTextView: TextView = convertView!!.findViewById(R.id.coordinate)
        val dateTextView: TextView = convertView.findViewById(R.id.date)
        movingPosition?.let {
            coordinateTextView.text = "${movingPosition.lat}, ${movingPosition.lng}"
            dateTextView.text = displayDateFormat.format(movingPosition.dateTime)
        }
        return view!!
    }
}