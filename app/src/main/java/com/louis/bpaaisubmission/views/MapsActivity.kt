package com.louis.bpaaisubmission.views


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.louis.bpaaisubmission.R
import com.louis.bpaaisubmission.data.remote.response.StoryItem
import com.louis.bpaaisubmission.databinding.ActivityMapsBinding
import com.louis.bpaaisubmission.utils.Helper.toBearerToken
import com.louis.bpaaisubmission.utils.Helper.toEntityModel
import com.louis.bpaaisubmission.utils.Result
import com.louis.bpaaisubmission.utils.ViewModelFactory
import com.louis.bpaaisubmission.viewmodels.MapsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val mapsViewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMapFragment()
        getDataStories()

        binding.fabBack.setOnClickListener { finish() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        showLoading(true)
    }

    private fun showLoading(state: Boolean) {
        if (state) binding.pgMaps.visibility = View.VISIBLE
        else binding.pgMaps.visibility = View.GONE
    }

    private fun getDataStories() {
        mapsViewModel.apply {
            getSession().observe(this@MapsActivity) { session ->
                getStoriesWithLocation(session.token.toBearerToken()).observe(this@MapsActivity) { result ->
                    showResult(result)
                }
            }
        }
    }

    private fun showResult(result: Result<ArrayList<StoryItem>>?) {
        when (result) {
            is Result.Loading -> { showLoading(true) }

            is Result.Success -> {
                result.data.forEach { addMarker(it) }
                showLoading(false)
            }

            is Result.Error -> {
                Snackbar.make(
                    binding.root,
                    resources.getString(R.string.error, result.errorMessage),
                    Snackbar.LENGTH_SHORT
                ).show()
                showLoading(false)
            }
            is Result.Empty -> {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.empty),
                    Toast.LENGTH_SHORT
                ).show()
                showLoading(false)
            }
            else -> {}
        }
    }

    private fun addMarker(story: StoryItem) {
        val snippet = StringBuilder(resources.getString(R.string.story)).append(" : ${story.description}")
        if (story.lat != null && story.lon != null) {
            LatLng(story.lat, story.lon).apply {
                mMap.addMarker(
                    MarkerOptions()
                        .position(this)
                        .title(story.name.toString())
                        .snippet(snippet.toString())

                )?.tag = story
            }
        }
    }

    private fun setMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setMapStyle()

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isMapToolbarEnabled = true
            isCompassEnabled = false
        }

        mMap.setOnMarkerClickListener(this)
        mMap.setOnInfoWindowClickListener(this)

        getMyLocation()

    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        CoroutineScope(Dispatchers.Main).launch {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 15f))
        }
        return false
    }

    override fun onInfoWindowClick(marker: Marker) {
        Intent(this@MapsActivity, DetailStoryActivity::class.java).apply {
            val story = marker.tag as StoryItem
            putExtra(DetailStoryActivity.EXTRA_DETAIL, story.toEntityModel())
        }.also {
            startActivity(it)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> { getMyLocation() }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> { getMyLocation() }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val myLocation = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10f))
                } else {
                    Toast.makeText(applicationContext, resources.getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val TAG = "MapsActivity"
    }
}