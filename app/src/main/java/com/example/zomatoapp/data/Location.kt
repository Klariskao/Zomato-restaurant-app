package com.example.zomatoapp.data

import com.google.gson.annotations.SerializedName

data class Location(

    @SerializedName("address") var address: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null
)