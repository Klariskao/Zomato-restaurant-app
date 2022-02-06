package com.example.zomatoapp.data

import androidx.lifecycle.LiveData

class RestaurantRepository(private val RestaurantDao: RestaurantDAO) {

    val readAllData: LiveData<List<Restaurant>> = RestaurantDao.readAllData()

    suspend fun addRestaurant(restaurant: Restaurant) {
        RestaurantDao.addRestaurant(restaurant)
    }
}