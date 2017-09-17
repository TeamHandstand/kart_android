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
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.RaceSignUpController
import us.handstand.kartwheel.controller.RaceSignUpListener
import us.handstand.kartwheel.controller.RegistrantInfo
import us.handstand.kartwheel.layout.BatteryWarningView
import us.handstand.kartwheel.layout.TopCourseTimeView
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_ANCHOR_POINT
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_COLLAPSED
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_EXPANDED
import us.handstand.kartwheel.layout.recyclerview.adapter.RegistrantAvatarAdapter
import us.handstand.kartwheel.layout.setCandyCaneBackground
import us.handstand.kartwheel.location.MapUtil
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.StringUtil
import us.handstand.kartwheel.util.ThreadManager
import java.util.concurrent.TimeUnit


// TODO: Add error callbacks when trying to join/leave a race
class RaceSignUpFragment : Fragment(), OnMapReadyCallback, RaceSignUpListener {

    @BindView(R.id.signUpButton) lateinit var signUpButton: FloatingActionButton
    @BindView(R.id.batteryWarning) lateinit var raceName: TextView
    @BindView(R.id.raceDescription) lateinit var raceDescription: TextView
    @BindView(R.id.raceCountdownTitle) lateinit var raceCountdownTitle: TextView
    @BindView(R.id.raceCountdown) lateinit var raceCountdown: TextView
    @BindView(R.id.spotsLeft) lateinit var spotsLeft: TextView
    @BindView(R.id.firstTopTime) lateinit var firstTopTime: TopCourseTimeView
    @BindView(R.id.secondTopTime) lateinit var secondTopTime: TopCourseTimeView
    @BindView(R.id.thirdTopTime) lateinit var thirdTopTime: TopCourseTimeView
    @BindView(R.id.registrantRecyclerView) lateinit var registrantRecyclerView: RecyclerView
    @BindView(R.id.batteryWarning) lateinit var batteryWarning: BatteryWarningView
    @BindView(R.id.map) lateinit var mapView: MapView
    @BindView(R.id.toolbar) lateinit var toolbar: View
    @BindView(R.id.raceSignUpParent) lateinit var raceSignUpParent: ViewGroup
    @BindView(R.id.bottomSheet) lateinit var bottomSheet: NestedScrollView
    lateinit private var unbinder: Unbinder
    lateinit private var behavior: AnchoredBottomSheetBehavior<NestedScrollView>
    lateinit private var controller: RaceSignUpController
    private val map = MapUtil()

    private val registrantAvatarAdapter = RegistrantAvatarAdapter()
    private val batteryInfoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            batteryWarning.setBatteryPercentage(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentView = inflater.inflate(R.layout.fragment_race_sign_up, container) as ViewGroup
        unbinder = ButterKnife.bind(this, fragmentView)
        registrantRecyclerView.layoutManager = LinearLayoutManager(inflater.context, HORIZONTAL, false)
        registrantRecyclerView.adapter = registrantAvatarAdapter
        bottomSheet.setCandyCaneBackground(android.R.color.white, R.color.textLightGrey_40p)
        ThreadManager.scheduler.scheduleWithFixedDelay({
            raceCountdown.post {
                when (controller.race?.raceStatus) {
                    Race.FINISHED -> {
                        raceCountdown.setText(R.string.finished)
                        raceCountdownTitle.visibility = GONE
                    }
                    Race.REGISTRATION_CLOSED -> {
                        raceCountdown.setText(R.string.start_race)
                        raceCountdownTitle.visibility = GONE
                    }
                    else -> raceCountdown.text = StringUtil.hourMinSecFromMs(controller.race?.timeUntilRace)
                }
            }
        }, 0, 1L, TimeUnit.SECONDS)

        behavior = AnchoredBottomSheetBehavior.from(bottomSheet)
        behavior.state = STATE_ANCHOR_POINT
        behavior.addBottomSheetCallback(object : AnchoredBottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Long) {
                map.moveToCenter(controller.race?.c(), ViewUtil.getCenterOfAnchor(raceSignUpParent, behavior), behavior.state, newState)
            }
        })
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return fragmentView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
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

    @Suppress("DEPRECATION") override fun onRegistrantsUpdated(registrantInfos: List<RegistrantInfo>) {
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
            raceDescription.text = context.getString(R.string.race_details, race.r().totalLaps(), miles)
            spotsLeft.text = context.getString(R.string.spots_available, race.r().openSpots())
            registrantAvatarAdapter.openSpots = race.r().openSpots() ?: 0L
            registrantAvatarAdapter.notifyOpenSpotsChanged()
            // Draw the course in case the map was ready before we got the race
            map.draw(race.c())
        }
    }

    override fun onTopThreeUpdated(topThree: List<User>) {
        // TODO
    }

    @OnClick(R.id.batteryWarning) fun onBatteryWarning() {
        if (behavior.state == STATE_COLLAPSED) {
            behavior.state = STATE_EXPANDED
        } else if (behavior.state == STATE_EXPANDED) {
            behavior.state = STATE_COLLAPSED
        }
    }

    @OnClick(R.id.signUpButton) fun onSignUp() {
        if (controller.userInRace) {
            API.leaveRace(controller.eventId, controller.raceId)
        } else {
            API.joinRace(controller.eventId, controller.raceId)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map.onMapReady(controller.race?.c(), googleMap, ViewUtil.getCenterOfAnchor(raceSignUpParent, behavior), behavior.state)
    }
}
