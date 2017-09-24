package us.handstand.kartwheel.fragment.race

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.HORIZONTAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.fragment_race_sign_up.*
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.LocationAwareActivity
import us.handstand.kartwheel.controller.RaceSignUpController
import us.handstand.kartwheel.controller.RaceSignUpListener
import us.handstand.kartwheel.controller.RegistrantInfo
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_ANCHOR_POINT
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_COLLAPSED
import us.handstand.kartwheel.layout.behavior.AnchoredBottomSheetBehavior.Companion.STATE_EXPANDED
import us.handstand.kartwheel.layout.recyclerview.adapter.RegistrantAvatarAdapter
import us.handstand.kartwheel.layout.runOnGlobalLayout
import us.handstand.kartwheel.layout.setCandyCaneBackground
import us.handstand.kartwheel.location.MapUtil
import us.handstand.kartwheel.location.UserLocation
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.StringUtil
import us.handstand.kartwheel.util.ThreadManager
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

// TODO: Add error callbacks when trying to join/leave a race
class RaceSignUpFragment : Fragment(), OnMapReadyCallback, RaceSignUpListener, MapUtil.MapViewHolder, View.OnClickListener {
    lateinit private var behavior: AnchoredBottomSheetBehavior<NestedScrollView>
    lateinit private var controller: RaceSignUpController
    lateinit private var scheduledCountdownFuture: ScheduledFuture<*>
    lateinit private var userLocation: UserLocation
    lateinit private var mapUtil: MapUtil
    lateinit private var mapView: MapView
    lateinit private var raceSignUpParent: View
    private val registrantAvatarAdapter = RegistrantAvatarAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        controller = RaceSignUpController(Database.get(), Storage.eventId, activity.intent.getStringExtra(RaceModel.ID), this)
        mapUtil = MapUtil(context)
        userLocation = (activity as LocationAwareActivity).userLocation
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_race_sign_up, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.mapView) // Need to keep this around for lifecycle callbacks
        raceSignUpParent = view.findViewById(R.id.raceSignUpParent)
        registrantRecyclerView.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        registrantRecyclerView.adapter = registrantAvatarAdapter
        bottomSheet.setCandyCaneBackground(android.R.color.white, R.color.textLightGrey_40p)
        // Show the race status on a timer
        scheduledCountdownFuture = ThreadManager.scheduler.scheduleWithFixedDelay({
            raceCountdown.post {
                when (controller.race?.raceStatus) {
                    Race.FINISHED -> {
                        raceCountdown.setText(R.string.finished)
                        raceCountdownTitle.visibility = View.GONE
                        scheduledCountdownFuture.cancel(true)
                    }
                    Race.REGISTRATION_CLOSED -> {
                        raceCountdown.setText(R.string.start_race)
                        raceCountdownTitle.visibility = View.GONE
                        scheduledCountdownFuture.cancel(true)
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
                mapUtil.moveToCenter(controller.race?.c(), this@RaceSignUpFragment, newState)
            }
        })
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scheduledCountdownFuture.cancel(true)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        controller.subscribe()
        batteryWarning.registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        controller.dispose()
        batteryWarning.unregisterReceiver()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        userLocation.subscribe {
            mapUtil.draw(Storage.userId, Storage.userImageUrl, it)
        }
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
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
            mapUtil.draw(race.c())
        }
    }

    override fun onTopThreeUpdated(topThree: List<User>) {
        // TODO
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.batteryWarning -> {
                if (behavior.state == STATE_COLLAPSED) {
                    behavior.state = STATE_EXPANDED
                } else if (behavior.state == STATE_EXPANDED) {
                    behavior.state = STATE_COLLAPSED
                }
            }
            R.id.signUpButton -> {
                if (controller.userInRace) {
                    API.leaveRace(controller.eventId, controller.raceId)
                } else {
                    API.joinRace(controller.eventId, controller.raceId)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        raceSignUpParent.runOnGlobalLayout {
            mapUtil.onMapReady(controller.race?.c(), googleMap, this)
        }
    }

    override val calculateCenter: Float
        get() {
            return if (raceSignUpParent.measuredHeight == 0) {
                0f
            } else {
                (raceSignUpParent.measuredHeight / 2f) - (behavior.anchorPoint / 2f)
            }
        }

    override val behaviorState: Long
        get() = behavior.state
}
