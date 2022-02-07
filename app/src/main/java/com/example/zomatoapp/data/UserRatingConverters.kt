package com.example.zomatoapp.data

import androidx.room.TypeConverter
import org.json.JSONObject

class UserRatingConverters {

    @TypeConverter
    fun toUserRating(string: String): UserRating {
        val json = JSONObject(string)
        return UserRating(json.getString("aggregateRating"),
                json.getInt("votes"))
    }

    @TypeConverter
    fun fromUserRating(rating: UserRating): String {
        return JSONObject().apply {
            put("aggregateRating", rating.aggregateRating)
            put("votes", rating.votes)
        }.toString()
    }
}