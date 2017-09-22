package us.handstand.kartwheel.location

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.support.annotation.ColorInt
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_ANCHOR_POINT
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_COLLAPSED
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_DRAGGING
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_SETTLING
import us.handstand.kartwheel.model.Course


class MapUtil(context: Context) {
    @ColorInt private var courseColor: Int = context.resources.getColor(R.color.blue)
    private lateinit var flagIcon: BitmapDescriptor
    private val glide = Glide.with(context)
    private var googleMap: GoogleMap? = null
    private var markerMap = mutableMapOf<String, Marker>()
    private var mapInitialized = false
    private var oldState: Long = 0

    interface MapViewHolder {
        val calculateCenter: Float
        val behaviorState: Long
    }

    fun draw(course: Course?) {
        if (mapInitialized || course?.vertices() == null || googleMap == null) {
            return
        }
        // Move the camera to the course
        val courseCenter = course.findCenter()
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(courseCenter.longitude, courseCenter.latitude)))
        googleMap?.setMinZoomPreference(15f)
        // Draw the course
        val coursePolyline = PolylineOptions()
        course.vertices()?.forEach { coursePolyline.add(LatLng(it.latitude(), it.longitude())) }
        coursePolyline.color(courseColor)
        googleMap?.addPolyline(coursePolyline)
        val flag = MarkerOptions()
                .icon(flagIcon)
                .position(LatLng(course.startLat(), course.startLong()))
                .anchor(.5f, .5f)
                .zIndex(10f)
        googleMap?.addMarker(flag)
        mapInitialized = true
    }

    fun draw(userId: String, buddyUrl: String, location: Location) {
        if (markerMap.containsKey(userId)) {
            markerMap[userId]?.position = LatLng(location.longitude, location.latitude)
        } else {
            glide.load(buddyUrl)
                    .asBitmap()
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                            val flag = MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                    .position(LatLng(location.longitude, location.latitude))
                                    .anchor(.5f, .5f)
                                    .zIndex(10f)
                            markerMap.put(userId, googleMap?.addMarker(flag)!!)
                        }
                    })
        }
    }

    fun moveToCenter(course: Course?, mapViewHolder: MapViewHolder, newState: Long) {
        if (course == null || newState == STATE_SETTLING || newState == STATE_DRAGGING) {
            return
        }
        draw(course)
        if (true) {//mapViewHolder.behaviorState == oldState) {
            return
        }
        val center = mapViewHolder.calculateCenter
        when (newState) {
            STATE_ANCHOR_POINT -> {
                if (isOldState(STATE_COLLAPSED)) {
                    googleMap?.animateCamera(CameraUpdateFactory.scrollBy(0f, center))
                }
            }
            STATE_COLLAPSED -> {
                if (isOldState(STATE_ANCHOR_POINT)) {
                    googleMap?.animateCamera(CameraUpdateFactory.scrollBy(0f, -center))
                }
            }
            else -> return
        }
        oldState = newState
    }

    private fun isOldState(state: Long): Boolean = state == oldState || oldState == 0L

    fun onMapReady(course: Course?, googleMap: GoogleMap, mapViewHolder: MapViewHolder) {
        this.googleMap = googleMap
        flagIcon = BitmapDescriptorFactory.fromResource(R.drawable.start_flag)
        draw(course)
        moveToCenter(course, mapViewHolder, STATE_ANCHOR_POINT)
    }
}