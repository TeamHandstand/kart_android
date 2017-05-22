package us.handstand.kartwheel.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import rx.android.schedulers.AndroidSchedulers
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Race


class RaceSignUpActivity : AppCompatActivity(), OnMapReadyCallback {
    var map: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race_sign_up)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        val raceQuery = Race.FACTORY.select_for_id(intent.getStringExtra(Race.ID))
        Database.get().createQuery(Race.TABLE_NAME, raceQuery.statement, *raceQuery.args)
                .mapToOne { Race.FACTORY.select_for_idMapper().map(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val course = it.course()
                    if (course != null) {
                        val courseBounds = course.findCorners();
                        val courseLatLng = LatLng(courseBounds.centerLat, courseBounds.centerLong)
                        map.moveCamera(CameraUpdateFactory.newLatLng(courseLatLng))
                        map.setMinZoomPreference(16f)
                        val coursePolyline = PolylineOptions()
                        for (point in course.vertices()!!) {
                            coursePolyline.add(LatLng(point.latitude()!!, point.longitude()!!))
                        }
                        coursePolyline.color(resources.getColor(R.color.blue))
                        map.addPolyline(coursePolyline)
                    }
                }
    }
}