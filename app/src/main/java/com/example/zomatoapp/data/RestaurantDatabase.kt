package com.example.zomatoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/* Room database containing restaurant data */

@Database(entities = [Restaurant::class], version = 1, exportSchema = false)
@TypeConverters(LocationConverters::class, UserRatingConverters::class)
abstract class RestaurantDatabase: RoomDatabase() {

    abstract fun restaurantDao(): RestaurantDAO

    companion object {
        @Volatile
        private var INSTANCE: RestaurantDatabase? = null

        fun getDatabase(context: Context): RestaurantDatabase {
            val tempInstance = INSTANCE

            if(tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RestaurantDatabase::class.java,
                    "restaurant_database"
                ).build()
                INSTANCE = instance

                return instance
            }
        }
    }
}