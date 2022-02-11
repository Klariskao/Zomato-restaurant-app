package com.example.zomatoapp.data

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "restaurant_table")
data class Restaurant(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("location") val location: Location? = Location(),
    @SerializedName("price_range") val price_range: Int?,
    @SerializedName("menu_url") val menu_url: String?,
    @SerializedName("phone_numbers") val phone_numbers: String?,
    @SerializedName("featured_image") var featuredImage: String?,
    @SerializedName("user_rating") var userRating: UserRating? = UserRating(),
    var distance: Float? = null
)