package com.example.zomatoapp.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zomatoapp.R
import com.example.zomatoapp.data.OkHttpRequest
import com.example.zomatoapp.data.RestaurantViewModel
import com.example.zomatoapp.databinding.FragmentHomeBinding

// Fragment for home screen with RecyclerView of restaurants
class HomeFragment: Fragment() {

    // Set up binding variables
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // View model variables
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var restaurantViewModel: RestaurantViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        requestLocationPermission(requireContext())


        // Make an http request to fetch data
        OkHttpRequest(this).makeRequest()

        // Restaurants Recycler View
        val mAdapter = RestaurantsAdapter()
        view.findViewById<RecyclerView>(R.id.restaurantsRecyclerView).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }

        // Restaurant View Model
        restaurantViewModel = ViewModelProvider(this).get(RestaurantViewModel::class.java)
        restaurantViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            mAdapter.setData(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestLocationPermission(context: Context) {
        val locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted
                    Toast.makeText(context, "Location access granted", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Got location")
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted
                    Toast.makeText(context, "Location access granted", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Got location")
                }
                else -> {
                    // No location access granted
                    Toast.makeText(context, "Location access denied", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Got not location")
                }
            }
        }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // You can use the API that requires the permission
                Log.d("TAG", "Got location")
            }
            else -> {
                locationPermissionRequest.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        }
    }
}