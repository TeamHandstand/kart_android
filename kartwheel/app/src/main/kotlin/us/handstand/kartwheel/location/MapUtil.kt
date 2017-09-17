package us.handstand.kartwheel.location

import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_ANCHOR_POINT
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_COLLAPSED
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_DRAGGING
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_SETTLING
import us.handstand.kartwheel.model.Course


object MapUtil {
    @ColorInt private var courseColor: Int = 0
    private lateinit var flagIcon: BitmapDescriptor

    fun initialize(@ColorInt color: Int, @DrawableRes flagRes: Int) {
        courseColor = color
        flagIcon = BitmapDescriptorFactory.fromResource(flagRes)
    }

    fun draw(course: Course?, map: GoogleMap?): Boolean {
        if (course?.vertices() == null || map == null) {
            return false
        }
        // Move the camera to the course
        val courseCenter = course.findCenter()
        map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(courseCenter.longitude, courseCenter.latitude)))
        map.setMinZoomPreference(15f)
        // Draw the course
        val coursePolyline = PolylineOptions()
        course.vertices()?.forEach { coursePolyline.add(LatLng(it.latitude(), it.longitude())) }
        coursePolyline.color(courseColor)
        map.addPolyline(coursePolyline)
        val flag = MarkerOptions()
                .icon(flagIcon)
                .position(LatLng(course.startLat(), course.startLong()))
                .anchor(.5f, .5f)
                .zIndex(10f)
        map.addMarker(flag)
        return true
    }

    fun moveToCenter(course: Course?, map: GoogleMap?, center: Float, currentState: Long, newState: Long, oldState: Long): Long {
        if (course == null || newState == STATE_SETTLING || newState == STATE_DRAGGING || currentState == oldState) {
            return oldState
        }
        draw(course, map)
        when (newState) {
            STATE_ANCHOR_POINT -> {
                if (checkLastState(STATE_COLLAPSED, oldState)) {
                    map?.animateCamera(CameraUpdateFactory.scrollBy(0f, center))
                    return newState
                }
            }
            STATE_COLLAPSED -> {
                if (checkLastState(STATE_ANCHOR_POINT, oldState)) {
                    map?.animateCamera(CameraUpdateFactory.scrollBy(0f, -center))
                    return newState
                }
            }
        }
        return oldState
    }

    private fun checkLastState(newState: Long, oldState: Long): Boolean = newState == newState || oldState == 0L

    fun <T : View> getCenter(parent: View, behavior: AnchoredBottomSheetBehavior<T>): Float = parent.measuredHeight / 2f - (behavior.anchorPoint / 2f)
}