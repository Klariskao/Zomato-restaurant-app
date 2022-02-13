package com.example.zomatoapp.data

import androidx.lifecycle.LiveData

/* Restaurant repository class */

class RestaurantRepository(private val RestaurantDao: RestaurantDAO) {

    val readAllData: LiveData<List<Restaurant>> = RestaurantDao.readAllData()

    fun queryRestaurant(id: Int) = RestaurantDao.getRestaurantFromId(id)

    suspend fun addRestaurant(restaurant: Restaurant) {
        RestaurantDao.addRestaurant(restaurant)
    }

    fun updateRestaurant(distance: Float?, id: Int?) {
        RestaurantDao.updateRestaurant(distance, id)
    }
}