package com.webgeoservices.woosmapgeofencingexample.models

import com.webgeoservices.woosmapgeofencingcore.database.POI
import com.webgeoservices.woosmapgeofencingcore.database.RegionLog

/***
 * The data class which contains the RegionLog and POI object obtained from Woosmap SDK.
 */
class EventDataModel {
    lateinit var eventName:String
    lateinit var regionLog: RegionLog
    lateinit var poi: POI
}