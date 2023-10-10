package com.webgeoservices.woosmapgeofencingexample.models

import com.webgeoservices.woosmapgeofencingcore.database.POI
import com.webgeoservices.woosmapgeofencingcore.database.RegionLog

class EventDataModel {
    lateinit var eventName:String
    lateinit var regionLog: RegionLog
    lateinit var poi: POI
}