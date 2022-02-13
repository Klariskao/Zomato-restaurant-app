package com.example.zomatoapp.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
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
import com.example.zomatoapp.ui.home.search.SuggestionClickListener
import com.example.zomatoapp.ui.home.search.SuggestionsAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder

/* Fragment for home screen with location SearchView,
   RecyclerView of search suggestions,
   RecyclerView of restaurants and
   bottom navigation to switch between Home and Map view */

class HomeFragment: Fragment(), SuggestionClickListener {

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

    // Suggestions for search Recycler View
    lateinit var suggestionsRecyclerView: RecyclerView
    // Search view
    lateinit var searchView: SearchView

    // Result of location search API
    lateinit var result: JsonObject

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate view
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        // Set up views
        suggestionsRecyclerView = view.findViewById(R.id.suggestionsRecyclerView)
        searchView = view.findViewById(R.id.searchView)

        // Get user's location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestLocationPermission(requireContext())

        // Make an http request to fetch restaurants data
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

        // Listener for location searchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                // Search for location
                Toast.makeText(requireContext(), "Searching.. please wait", Toast.LENGTH_SHORT).show()
                CoroutineScope(IO).launch {
                    makeApiRequest(query)
                }
                // Clear searchView
                searchView.setQuery("", false)
                searchView.clearFocus()

                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    // Get search suggestions
                    CoroutineScope(IO).launch {
                        result = stringToJSON(GetUserLocation()
                            .makeRequest(GetUserLocation().makeUrlFromPartData(URLEncoder.encode(query, "utf-8"))))
                        // Update suggestions
                        withContext(Main) {
                            suggestionsRecyclerView.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = SuggestionsAdapter(result, this@HomeFragment, requireContext())
                            }
                            suggestionsRecyclerView.visibility = View.VISIBLE
                        }
                    }
                }
                else {
                    suggestionsRecyclerView.visibility = View.GONE
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getLastKnownLocation() {
        // Check if location permissions are granted
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

            // Update Restaurant distance
            updateDistance(lat!!, lon!!)
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
                    getLastKnownLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted
                    Toast.makeText(context, "Location access granted", Toast.LENGTH_SHORT).show()
                    getLastKnownLocation()
                }
                else -> {
                    // No location access granted
                    Toast.makeText(context, "Location access denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // Got location permission
                getLastKnownLocation()

            }
            else -> {
                // Don't have location permission yet
                locationPermissionRequest.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        }
    }

    private fun getDistance(userLat: Double?, userLon: Double?, restaurantLat: String?, restaurantLon: String?): Float? {
        // Calculate distance from location to restaurant
        val results = FloatArray(1)
        if(userLat != null && userLon != null) {
            if(restaurantLat != null && restaurantLon != null) {
                Location.distanceBetween(userLat, userLon, restaurantLat.toDouble(), restaurantLon.toDouble(), results)
                return results[0]
            }
        }

        return null
    }

    private suspend fun makeApiRequest(city: String) {
        // Get coordinates of a place from Geocoding API
        try {
            val result1 = getCoordinates(city)

            lat = degreeConversion(result1.lookup<String>("results.annotations.DMS.lat")[0])
            lon = degreeConversion(result1.lookup<String>("results.annotations.DMS.lng")[0])

            // Update Restaurant distance
            updateDistance(lat!!, lon!!)

        }
        catch (exception: java.lang.Exception) {
            withContext(Main) {
                Toast.makeText(requireContext(), "Oops.. that didn't work out. Please try again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateDistance(lat: Double, lon: Double) {
        // Update Restaurant distance
        restaurantViewModel.readAllData.value?.forEach {
            restaurantViewModel.updateRestaurant(
                    getDistance(lat, lon, it.location?.latitude, it.location?.longitude), it.id)
        }
    }

    private fun getCoordinates(city: String): JsonObject {
        // Make a request to Geocoding API
        return stringToJSON(GetUserLocation()
            .makeRequest(GetUserLocation().makeUrl(city)))
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

    override fun onSuggestionClickClickAction(position: Int) {
        // Action on click on location suggestion
        CoroutineScope(IO).launch {
            val query = result.lookup<String>("geonames.toponymName")[position] + ", " +
                    result.lookup<String>("geonames.adminName1")[position] + ", " +
                    result.lookup<String>("geonames.countryName")[position]
            // Get coordinates of location from Geocoding API
            makeApiRequest(query)
        }

        // Clear searchView
        searchView.setQuery("", false)
        searchView.clearFocus()
        suggestionsRecyclerView.visibility = View.GONE
    }
}