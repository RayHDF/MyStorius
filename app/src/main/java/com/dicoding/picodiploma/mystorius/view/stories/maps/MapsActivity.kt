package com.dicoding.picodiploma.mystorius.view.stories.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.dicoding.picodiploma.mystorius.R
import com.dicoding.picodiploma.mystorius.data.pref.UserPreference
import com.dicoding.picodiploma.mystorius.data.pref.dataStore

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.picodiploma.mystorius.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.mystorius.view.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val pref = UserPreference.getInstance(dataStore)
        val user = runBlocking { pref.getSession().first() }
        user.token?.let {
            mapsViewModel.getStoriesWithLocation(it)
            mapsViewModel.stories.observe(this) { stories ->
                Log.d("MapsActivity", "Stories with location: $stories")
                stories?.forEach { story ->
                    val location = LatLng(story.lat ?: 0.0, story.lon?: 0.0)
                    mMap.addMarker(MarkerOptions().position(location).title(story.name).snippet(story.description))
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }
}