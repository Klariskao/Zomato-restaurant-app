package com.example.zomatoapp.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class OkHttpRequest(val owner: ViewModelStoreOwner) {

    private val url = "https://cms.dgrp.cz/ios/data.json"

    private val request = Request.Builder().url(url).build()

    private val client = OkHttpClient()

    fun makeRequest(context: Context) {
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).postDelayed( {
                    Toast.makeText(context, "Failed to fetch data. " +
                            "Please check your internet connection and try again.", Toast.LENGTH_LONG).show()
                }, 2000)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    val gson = GsonBuilder().create()
                    val restaurantsFeed = gson.fromJson(body, Result::class.java)

                    val mRestaurantViewModel = ViewModelProvider(owner).get(RestaurantViewModel::class.java)
                    restaurantsFeed.restaurants.forEach {
                        it.restaurant?.let { restaurant ->
                            mRestaurantViewModel.addRestaurant(restaurant)
                        }
                    }
                }
            }
        })
    }
}