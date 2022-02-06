package com.example.zomatoapp.data

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException

class OkHttpRequest() {
    private val url = "https://cms.dgrp.cz/ios/data.json"

    private val request = Request.Builder().url(url).build()

    private val client = OkHttpClient()

    fun makeRequest() {
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG", "Failed to fetch data")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    Log.d("TAG", body)

                    val gson = GsonBuilder().create()
                    val restaurantsFeed = gson.fromJson(body, Result::class.java)
                    Log.d("TAG", restaurantsFeed.restaurants.toString())
                }
            }
        })
    }
}