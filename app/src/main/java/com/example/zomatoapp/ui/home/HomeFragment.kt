package com.example.zomatoapp.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.lookup
import com.example.zomatoapp.R
import com.example.zomatoapp.data.GetUserLocation
import com.example.zomatoapp.data.OkHttpRequest
import com.example.zomatoapp.data.RestaurantViewModel
import com.example.zomatoapp.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Fragment for home screen with RecyclerView of restaurants
class HomeFragment: Fragment() {

    // Set up binding variables
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // View model
    private lateinit var restaurantViewModel: RestaurantViewModel

    // Location variables
    private var lat: Double? = null
    private var lon: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Restaurants Recycler View adapter
    private lateinit var mAdapter: RestaurantsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        // Get user's location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestLocationPermission(requireContext())

        // Make an http request to fetch data
        OkHttpRequest(this).makeRequest(requireContext())

        // Restaurants Recycler View
        mAdapter = RestaurantsAdapter(requireContext())
        view.findViewById<RecyclerView>(R.id.restaurantsRecyclerView).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }

        // Restaurant View Model
        restaurantViewModel = ViewModelProvider(this).get(RestaurantViewModel::class.java)

        // Update Restaurant distance
        restaurantViewModel.readAllData.value?.forEach {
            restaurantViewModel.updateRestaurant(
                    getDistance(lat, lon, it.location?.latitude, it.location?.longitude), it.id)
        }

        // Observe restaurants data
        restaurantViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            mAdapter.setData(it)
        })

        view.findViewById<SearchView>(R.id.searchView).setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("TAG", "Searching coordinates for $query")
                Toast.makeText(requireContext(), "Searching.. please wait", Toast.LENGTH_SHORT).show()
                CoroutineScope(IO).launch {
                    makeApiRequest(query)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission
                (requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Assign last known location
            lat = location?.latitude
            lon = location?.longitude
            Log.d("TAG", "Updated location to $lat, $lon")

            // FIX THIS
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun requestLocationPermission(context: Context) {
        val locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted
                    Toast.makeText(context, "Location access granted", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Got approximate location")
                    getLastKnownLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted
                    Toast.makeText(context, "Location access granted", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Got better location")
                    getLastKnownLocation()
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
                getLastKnownLocation()

            }
            else -> {
                locationPermissionRequest.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        }
    }

    private fun getDistance(userLat: Double?, userLon: Double?, restaurantLat: String?, restaurantLon: String?): Float? {
        val results = FloatArray(1)
        Log.d("TAG", "Location: $userLat, $userLon")
        if(userLat != null && userLon != null) {
            if(restaurantLat != null && restaurantLon != null) {
                Location.distanceBetween(userLat, userLon, restaurantLat.toDouble(), restaurantLon.toDouble(), results)
                return results[0]
            }
        }
        for (result in results) {
            Log.d("TAG", result.toString())
        }

        return null
    }

    private suspend fun makeApiRequest(city: String) {
        try {
            val result1 = getCoordinates(city)

            val lat = degreeConversion(result1.lookup<String>("results.annotations.DMS.lat")[0])
            val lon = degreeConversion(result1.lookup<String>("results.annotations.DMS.lng")[0])

            Log.d("TAG", lat.toString())
            Log.d("TAG", lon.toString())

            // Update Restaurant distance
            restaurantViewModel.readAllData.value?.forEach {
                restaurantViewModel.updateRestaurant(
                        getDistance(lat, lon, it.location?.latitude, it.location?.longitude), it.id)
            }

        }
        catch (exception: java.lang.Exception) {
            Log.d("TAG", exception.toString())
            withContext(Main) {
                Toast.makeText(requireContext(), "Oops.. that didn't work out. Please try again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCoordinates(city: String): JsonObject {
        Log.d("TAG", "Getting result")
        return stringToJSON(GetUserLocation(city).makeRequest())
    }

    private fun stringToJSON(string: String): JsonObject {
        val parser: Parser = Parser.default()
        val stringBuilder: StringBuilder = StringBuilder(string)
        return parser.parse(stringBuilder) as JsonObject
    }

    private fun degreeConversion(deg: String): Double {
        val direction: Map<String, Int> = mapOf(" N" to 1, " S" to -1, " E" to 1, " W" to -1)
        val new = deg.replace('°', ' ').replace('\'', ' ').replace("\'\'", " ")
        val newList = new.split("  ")
        return (newList[0].toInt() + newList[1].toInt() / 60.0 * direction.getValue(newList[3]))
    }
}