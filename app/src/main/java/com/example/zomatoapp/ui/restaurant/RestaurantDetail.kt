package com.example.zomatoapp.ui.restaurant

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.zomatoapp.data.Restaurant
import com.example.zomatoapp.data.RestaurantViewModel
import com.example.zomatoapp.databinding.RestaurantDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantDetail: Fragment() {

    private var _binding: RestaurantDetailBinding? = null
    private val binding get() = _binding!!

    private val args: RestaurantDetailArgs by navArgs()
    private lateinit var restaurantInfo: Restaurant
    private lateinit var restaurantViewModel: RestaurantViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RestaurantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ID of restaurant
        val resId = args.restaurantId

        restaurantViewModel = ViewModelProvider(this).get(RestaurantViewModel::class.java)

        getRestaurantFromDatabase(resId)
    }

    private fun getRestaurantFromDatabase(id: Int) {
        CoroutineScope(IO).launch {
            restaurantInfo = restaurantViewModel.queryRestaurant(id)
            Log.d("TAG", id.toString())
            Log.d("TAG", "Current restaurant: $restaurantInfo")
            restaurantInfo.run { setUI() }
        }
    }

    private fun setUI() {
        CoroutineScope(IO).launch {
            binding.restaurantDetailName.text = restaurantInfo.name
            binding.restaurantLocation.text = restaurantInfo.location?.address
            binding.phoneNumber.text = restaurantInfo.phone_numbers

            val webView = binding.webView
            webView.post {
                webView.webViewClient = WebViewClient()
                restaurantInfo.menu_url?.let { webView.loadUrl(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}