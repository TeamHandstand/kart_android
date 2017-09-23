package us.handstand.kartwheel.location


import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Location
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import com.google.android.gms.location.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.UserRaceInfo
import us.handstand.kartwheel.network.API

class UserLocation(val context: Context) : Observable<Location>() {
    private var publisher = PublishSubject.create<Location>()
    private val locationProvider = FusedLocationProviderClient(context)

    private val locationCallbacks = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            publisher.onNext(result.lastLocation)
            val query = UserRaceInfo.FACTORY.select_for_id(Storage.userId)
            Database.get().query(query.statement, *query.args).use {
                val userRaceInfo = UserRaceInfo.FACTORY.select_for_idMapper().map(it)
                API.updateLocation(Storage.eventId, Storage.raceId, userRaceInfo.id(), result.lastLocation)
            }
        }

        override fun onLocationAvailability(availability: LocationAvailability) {
            super.onLocationAvailability(availability)
            if (availability.isLocationAvailable) {
            }
        }
    }

    override fun subscribeActual(observer: Observer<in Location>) {
        publisher.subscribe(observer)
        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            locationProvider.requestLocationUpdates(LocationRequest().setInterval(1000), locationCallbacks, Looper.getMainLooper())
        }
    }

    fun dispose() {
        locationProvider.removeLocationUpdates(locationCallbacks)
    }
}
