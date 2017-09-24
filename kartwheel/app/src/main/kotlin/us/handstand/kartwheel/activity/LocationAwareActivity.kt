package us.handstand.kartwheel.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import us.handstand.kartwheel.location.UserLocation
import us.handstand.kartwheel.util.Permissions
import us.handstand.kartwheel.util.SnackbarUtil


/**
 * Activity that handles Location settings requests
 */
open class LocationAwareActivity : AppCompatActivity() {
    lateinit var userLocation: UserLocation
    private var locationPermissionsDenied = false
    private var locationSettingsRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userLocation = UserLocation(this)
    }

    override fun onResume() {
        super.onResume()
        if (locationSettingsRequested) {
            return
        }
        if (locationPermissionsDenied) {
            SnackbarUtil.show(this, "Need location permissions to play, bro!")
        } else {
            userLocation.requestLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        userLocation.stopLocationUpdates()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Permissions.LOCATION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                userLocation.requestLocationUpdates()
            } else {
                // Prevents us from an infinite loop of permission requests
                // TODO: Show a more aggressive and persistent UI for this state
                locationPermissionsDenied = true
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // The user was redirected to the device's location settings
        if (requestCode == Permissions.CHECK_SETTINGS_REQUEST) {
            // If the user has increased the permissions, then request location updates.
            // If the user is a dick and didn't, then show a Snackbar :)
            if (locationPermissionsDenied) {
                return
            }
            locationSettingsRequested = true
            Permissions.getDeviceLocationSettingTask(this)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            userLocation.requestLocationUpdates()
                        } else {
                            locationPermissionsDenied = true
                            SnackbarUtil.show(this, "Common, dude. We need those permissions!")
                        }
                        locationSettingsRequested = false
                    }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}