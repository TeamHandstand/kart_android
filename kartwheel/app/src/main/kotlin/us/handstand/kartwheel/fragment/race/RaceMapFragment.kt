package us.handstand.kartwheel.fragment.race

import android.graphics.Bitmap
import us.handstand.kartwheel.R
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import us.handstand.kartwheel.interfaces.RaceFragmentInterface
import us.handstand.kartwheel.layout.AvatarUtil
import us.handstand.kartwheel.layout.GlideApp
import us.handstand.kartwheel.location.MapUtil
import us.handstand.kartwheel.location.UserLocation
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.UserRaceInfo

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

    //region - Private

    // TODO: Remove this. Including for now for visualzation purposes
    private fun layoutDummyIcons(googleMap: GoogleMap) {
        val avatarUtil = AvatarUtil(activity!!.applicationContext)

        GlideApp.with(context!!).asBitmap().load("http://www.aratex-group.com/wp-content/uploads/2015/01/profile.png").apply(RequestOptions().circleCrop()).
                into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(profileBitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        val markerBitmap = avatarUtil.createAvatarMarkerBitmap(
                                profileBitmap,
                                Storage.userId,
                                UserRaceInfo.UserState.ATTACHED,
                                true,
                                1,
                                true)

                        val descriptor = BitmapDescriptorFactory.fromBitmap(markerBitmap)
                        val marker = MarkerOptions()
                                .icon(descriptor)
                                .position(LatLng(0.0, 0.0))
                                .anchor(.5f, .5f)
                                .zIndex(10f)

                        googleMap.addMarker(marker)

                        val markerBitmap1 = avatarUtil.createAvatarMarkerBitmap(
                                profileBitmap,
                                "",
                                UserRaceInfo.UserState.INJURED,
                                false,
                                2,
                                false)
                        val descriptor1 = BitmapDescriptorFactory.fromBitmap(markerBitmap1)
                        val marker1 = MarkerOptions()
                                .icon(descriptor1)
                                .position(LatLng(10.0, 0.0))
                                .anchor(.5f, .5f)
                                .zIndex(10f)

                        googleMap.addMarker(marker1)

                        val markerBitmap2 = avatarUtil.createAvatarMarkerBitmap(
                                profileBitmap,
                                "",
                                UserRaceInfo.UserState.DETACHED,
                                false,
                                3,
                                false)
                        val descriptor2 = BitmapDescriptorFactory.fromBitmap(markerBitmap2)
                        val marker2 = MarkerOptions()
                                .icon(descriptor2)
                                .position(LatLng(10.0, 10.0))
                                .anchor(.5f, .5f)
                                .zIndex(10f)

                        googleMap.addMarker(marker2)

                        val markerBitmap3 = avatarUtil.createAvatarMarkerBitmap(
                                profileBitmap,
                                "",
                                UserRaceInfo.UserState.DISCONNECTED,
                                false,
                                4,
                                false)
                        val descriptor3 = BitmapDescriptorFactory.fromBitmap(markerBitmap3)
                        val marker3 = MarkerOptions()
                                .icon(descriptor3)
                                .position(LatLng(0.0, 10.0))
                                .anchor(.5f, .5f)
                                .zIndex(10f)

                        googleMap.addMarker(marker3)

                        val itemZone1Bitmap = avatarUtil.createItemZoneBitmap(0)
                        val itemZone1Descriptor = BitmapDescriptorFactory.fromBitmap(itemZone1Bitmap)
                        val itemMarker1 = MarkerOptions()
                                .icon(itemZone1Descriptor)
                                .position(LatLng(0.0, -5.0))
                                .anchor(.5f, .5f)
                                .zIndex(10f)
                        googleMap.addMarker(itemMarker1)

                        val itemZone2Bitmap = avatarUtil.createItemZoneBitmap(3)
                        val itemZone2Descriptor = BitmapDescriptorFactory.fromBitmap(itemZone2Bitmap)
                        val itemMarker2 = MarkerOptions()
                                .icon(itemZone2Descriptor)
                                .position(LatLng(0.0, 15.0))
                                .anchor(.5f, .5f)
                                .zIndex(10f)
                        googleMap.addMarker(itemMarker2)

                    }
                })
    }

    //endregion

    //region - OnMapReadyCallback

    override fun onMapReady(googleMap: GoogleMap) {
        // On map Ready, pass in params
        layoutDummyIcons(googleMap)
    }

    //endregion
}