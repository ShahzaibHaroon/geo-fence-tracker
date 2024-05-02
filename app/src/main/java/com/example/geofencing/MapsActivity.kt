package com.example.geofencing

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.geofencing.databinding.ActivityMapsBinding
import com.example.geofencing.helpers.GeofenceHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMapLongClickListener,
    View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val MAX_RADIUS = 1000 // Maximum radius in meters
    private val INCREMENT_VALUE = 100 // Increment/Decrement value in meters
    private lateinit var userLatLng: LatLng
    lateinit var geofenceHelper: GeofenceHelper
    private val GEO_FENCE_ID = "some_id"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupMap()
        setupGeoFencingClient()
        initViews()
        setListeners()
    }

    private fun initViews() {
        binding.radiusSlider.max = MAX_RADIUS
    }

    private fun setListeners() {
        binding.radiusSlider.setOnSeekBarChangeListener(this)
        binding.plusButton.setOnClickListener(this)
        binding.minusButton.setOnClickListener(this)
    }

    private fun setupGeoFencingClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        /*
                // Add a marker in Sydney and move the camera
                val sydney = LatLng(-34.0, 151.0)
                mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
        mMap.setOnMapLongClickListener(this)
        moveCameraToCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun moveCameraToCurrentLocation() {
        // Retrieve the last known location of the device
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // If location is available, move camera to that location
                location?.let {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    userLatLng = currentLatLng
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure to retrieve location
                Log.e("MapActivity", "Error getting location: $exception")
            }
    }

    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()
        userLatLng = latLng
        addCircle()
        addGeofence()
    }

    private fun addMarker() {
        val options = MarkerOptions().position(userLatLng)
        mMap.addMarker(options)
    }

    private fun addCircle() {
        val circleOptions = CircleOptions()
        circleOptions.center(userLatLng)
        circleOptions.radius(binding.radiusSlider.progress.toDouble())
        circleOptions.strokeColor(this.getColor(R.color.white))
        circleOptions.fillColor(this.getColor(R.color.blue))
        circleOptions.strokeWidth(4.0f)
        mMap.addCircle(circleOptions)
        addMarker()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.plus_button -> {
                increaseRadius()
            }

            R.id.minus_button -> {
                decreaseRadius()
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        binding.tvRadius.text = "${progress} meters"
        mMap.clear()
        addCircle()
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence() {
        val geofence = geofenceHelper.getGeofence(
            GEO_FENCE_ID,
            LatLng(userLatLng.latitude, userLatLng.longitude),
            binding.radiusSlider.progress.toFloat(),
            (Geofence.GEOFENCE_TRANSITION_ENTER) or (Geofence.GEOFENCE_TRANSITION_DWELL) or (Geofence.GEOFENCE_TRANSITION_EXIT)
        )
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper.getLocalPendingIntent()
        geofencingClient.addGeofences(geofencingRequest, pendingIntent!!)
            .addOnSuccessListener {
                Log.d("GEOFENCING", "GEOFENCE ADDED")
            }
            .addOnFailureListener { e ->
                Log.d("GEOFENCING", "onFailure: ${geofenceHelper.getErrorString(e)}")
            }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    private fun increaseRadius() {
        val currentValue = binding.radiusSlider.progress
        if (currentValue + INCREMENT_VALUE <= MAX_RADIUS) {
            binding.radiusSlider.progress = currentValue + INCREMENT_VALUE
        } else {
            Toast.makeText(this, "Maximum radius reached", Toast.LENGTH_SHORT).show()
        }
    }

    private fun decreaseRadius() {
        val currentValue = binding.radiusSlider.progress
        if (currentValue - INCREMENT_VALUE >= 0) {
            binding.radiusSlider.progress = currentValue - INCREMENT_VALUE
        } else {
            Toast.makeText(this, "Minimum radius reached", Toast.LENGTH_SHORT).show()
        }
    }
}