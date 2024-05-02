package com.example.geofencing.helpers

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng

class GeofenceHelper(private val context: Context) : ContextWrapper(context) {

    var pendingIntent: PendingIntent? = null

    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }

    fun getGeofence(id: String, latLng: LatLng, radius: Float, transitionTypes: Int): Geofence {
        return Geofence.Builder()
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setRequestId(id)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    fun getLocalPendingIntent(): PendingIntent? {
        if (pendingIntent != null) {
            return pendingIntent
        }

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            context,
            2608,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE // Add FLAG_MUTABLE
        )
        return pendingIntent
    }


    fun getErrorString(e: Exception): String {
        if (e is ApiException) {
            when (e.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> {
                    return "GEOFENCE_NOT_AVAILABLE"
                }

                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> {
                    return "GEOFENCE_NOT_AVAILABLE"
                }

                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> {
                    return "GEOFENCE_NOT_AVAILABLE"
                }

                else -> {
                    return e.localizedMessage?.toString() ?: "SERVER_ERROR"
                }
            }
        } else {
            return e.localizedMessage?.toString() ?: "SERVER_ERROR"
        }

    }
}