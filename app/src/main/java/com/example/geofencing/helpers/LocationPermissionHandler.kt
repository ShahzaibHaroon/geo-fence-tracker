import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.geofencing.R
import com.google.android.gms.maps.GoogleMap

class LocationPermissionHandler(private val activity: Activity) {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 101

    }

    // Store the callback for the combined permissions check
    private var permissionCallback: ((locationGranted: Boolean) -> Unit)? = null

    fun requestLocationPermissions(callback: (locationGranted: Boolean) -> Unit) {
        permissionCallback = callback

        val fineLocationPermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        var backgroundLocationPermission = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationPermission = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED
            if (fineLocationPermission && backgroundLocationPermission) {
                callback(true)
            } else {
                if (!fineLocationPermission) {
                    requestBackGroundLocationPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    requestBackGroundLocationPermissions(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        } else {

            if (fineLocationPermission) {
                callback(true)
            } else {
                requestBackGroundLocationPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    fun requestBackGroundLocationPermissions(
        requestPermission: String,
        requestPermissionCode: Int
    ) {
        val permissionsToRequest = mutableListOf<String>()
        permissionsToRequest.add(requestPermission)
        ActivityCompat.requestPermissions(
            activity,
            permissionsToRequest.toTypedArray(),
            requestPermissionCode
        )
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            //val coarseLocationGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            val fineLocationGranted =
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (fineLocationGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestBackGroundLocationPermissions(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    permissionCallback?.invoke(fineLocationGranted)
                }
            } else {
                permissionCallback?.invoke(fineLocationGranted)
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            val backGroundLocationGranted =
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

            permissionCallback?.invoke(backGroundLocationGranted)
        }
    }

    fun enableMyLocation(map: GoogleMap) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            // Permission not granted, handle this case
        }
    }

    fun showLocationPermissionDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.location_access_required)
        builder.setMessage(R.string.geofencing_permission_denied_message)
        builder.setPositiveButton(R.string.open_settings) { dialog, which ->
            openSettings(context)
        }
        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun openSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)

    }

}
