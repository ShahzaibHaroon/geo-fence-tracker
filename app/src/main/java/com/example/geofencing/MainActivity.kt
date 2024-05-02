package com.example.geofencing

import LocationPermissionHandler
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.geofencing.databinding.ActivityMainBinding
import com.example.geofencing.helpers.SharedPreferencesHandler

class MainActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var binding: ActivityMainBinding
    lateinit var permissionHandler: LocationPermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissionsHaveGrantedPreviously()
        setListeners()

    }

    private fun checkPermissionsHaveGrantedPreviously() {
        val isLocationPermissionGrantedPreviously =
            SharedPreferencesHandler.isLocationPermissionGranted(this)
        if (isLocationPermissionGrantedPreviously) {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun setListeners() {
        binding.rlBtnProceed.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.rlBtnProceed -> {
                requestLocationPermissions()
            }
        }
    }

    private fun requestLocationPermissions() {
        permissionHandler = LocationPermissionHandler(this)
        permissionHandler.requestLocationPermissions { locationGranted ->
            if (locationGranted) {
                SharedPreferencesHandler.setLocationPermissionGranted(this, true)
                startActivity(Intent(this, MapsActivity::class.java))
            } else {
                permissionHandler.showLocationPermissionDialog(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionHandler.onRequestPermissionsResult(requestCode, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}