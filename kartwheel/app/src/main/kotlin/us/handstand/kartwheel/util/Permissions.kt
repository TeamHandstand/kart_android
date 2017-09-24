package us.handstand.kartwheel.util

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import us.handstand.kartwheel.R


object Permissions {
    val LOCATION_REQUEST = 101

    fun hasLocationPermissions(context: Context): Boolean =
            ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    fun requestLocationPermissions(activity: Activity) {
        if (!hasLocationPermissions(activity)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(activity)
                        .setTitle(R.string.location_permission_dialog_title)
                        .setMessage(R.string.location_permission_dialog_message)
                        .setPositiveButton(android.R.string.ok, { _, _ -> ActivityCompat.requestPermissions(activity, arrayOf(ACCESS_FINE_LOCATION), LOCATION_REQUEST) })
                        .show()
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(ACCESS_FINE_LOCATION), LOCATION_REQUEST)
            }
        }
    }
}