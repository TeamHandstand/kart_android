package us.handstand.kartwheel.location


import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.network.API

class UserLocation : LocationListener {

    private var locationManager: LocationManager? = null

    fun init(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Throws(SecurityException::class) fun requestLocationUpdates() = locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this)

    fun ignoreLocationUpdates() = locationManager?.removeUpdates(this)

    @Throws(SecurityException::class) fun getLocation() = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

    override fun onLocationChanged(location: Location) {
        API.updateLocation(Storage.eventId, Storage.raceId, "", location)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }
}
