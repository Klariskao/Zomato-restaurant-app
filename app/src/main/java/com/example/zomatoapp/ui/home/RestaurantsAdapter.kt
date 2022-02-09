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

    //private var restaurantList = emptyList<Restaurant>()
    private var restaurantList = listOf<Restaurant>(Restaurant(id=16611378, name="Ruby'S Mount Kembla", url="https://www.zomato.com/mount-kembla-nsw/rubys-mount-kembla-mount-kembla?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1", location= Location(address="Lot 1120 Harry Graham Dr, Mount Kembla", latitude="-34.4121576000", longitude="150.8214917000"), price_range=1, menu_url="https://www.zomato.com/mount-kembla-nsw/rubys-mount-kembla-mount-kembla/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop", phone_numbers="(02) 4272 2541", featuredImage="", userRating= UserRating(aggregateRating="3.9", votes=9), distance=0))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RestaurantRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, View(parent.context))
    }

    override fun getItemCount() = restaurantList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("TAG", "Binding")
        val currentItem = restaurantList[position]
        holder.bind(currentItem)
    }

    fun setData(restaurants: List<Restaurant>) {
        this.restaurantList = restaurants
        Log.d("TAG", "Notified")
        Log.d("TAG", "Rest. list: ${this.restaurantList}")
        Log.d("TAG", "Size: ${this.restaurantList.size}")
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

            Log.d("TAG", "Binding")
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


/*class RestaurantsAdapter() : RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>() {

    private var restaurantList = listOf<Restaurant>(Restaurant(id=16611378, name="Ruby'S Mount Kembla", url="https://www.zomato.com/mount-kembla-nsw/rubys-mount-kembla-mount-kembla?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1", location= Location(address="Lot 1120 Harry Graham Dr, Mount Kembla", latitude="-34.4121576000", longitude="150.8214917000"), price_range=1, menu_url="https://www.zomato.com/mount-kembla-nsw/rubys-mount-kembla-mount-kembla/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop", phone_numbers="(02) 4272 2541", featuredImage="", userRating= UserRating(aggregateRating="3.9", votes=9), distance=0))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_row, parent, false)
        return ViewHolder(view)
    }

    // Get number of menu items
    override fun getItemCount() = restaurantList.size

    // Loop through menu items
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = restaurantList[position]
        holder.bind(item)
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        // Display info for each item on main page
        fun bind(restaurant: Restaurant) {
            view.findViewById<TextView>(R.id.restaurantName).text = restaurant.name

            if(restaurant.featuredImage.isNullOrEmpty()) {
                view.findViewById<ImageView>(R.id.featuredImage).setImageResource(R.drawable.food_image)
            }
            else {
                Glide.with(itemView.context).load(restaurant.featuredImage).into(view.findViewById<ImageView>(R.id.featuredImage))
            }
        }
    }
}*/
