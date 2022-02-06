package com.example.zomatoapp.data

import com.google.gson.annotations.SerializedName

data class Result(

    @SerializedName("results_found") var resultsFound: Int? = null,
    @SerializedName("results_start") var resultsStart: Int? = null,
    @SerializedName("results_shown") var resultsShown: Int? = null,
    @SerializedName("restaurants") var restaurants: ArrayList<Restaurants> = arrayListOf()
)