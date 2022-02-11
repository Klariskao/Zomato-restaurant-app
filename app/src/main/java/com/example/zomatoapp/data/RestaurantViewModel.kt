package com.example.zomatoapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Restaurant>>
    private val repository: RestaurantRepository

    init {
        val restaurantDao = RestaurantDatabase.getDatabase(application).restaurantDao()
        repository = RestaurantRepository(restaurantDao)
        readAllData = repository.readAllData
    }

    fun addRestaurant(restaurant: Restaurant) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRestaurant(restaurant)
        }
    }

    fun queryRestaurant(id: Int): Restaurant {
        return repository.queryRestaurant(id)
    }

    fun updateRestaurant(distance: Float?, id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRestaurant(distance, id)
        }
    }
}