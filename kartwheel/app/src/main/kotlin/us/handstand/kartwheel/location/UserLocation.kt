package us.handstand.kartwheel.location


import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

object UserLocation : LocationListener {

    private var locationManager: LocationManager? = null

    fun init(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun requestLocationUpdates() = locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this)

    fun ignoreLocationUpdates() = locationManager?.removeUpdates(this)

    fun getLocation() = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

    override fun onLocationChanged(location: Location) {

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }
}
