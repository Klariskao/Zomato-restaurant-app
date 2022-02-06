package com.example.zomatoapp.data

import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName

data class Location(

    @SerializedName("address") var address: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("zipcode") var zipcode: String? = null
)