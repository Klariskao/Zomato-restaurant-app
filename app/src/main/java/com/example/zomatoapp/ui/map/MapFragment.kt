package com.example.zomatoapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zomatoapp.R
import com.example.zomatoapp.data.RestaurantViewModel
import com.example.zomatoapp.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/* Fragment with map of restaurants */

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapBinding.inflate(layoutInflater)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Restaurant View Model
        val restaurantViewModel = ViewModelProvider(this).get(RestaurantViewModel::class.java)
        val restaurants = restaurantViewModel.readAllData.value

        // Set up markers
        restaurants?.forEach {
            mMap.addMarker(MarkerOptions()
                .position(LatLng(it.location!!.latitude!!.toDouble(), it.location.longitude!!.toDouble()))
                .title(it.name))
        }

        // Move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng
        (restaurants?.get(0)?.location!!.latitude!!.toDouble(), restaurants[0].location!!.longitude!!.toDouble())))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}