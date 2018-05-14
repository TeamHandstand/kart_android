package us.handstand.kartwheel.fragment.race

import us.handstand.kartwheel.R
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.MapView
import us.handstand.kartwheel.activity.LocationAwareActivity
import us.handstand.kartwheel.location.UserLocation

class RaceMapFragment : Fragment() {
    lateinit private var userLocation: UserLocation

    lateinit private var mapView: MapView

    // region - Life Cycle

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userLocation = (activity as LocationAwareActivity).userLocation
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View = inflater.inflate(R.layout.fragment_race_map_race, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mapView)
    }

    //endregion
}