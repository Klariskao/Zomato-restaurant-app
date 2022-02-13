package com.example.zomatoapp.data

import com.google.gson.annotations.SerializedName

/* Class containing user rating of a restaurant */

data class UserRating (

        @SerializedName("aggregate_rating") var aggregateRating: String? = null,
        @SerializedName("votes") var votes: Int? = null
)