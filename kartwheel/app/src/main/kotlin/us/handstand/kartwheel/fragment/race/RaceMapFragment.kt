package us.handstand.kartwheel.fragment.race

import us.handstand.kartwheel.R
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import us.handstand.kartwheel.interfaces.RaceFragmentInterface
import us.handstand.kartwheel.location.MapUtil
import us.handstand.kartwheel.location.UserLocation

// TODO: Restructure to reduce boilerplate code also in RaceSignUpFragment
class RaceMapFragment : Fragment(), OnMapReadyCallback {
    lateinit private var userLocation: UserLocation

    lateinit private var mapView: MapView

    lateinit private var mapUtil: MapUtil

    lateinit private var listener: RaceFragmentInterface

    companion object {
        fun newInstance(listener: RaceFragmentInterface): RaceMapFragment {
            val raceMapFragment = RaceMapFragment()
            raceMapFragment.listener = listener
            return raceMapFragment
        }
    }

    // region - Life Cycle

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userLocation = listener.getLocation()

        // Temporary
        mapUtil = MapUtil(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_race_map_race, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()

        mapView.onStart()
        userLocation.subscribe {
            // TODO: Update location
        }
    }

    override fun onPause() {
        mapView.onPause()

        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()

        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()

        mapView.onLowMemory()
    }

    //endregion

    //region - OnMapReadyCallback

    override fun onMapReady(googleMap: GoogleMap) {
        // TODO: Implement
    }

    //endregion
}