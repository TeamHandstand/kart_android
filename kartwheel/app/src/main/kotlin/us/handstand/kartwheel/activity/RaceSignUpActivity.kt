package us.handstand.kartwheel.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.Course
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Race


class RaceSignUpActivity : AppCompatActivity(), OnMapReadyCallback {
    var map: GoogleMap? = null
    var subscription: Subscription? = null
    var isMapExpanded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race_sign_up)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        findViewById(R.id.map).setOnClickListener { collapse() }
        findViewById(R.id.mapViewOverlay).setOnClickListener { expand() }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        val raceQuery = Race.FACTORY.select_for_id(intent.getStringExtra(Race.ID))
        subscription = Database.get().createQuery(Race.TABLE_NAME, raceQuery.statement, *raceQuery.args)
                .mapToOne { Race.FACTORY.select_for_idMapper().map(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { drawCourse(it.course(), this.map!!) }
    }

    private fun drawCourse(course: Course?, map: GoogleMap) {
        subscription?.unsubscribe()
        if (course != null) {
            val courseBounds = course.findCorners()
            val courseLatLng = LatLng(courseBounds.centerLat, courseBounds.centerLong)
            map.moveCamera(CameraUpdateFactory.newLatLng(courseLatLng))
            map.setMinZoomPreference(15f)
            val coursePolyline = PolylineOptions()
            for (point in course.vertices()!!) {
                coursePolyline.add(LatLng(point.latitude(), point.longitude()))
            }
            coursePolyline.color(resources.getColor(R.color.blue))
            map.addPolyline(coursePolyline)
            val flagOverlay = GroundOverlayOptions()
            flagOverlay.image(BitmapDescriptorFactory.fromResource(R.drawable.start_flag))
                    .position(LatLng(course.startLat(), course.startLong()), 200f, 200f)
                    .zIndex(10f)
            map.addGroundOverlay(flagOverlay)
        }

    }

    fun collapse() {
        val mapView = findViewById(R.id.map)
        val listView = findViewById(R.id.fragment)
        val initialHeight = mapView.measuredHeight
        val targetHeight = findViewById(R.id.mapViewOverlay).measuredHeight
        val animator = ValueAnimator.ofInt(initialHeight, targetHeight)
        animator.addUpdateListener {
            mapView.layoutParams.height = it.animatedValue as Int
            mapView.requestLayout()
        }

        listView.measure(MATCH_PARENT, MATCH_PARENT)
        val translationAnimator = ObjectAnimator.ofFloat(listView, "translationY", 0f)
        val set = AnimatorSet()
        set.playTogether(animator, translationAnimator)
        set.start()
        isMapExpanded = false
    }

    fun expand() {
        val mapView = findViewById(R.id.map)
        val listView = findViewById(R.id.fragment)
        val initialHeight = mapView.measuredHeight
        val targetHeight = listView.measuredHeight
        val animator = ValueAnimator.ofInt(initialHeight, targetHeight)
        animator.addUpdateListener {
            mapView.layoutParams.height = it.animatedValue as Int
            mapView.requestLayout()
        }
        val totalHeight = listView.measuredHeight.toFloat()
        val translationAnimator = ObjectAnimator.ofFloat(listView, "translationY", totalHeight)
        val set = AnimatorSet()
        set.playTogether(animator, translationAnimator)
        set.start()
        isMapExpanded = true
    }

    override fun onBackPressed() {
        if (isMapExpanded) {
            collapse()
        } else {
            super.onBackPressed()
        }
    }
}