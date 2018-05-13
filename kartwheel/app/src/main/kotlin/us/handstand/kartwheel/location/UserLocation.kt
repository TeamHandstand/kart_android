package us.handstand.kartwheel.location


import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import us.handstand.kartwheel.activity.LocationAwareActivity
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.UserRaceInfo
import us.handstand.kartwheel.model.UserRaceInfoModel
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.Permissions
import us.handstand.kartwheel.util.SnackbarUtil

class UserLocation(val activity: LocationAwareActivity) : Observable<Location>() {
    private val publisher = PublishSubject.create<Location>()
    private val locationProvider = FusedLocationProviderClient(activity)
    private val userId = Storage.userId

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
//        publisher.onNext(location)
        val query = UserRaceInfo.FACTORY.select_for_id(userId)
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

    /**
     *  Asynchronously request location updates via the GPS provider.
     *
     *  May or may not succeed depending on device settings.
     *
     *  @see Permissions.checkDeviceLocationSettings()
     */
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        // Check location permissions from our manifest
        if (Permissions.hasLocationPermissions(activity)) {
            // If the device's location settings aren't high enough, then the user is redirected to them.
            // This class only handles the success case
            Permissions.checkDeviceLocationSettings(activity)
                    .addOnCompleteListener {
                        // We must get the last location before requesting location updates
                        // if we don't want to depend on another application storing the location in
                        // the system cache.
                        locationProvider.lastLocation
                                .addOnSuccessListener {
                                    publishLocation(it)
                                }
                                .addOnFailureListener {
                                    SnackbarUtil.show(activity, "Unable to get last location")
                                }
                        locationProvider.requestLocationUpdates(Permissions.locationRequest, locationCallbacks, Looper.getMainLooper())
                    }
        } else {
            Permissions.requestLocationPermissions(activity)
        }
    }
}
