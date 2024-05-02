package com.example.geofencing.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {


    companion object {
        private const val TAG = "GeofenceReceiver"
        private var entranceTime: Long = 0
        private var exitTime: Long = 0
    }

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent!!.hasError()) {
            Log.e(TAG, "GeofencingEvent error: ${geofencingEvent.errorCode}")
            return
        }
        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            entranceTime = System.currentTimeMillis()
            val formattedDateTime = Utils.formatTime(entranceTime)
            Log.d(TAG, "Entered geofence at: $formattedDateTime")
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            exitTime = System.currentTimeMillis()
            val formattedDateTime = Utils.formatTime(exitTime)
            Log.d(TAG, "Exited geofence at: $formattedDateTime")
            if (entranceTime != 0L) {
                val durationInMillis = exitTime - entranceTime
                Log.d(TAG, "Time spent in geofence: $durationInMillis ms")
                val durationInSeconds = durationInMillis / 1000
                Log.d(TAG, "Time spent in geofence: $durationInSeconds seconds")
                entranceTime = 0
                exitTime = 0
            } else {
                Log.d(TAG, "Exit time recorded without corresponding entrance time")
            }
        }
    }
}