package us.handstand.kartwheel.location


import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.Task
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.UserRaceInfo
import us.handstand.kartwheel.model.UserRaceInfoModel
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.Permissions
import us.handstand.kartwheel.util.SnackbarUtil

class UserLocation(val activity: Activity) : Observable<Location>() {
    private val CHECK_SETTINGS_REQUEST = 102
    private val publisher = PublishSubject.create<Location>()
    private val locationProvider = FusedLocationProviderClient(activity)
    private val locationRequest = LocationRequest()
            .setInterval(1000)
            .setFastestInterval(400)
            .setPriority(PRIORITY_HIGH_ACCURACY)

    private val locationCallbacks = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (result != null) {
                publishLocation(result.lastLocation)
            }
        }
    }

    private fun publishLocation(location: Location?) {
        if (location == null) return
        publisher.onNext(location)
        val query = UserRaceInfo.FACTORY.select_for_id(Storage.userId)
        Database.get().createQuery(UserRaceInfoModel.TABLE_NAME, query.statement, *query.args)
                .mapToOne { UserRaceInfo.FACTORY.select_for_idMapper().map(it) }
                .doOnNext {
                    API.updateLocation(Storage.eventId, Storage.raceId, it.id(), location)
                }
    }

    override fun subscribeActual(observer: Observer<in Location>) {
        publisher.subscribe(observer)
    }

    fun stopLocationUpdates() {
        locationProvider.removeLocationUpdates(locationCallbacks)
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        if (Permissions.hasLocationPermissions(activity)) {
            val settingsResponse = checkDeviceLocationSettings()
            settingsResponse.addOnCompleteListener {
                locationProvider.lastLocation
                        .addOnSuccessListener {
                            publishLocation(it)
                        }
                        .addOnFailureListener {
                            SnackbarUtil.show(activity, "Unable to get last location")
                        }
                locationProvider.requestLocationUpdates(locationRequest, locationCallbacks, Looper.getMainLooper())
            }
        }
    }

    private fun checkDeviceLocationSettings(): Task<LocationSettingsResponse> {
        val settingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val settingsResponse = LocationServices.getSettingsClient(activity).checkLocationSettings(settingsRequest)
        settingsResponse.addOnFailureListener {
            when ((it as ApiException).statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        (it as ResolvableApiException).startResolutionForResult(activity, CHECK_SETTINGS_REQUEST)
                    } catch (e: IntentSender.SendIntentException) {
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    SnackbarUtil.show(activity, "Location settings not satisfied, but unable to fix the settings.")
                }
            }
        }
        return settingsResponse
    }
}
