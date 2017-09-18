package us.handstand.kartwheel.location


import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.UserRaceInfo
import us.handstand.kartwheel.network.API

class UserLocation(context: Context) : LocationListener {
    private var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Throws(SecurityException::class) fun requestLocationUpdates() = locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this)

    fun ignoreLocationUpdates() = locationManager.removeUpdates(this)

    @Throws(SecurityException::class) fun getLocation() = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

    override fun onLocationChanged(location: Location) {
        val query = UserRaceInfo.FACTORY.select_for_id(Storage.userId)
        Database.get().query(query.statement, *query.args).use {
            val userRaceInfo = UserRaceInfo.FACTORY.select_for_idMapper().map(it)
            API.updateLocation(Storage.eventId, Storage.raceId, userRaceInfo.id(), location)
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }
}
