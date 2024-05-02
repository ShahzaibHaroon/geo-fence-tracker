package com.example.geofencing.helpers

import java.text.SimpleDateFormat
import java.util.Date

object Utils {
    fun formatTime(milliseconds: Long): String {
        // Convert milliseconds to Date with time
        val dateTime = Date(milliseconds)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(dateTime)
    }
}
