package us.handstand.kartwheel.util

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.Task
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.LocationAwareActivity


object Permissions {
    val LOCATION_REQUEST = 101
    val CHECK_SETTINGS_REQUEST = 102
    val locationRequest = LocationRequest()
            .setInterval(1000)
            .setFastestInterval(400)
            .setPriority(PRIORITY_HIGH_ACCURACY)!!

    fun hasApi(apiLevel: Int): Boolean = Build.VERSION.SDK_INT >= apiLevel

    fun hasLocationPermissions(context: Context): Boolean =
            ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    fun requestLocationPermissions(activity: LocationAwareActivity) {
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

    /**
     * Checks that the device's location settings are set to high accuracy. If they aren't, then
     * the user will be redirected to the settings page.
     *
     * All devices that see this application in the Play Store will have GPS.
     *
     * @return  A task upon which the caller should attach an onCompletionListener. Failure cases are
     *          handled by the LocationAwareActivity.
     */
    fun checkDeviceLocationSettings(activity: LocationAwareActivity): Task<LocationSettingsResponse> {
        return getDeviceLocationSettingTask(activity)
                .addOnFailureListener {
                    when ((it as ApiException).statusCode) {
                        CommonStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                (it as ResolvableApiException).startResolutionForResult(activity, CHECK_SETTINGS_REQUEST)
                            } catch (e: IntentSender.SendIntentException) {
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            SnackbarUtil.show(activity, "Location settings not satisfied. Please contact support.")
                        }
                    }
                }
    }

    fun getDeviceLocationSettingTask(activity: LocationAwareActivity): Task<LocationSettingsResponse> {
        val settingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        return LocationServices.getSettingsClient(activity).checkLocationSettings(settingsRequest)
    }
}