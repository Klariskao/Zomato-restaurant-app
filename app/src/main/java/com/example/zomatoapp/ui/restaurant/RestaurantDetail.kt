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
import com.example.zomatoapp.data.RestaurantDAO
import com.example.zomatoapp.data.RestaurantViewModel
import com.example.zomatoapp.databinding.FragmentHomeBinding
import com.example.zomatoapp.databinding.RestaurantDetailBinding

class RestaurantDetail: Fragment() {

    private var _binding: RestaurantDetailBinding? = null
    private val binding get() = _binding!!

    // ID of restaurant
    private val restaurantId: Int by navArgs()

    private val restaurantViewModel = ViewModelProvider(this).get(RestaurantViewModel::class.java)
    private val restaurantInfo = restaurantViewModel.queryRestaurant(restaurantId)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RestaurantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("TAG", "Current restaurant: $restaurantInfo")

        binding.restaurantDetailName.text = restaurantInfo.name

        val webView = binding.webView
        webView.webViewClient = WebViewClient()
        restaurantInfo.menu_url?.let { webView.loadUrl(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}