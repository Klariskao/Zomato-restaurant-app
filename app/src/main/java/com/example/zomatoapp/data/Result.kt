package com.example.zomatoapp.data

import com.google.gson.annotations.SerializedName

data class Result(

    @SerializedName("restaurants") var restaurants: ArrayList<Restaurants> = arrayListOf()
)