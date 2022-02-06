package com.example.zomatoapp.data

import com.google.gson.annotations.SerializedName

data class Restaurants (

    @SerializedName("restaurant") var restaurant: Restaurant? = null
)