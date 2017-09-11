package us.handstand.kartwheel.fragment.race

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.BatteryManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.HORIZONTAL
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.RaceSignUpController
import us.handstand.kartwheel.controller.RaceSignUpListener
import us.handstand.kartwheel.controller.RegistrantInfo
import us.handstand.kartwheel.layout.*
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_ANCHOR_POINT
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_COLLAPSED
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_DRAGGING
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_EXPANDED
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_SETTLING
import us.handstand.kartwheel.layout.recyclerview.adapter.RegistrantAvatarAdapter
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.StringUtil
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


// TODO: Add error callbacks when trying to join/leave a race
class RaceSignUpFragment : Fragment(), OnMapReadyCallback, View.OnClickListener, RaceSignUpListener {

    lateinit private var signUpButton: FloatingActionButton
    lateinit private var raceName: TextView
    lateinit private var raceDescription: TextView
    lateinit private var raceCountdownTitle: TextView
    lateinit private var raceCountdown: TextView
    lateinit private var spotsLeft: TextView
    lateinit private var firstTopTime: TopCourseTimeView
    lateinit private var secondTopTime: TopCourseTimeView
    lateinit private var thirdTopTime: TopCourseTimeView
    lateinit private var registrantRecyclerView: RecyclerView
    lateinit private var batteryWarning: BatteryWarningView
    lateinit private var mapView: MapView
    lateinit private var toolbar: View
    lateinit private var behavior: AnchoredBottomSheetBehavior<NestedScrollView>
    lateinit private var controller: RaceSignUpController
    lateinit private var raceSignUpParent: ViewGroup
    lateinit private var bottomSheet: NestedScrollView
    private var map: GoogleMap? = null
    private var mapInitialized = false
    private var lastBehaviorState: Long = 0

    private val registrantAvatarAdapter = RegistrantAvatarAdapter()
    private val countdownScheduler = Executors.newSingleThreadScheduledExecutor()!!
    private val batteryInfoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            batteryWarning.setBatteryPercentage(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentView = inflater.inflate(R.layout.fragment_race_sign_up, container) as ViewGroup
        raceSignUpParent = ViewUtil.findView(fragmentView, R.id.raceSignUpParent)
        signUpButton = ViewUtil.findView(fragmentView, R.id.signUpButton)
        signUpButton.setOnClickListener(this)
        batteryWarning = ViewUtil.findView(fragmentView, R.id.batteryWarning)
        batteryWarning.setOnClickListener(this)
        raceName = ViewUtil.findView(fragmentView, R.id.raceName)
        raceDescription = ViewUtil.findView(fragmentView, R.id.raceDescription)
        raceCountdown = ViewUtil.findView(fragmentView, R.id.raceCountdown)
        raceCountdownTitle = ViewUtil.findView(fragmentView, R.id.raceCountdownTitle)
        spotsLeft = ViewUtil.findView(fragmentView, R.id.spotsLeft)
        registrantRecyclerView = ViewUtil.findView(fragmentView, R.id.registrantRecyclerView)
        registrantRecyclerView.layoutManager = LinearLayoutManager(inflater.context, HORIZONTAL, false)
        registrantRecyclerView.adapter = registrantAvatarAdapter
        firstTopTime = ViewUtil.findView(fragmentView, R.id.firstTopTime)
        secondTopTime = ViewUtil.findView(fragmentView, R.id.secondTopTime)
        thirdTopTime = ViewUtil.findView(fragmentView, R.id.thirdTopTime)
        toolbar = ViewUtil.findView(fragmentView, R.id.toolbar)
        mapView = ViewUtil.findView(fragmentView, R.id.map)
        bottomSheet = ViewUtil.findView(fragmentView, R.id.bottomSheet)
        bottomSheet.setCandyCaneBackground(android.R.color.white, R.color.textLightGrey_40p)
        countdownScheduler.scheduleWithFixedDelay({
            raceCountdown.post {
                if (controller.race?.r()?.alreadyStarted() == true) {
                    raceCountdown.text = "Start The Race!"
                    raceCountdownTitle.visibility = GONE
                } else {
                    raceCountdown.text = StringUtil.hourMinSecFromMs(controller.race?.timeUntilRace)
                }
            }
        }, 0, 1L, TimeUnit.SECONDS)

        behavior = AnchoredBottomSheetBehavior.from(bottomSheet)
        behavior.state = STATE_ANCHOR_POINT
        behavior.addBottomSheetCallback(object : AnchoredBottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Long) {
                moveToCenter(controller.race?.c(), newState)
            }
        })
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        controller = RaceSignUpController(Database.get(), Storage.eventId, activity.intent.getStringExtra(RaceModel.ID), this)
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
        controller.subscribe()
        activity.registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        controller.unsubscribe()
        activity.unregisterReceiver(batteryInfoReceiver)
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    @Suppress("DEPRECATION")
    override fun onRegistrantsUpdated(registrantInfos: List<RegistrantInfo>) {
        activity.runOnUiThread {
            registrantAvatarAdapter.setRegistrantInfos(registrantInfos)
            if (controller.userInRace) {
                signUpButton.setImageResource(R.drawable.ic_clear_white_24dp)
                signUpButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red))
            } else {
                signUpButton.setImageResource(R.drawable.flag)
                signUpButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.blue))
            }
        }
    }

    override fun onRaceUpdated(race: Race.RaceWithCourse) {
        activity.runOnUiThread {
            raceName.text = race.r().name() ?: Race.DEFAULT_RACE_NAME
            val miles = (race.c()?.distance() ?: 0.0) * (race.r().totalLaps() ?: 0L)
            raceDescription.text = race.r().totalLaps().toString() + " laps | " + miles.toString().substring(0, 3) + " miles"
            spotsLeft.text = "+ ${race.r().openSpots()} Spots Available"
            registrantAvatarAdapter.openSpots = race.r().openSpots() ?: 0L
            registrantAvatarAdapter.notifyOpenSpotsChanged()
            // Draw the course in case the map was ready before we got the race
            drawCourse(race.c(), map)
        }
    }

    override fun onTopThreeUpdated(topThree: List<User>) {
        // TODO
    }

    override fun onClick(v: View) {
        if (v.id == R.id.batteryWarning) {
            if (behavior.state == STATE_COLLAPSED) {
                behavior.state = STATE_EXPANDED
            } else if (behavior.state == STATE_EXPANDED) {
                behavior.state = STATE_COLLAPSED
            }
            return
        }
        if (controller.userInRace) {
            API.leaveRace(controller.eventId, controller.raceId)
        } else {
            API.joinRace(controller.eventId, controller.raceId)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        raceSignUpParent.runOnGlobalLayout {
            drawCourse(controller.race?.c(), map)
            moveToCenter(controller.race?.c(), STATE_ANCHOR_POINT)
        }
    }

    private fun drawCourse(course: Course?, map: GoogleMap?) {
        if (mapInitialized || course == null || course.vertices() == null || map == null) {
            return
        } else {
            mapInitialized = true
        }
        // Move the camera to the course
        val courseCenter = course.findCenter()
        map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(courseCenter.longitude, courseCenter.latitude)))
        map.setMinZoomPreference(15f)
        // Draw the course
        val coursePolyline = PolylineOptions()
        course.vertices()?.forEach { coursePolyline.add(LatLng(it.latitude(), it.longitude())) }
        @Suppress("DEPRECATION")
        coursePolyline.color(resources.getColor(R.color.blue))
        map.addPolyline(coursePolyline)
        val flag = MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_flag))
                .position(LatLng(course.startLat(), course.startLong()))
                .anchor(.5f, .5f)
                .zIndex(10f)
        map.addMarker(flag)
    }

    private fun moveToCenter(course: Course?, newState: Long = behavior.state) {
        if (course != null && newState != STATE_SETTLING && newState != STATE_DRAGGING) {
            drawCourse(course, map)
            if (behavior.state == lastBehaviorState) {
                return
            }
            if (newState == STATE_ANCHOR_POINT && checkLastState(STATE_COLLAPSED)) {
                map?.animateCamera(CameraUpdateFactory.scrollBy(0f, anchorPointCenter))
            } else if (newState == STATE_COLLAPSED && checkLastState(STATE_ANCHOR_POINT)) {
                map?.animateCamera(CameraUpdateFactory.scrollBy(0f, -anchorPointCenter))
            } else {
                return
            }
            lastBehaviorState = newState
        }
    }

    private val anchorPointCenter: Float
        get() = raceSignUpParent.measuredHeight / 2f - (behavior.anchorPoint / 2f)

    private fun checkLastState(state: Long): Boolean {
        return lastBehaviorState == state || lastBehaviorState == 0L
    }
}
