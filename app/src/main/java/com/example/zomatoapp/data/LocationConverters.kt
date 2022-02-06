package com.example.zomatoapp.data

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class LocationConverters() {
    @TypeConverter
    fun toLocation(string: String): Location {
        val json = JSONObject(string)
        return Location(json.getString("address"),
            json.getString("city"),
            json.getString("latitude"),
            json.getString("longitude"),
            json.getString("zipcode"))
    }

    @TypeConverter
    fun fromLocation(location: Location): String {
        return JSONObject().apply {
            put("address", location.address)
            put("city", location.city)
            put("city", location.latitude)
            put("city", location.longitude)
            put("city", location.zipcode)
        }.toString()
    }
}