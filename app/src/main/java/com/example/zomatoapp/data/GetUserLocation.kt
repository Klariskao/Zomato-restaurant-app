package com.example.zomatoapp.data

import java.net.URL

class GetUserLocation(private val query: String) {

    private val key: String = "&key=0826219a14f94bfbb49a56b9902f31e8"
    private val url: String = "https://api.opencagedata.com/geocode/v1/json"

    private fun makeUrl(place: String): String {
        return "$url?q=$place&limit=1$key"
    }

    fun makeRequest():String {
        return URL(makeUrl(query)).readText()
    }
}