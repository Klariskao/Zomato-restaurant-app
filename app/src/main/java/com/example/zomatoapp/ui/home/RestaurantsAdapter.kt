package com.example.zomatoapp.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zomatoapp.R
import com.example.zomatoapp.data.Location
import com.example.zomatoapp.data.Restaurant
import com.example.zomatoapp.data.UserRating
import com.example.zomatoapp.databinding.RestaurantRowBinding

class RestaurantsAdapter() : RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>() {

    private var restaurantList = emptyList<Restaurant>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RestaurantRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, View(parent.context))
    }

    override fun getItemCount() = restaurantList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = restaurantList[position]
        holder.bind(currentItem)
    }

    fun setData(restaurants: List<Restaurant>) {
        this.restaurantList = restaurants
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: RestaurantRowBinding, private val view: View): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Restaurant) {
            loadImage(item)
            binding.restaurantName.text = item.name
            //binding.restaurantLocation.text = item.location?.address
            binding.priceRange.text = item.price_range?.let { "$".repeat(it) }
            binding.distance.text = "${item.distance} mil"
            binding.ratingBar.rating = item.userRating?.aggregateRating?.toFloat() ?: 0F
            binding.votes.text = "(${item.userRating?.votes})"

            binding.restaurantRow.setOnClickListener {
                val action = HomeFragmentDirections.actionNavigationHomeToRestaurantDetail(item.id!!)
                view.findNavController().navigate(action)
            }
        }

        private fun loadImage(item: Restaurant) {
            if(item.featuredImage.isNullOrEmpty()) {
                binding.featuredImage.setImageResource(R.drawable.food_image)
            }
            else {
                Glide.with(itemView.context).load(item.featuredImage).into(binding.featuredImage)
            }
        }
    }
}