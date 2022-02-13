package com.example.zomatoapp.data

import androidx.room.TypeConverter
import org.json.JSONObject

/* Class for conversion of Location class */

class LocationConverters() {

    @TypeConverter
    fun toLocation(string: String): Location {
        val json = JSONObject(string)
        return Location(json.getString("address"),
            json.getString("latitude"),
            json.getString("longitude"))
    }

    @TypeConverter
    fun fromLocation(location: Location): String {
        return JSONObject().apply {
            put("address", location.address)
            put("latitude", location.latitude)
            put("longitude", location.longitude)
        }.toString()
    }
}