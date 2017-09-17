package us.handstand.kartwheel.location

import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_ANCHOR_POINT
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_COLLAPSED
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_DRAGGING
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_SETTLING
import us.handstand.kartwheel.model.Course


class MapUtil {
    @ColorInt private var courseColor: Int = 0
    private lateinit var flagIcon: BitmapDescriptor
    private var googleMap: GoogleMap? = null
    private var mapInitialized = false
    private var oldState: Long = 0

    fun initialize(@ColorInt color: Int, @DrawableRes flagRes: Int) {
        courseColor = color
        flagIcon = BitmapDescriptorFactory.fromResource(flagRes)
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

    fun moveToCenter(course: Course?, center: Float, currentState: Long, newState: Long) {
        if (course == null || newState == STATE_SETTLING || newState == STATE_DRAGGING || currentState == oldState) {
            return
        }
        draw(course)
        when (newState) {
            STATE_ANCHOR_POINT -> {
                if (checkLastState(STATE_COLLAPSED, oldState)) {
                    googleMap?.animateCamera(CameraUpdateFactory.scrollBy(0f, center))
                    oldState = newState
                }
            }
            STATE_COLLAPSED -> {
                if (checkLastState(STATE_ANCHOR_POINT, oldState)) {
                    googleMap?.animateCamera(CameraUpdateFactory.scrollBy(0f, -center))
                    oldState = newState
                }
            }
        }
    }

    private fun checkLastState(newState: Long, oldState: Long): Boolean = newState == newState || oldState == 0L

    fun onMapReady(course: Course?, googleMap: GoogleMap?, center: Float, currentState: Long) {
        this.googleMap = googleMap
        draw(course)
        moveToCenter(course, center, currentState, STATE_ANCHOR_POINT)
    }
}