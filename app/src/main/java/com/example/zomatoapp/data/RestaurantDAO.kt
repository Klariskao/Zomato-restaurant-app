package com.example.zomatoapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

/* RestaurantDAO implementing database functions */

@Dao
interface RestaurantDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRestaurant(restaurant: Restaurant)

    @Query("UPDATE restaurant_table SET distance=:distance WHERE id = :id")
    fun updateRestaurant(distance: Float?, id: Int?)

    @Query("SELECT * FROM restaurant_table ORDER BY distance ASC")
    fun readAllData(): LiveData<List<Restaurant>>

    @Query("SELECT * FROM restaurant_table WHERE id LIKE :searchedId")
    fun getRestaurantFromId(searchedId: Int): Restaurant
}