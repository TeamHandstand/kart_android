package us.handstand.kartwheel.activity


import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import us.handstand.kartwheel.location.UserLocation
import us.handstand.kartwheel.util.SnackbarUtil

open class LocationAwareActivity : FragmentActivity(), GoogleApiClient.OnConnectionFailedListener {
    private lateinit var googleApiClient: GoogleApiClient
    @Suppress("unused") lateinit var userLocation: UserLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build()
        userLocation = UserLocation(this)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        SnackbarUtil.show(this, "Unable to get location services from Google. Please contact support")
    }
}
