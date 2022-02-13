package com.example.zomatoapp.data

import com.google.gson.annotations.SerializedName

/* Class containing a result of Zomato API query */

data class Result(

    @SerializedName("restaurants") var restaurants: ArrayList<Restaurants> = arrayListOf()
)