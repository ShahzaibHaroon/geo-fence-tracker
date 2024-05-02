package com.example.geofencing.helpers

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesHandler {

    private const val PREF_NAME = "MyPrefs"
    private const val LOCATION_PERMISSION_GRANTED_KEY = "location_permission_granted"

    // Function to get SharedPreferences instance
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Function to check if location permissions were granted
    fun isLocationPermissionGranted(context: Context): Boolean {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getBoolean(LOCATION_PERMISSION_GRANTED_KEY, false)
    }

    // Function to set location permissions
    fun setLocationPermissionGranted(context: Context, granted: Boolean) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(LOCATION_PERMISSION_GRANTED_KEY, granted)
        editor.apply()
    }
}
