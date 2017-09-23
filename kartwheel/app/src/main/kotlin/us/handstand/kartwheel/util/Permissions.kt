package us.handstand.kartwheel.util

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat


object Permissions {
    val LOCATION_REQUEST = 1
    fun hasLocationPermissions(context: Context): Boolean =
            ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    fun requestLocationPermissions(activity: Activity, fragment: Fragment) {
        if (!hasLocationPermissions(activity)) {
            fragment.requestPermissions(arrayOf(ACCESS_FINE_LOCATION), LOCATION_REQUEST)
        }
    }
}