package us.handstand.kartwheel.fragment.race

import android.graphics.Bitmap
import android.location.Location
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
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import us.handstand.kartwheel.activity.LocationAwareActivity
import us.handstand.kartwheel.layout.AvatarUtil
import us.handstand.kartwheel.layout.AvatarView
import us.handstand.kartwheel.layout.GlideApp
import us.handstand.kartwheel.location.MapUtil
import us.handstand.kartwheel.location.UserLocation

class RaceMapFragment : Fragment(), OnMapReadyCallback {
    lateinit private var userLocation: UserLocation

    lateinit private var mapView: MapView

    lateinit private var mapUtil: MapUtil

    // region - Life Cycle

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userLocation = (activity as LocationAwareActivity).userLocation

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

    private fun layoutDummyIcons(googleMap: GoogleMap) {
        val avatarUtil = AvatarUtil(activity!!.applicationContext)

        GlideApp.with(context!!).asBitmap().load("http://www.aratex-group.com/wp-content/uploads/2015/01/profile.png").apply(RequestOptions().circleCrop()).
                into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(profileBitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        val markerBitmap = avatarUtil.createAvatarMarkerBitmap(profileBitmap, "attached", true, 1, true)
                        val descriptor = BitmapDescriptorFactory.fromBitmap(markerBitmap)
                        val marker = MarkerOptions()
                                .icon(descriptor)
                                .position(LatLng(0.0, 0.0))
                                .anchor(.5f, .5f)
                                .zIndex(10f)

                        googleMap.addMarker(marker)

                        val markerBitmap1 = avatarUtil.createAvatarMarkerBitmap(profileBitmap, "injured", false, 2, false)
                        val descriptor1 = BitmapDescriptorFactory.fromBitmap(markerBitmap1)
                        val marker1 = MarkerOptions()
                                .icon(descriptor1)
                                .position(LatLng(10.0, 0.0))
                                .anchor(.5f, .5f)
                                .zIndex(10f)

                        googleMap.addMarker(marker1)

                        val markerBitmap2 = avatarUtil.createAvatarMarkerBitmap(profileBitmap, "connected", false, 3, false)
                        val descriptor2 = BitmapDescriptorFactory.fromBitmap(markerBitmap2)
                        val marker2 = MarkerOptions()
                                .icon(descriptor2)
                                .position(LatLng(10.0, 10.0))
                                .anchor(.5f, .5f)
                                .zIndex(10f)

                        googleMap.addMarker(marker2)

                        val markerBitmap3 = avatarUtil.createAvatarMarkerBitmap(profileBitmap, "disconnected", false, 4, false)
                        val descriptor3 = BitmapDescriptorFactory.fromBitmap(markerBitmap3)
                        val marker3 = MarkerOptions()
                                .icon(descriptor3)
                                .position(LatLng(0.0, 10.0))
                                .anchor(.5f, .5f)
                                .zIndex(10f)

                        googleMap.addMarker(marker3)

                        mapUtil.moveToLocation(LatLng(0.0, 0.0))
                    }
                })
    }

    //endregion

    //region - OnMapReadyCallback

    override fun onMapReady(googleMap: GoogleMap) {
        layoutDummyIcons(googleMap)
    }

    //endregion
}