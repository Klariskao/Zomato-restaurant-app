package com.example.zomatoapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zomatoapp.R
import com.example.zomatoapp.databinding.RestaurantRowBinding

/*
class RestaurantsAdapter(restaurantFeed: RestaurantFeed) : RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>() {
    val binding = RestaurantRowBinding.inflate(layoutInflater)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_row, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = 6

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(result2, timeZone, position)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(result2: JsonObject, timeZone: String, position: Int) {
            restaurantName.
            itemView.findViewById<TextView>(R.id.hourView).text = dateTime(result2.lookup<Int>("daily.dt")[position + 1], timeZone)
        }
    }
}*/
