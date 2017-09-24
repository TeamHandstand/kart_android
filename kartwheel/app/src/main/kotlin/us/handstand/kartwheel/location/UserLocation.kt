package us.handstand.kartwheel.location


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.UserRaceInfo
import us.handstand.kartwheel.model.UserRaceInfoModel
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.Permissions

class UserLocation(val context: Context) : Observable<Location>() {
    private var publisher = PublishSubject.create<Location>()
    private val locationProvider = FusedLocationProviderClient(context)

    private val locationCallbacks = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            publishLocation(result.lastLocation)
        }
    }

    private fun publishLocation(location: Location) {
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
        if (Permissions.hasLocationPermissions(context)) {
            locationProvider.requestLocationUpdates(LocationRequest().setInterval(1000), locationCallbacks, Looper.getMainLooper())
        }
    }
}
