package us.handstand.kartwheel.fragment.race

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.HORIZONTAL
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.TopCourseTimeView
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.recyclerview.adapter.RegistrantAvatarAdapter
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.UserRaceInfo
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.StringUtil
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class RaceSignUpFragment : Fragment(), View.OnClickListener {
    lateinit var signUpButton: FloatingActionButton
    lateinit var raceName: TextView
    lateinit var raceDescription: TextView
    lateinit var raceCountdownTitle: TextView
    lateinit var raceCountdown: TextView
    lateinit var spotsLeft: TextView
    lateinit var firstTopTime: TopCourseTimeView
    lateinit var secondTopTime: TopCourseTimeView
    lateinit var thirdTopTime: TopCourseTimeView
    lateinit var registrantRecyclerView: RecyclerView
    var raceSubscription: Subscription? = null
    var participantSubscription: Subscription? = null
    val registrantAvatarAdapter = RegistrantAvatarAdapter()
    val countdownScheduler = Executors.newSingleThreadScheduledExecutor()!!
    var race: Race? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentView = inflater.inflate(R.layout.fragment_race_sign_up, container, false) as ViewGroup
        signUpButton = ViewUtil.findView(fragmentView, R.id.signUpButton)
        signUpButton.setOnClickListener(this)
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
        countdownScheduler.scheduleWithFixedDelay({
            raceCountdown.post {
                if (race?.alreadyStarted() == true) {
                    raceCountdown.text = "Start The Race!"
                    raceCountdownTitle.visibility = GONE
                } else {
                    raceCountdown.text = StringUtil.hourMinSecFromMs(race?.timeUntilRace)
                }
            }
        }, 0, 1L, TimeUnit.SECONDS)
        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        val raceId = activity.intent.getStringExtra(Race.ID)
        // Get Race and the participants from the network and from the Database
        API.getRaceParticipants(Storage.eventId, raceId)
        val raceQuery = Race.FACTORY.select_for_id(raceId)
        raceSubscription = Database.get().createQuery(Race.TABLE_NAME, raceQuery.statement, *raceQuery.args)
                .mapToOne { Race.FACTORY.select_for_idMapper().map(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onRaceUpdated(it) }
//        val participantQuery = User.FACTORY.select_for_race_id(raceId)
//        participantSubscription = Database.get().createQuery(User.TABLE_NAME, participantQuery.statement, *participantQuery.args)
//                .mapToList({ User.FACTORY.select_for_race_idMapper().map(it) })
//                .subscribe {
//                    Race.update(Database.get(), it, raceId)
//                    registrantAvatarAdapter.setRegistrants(it)
//                }
    }

    override fun onPause() {
        super.onPause()
        raceSubscription?.unsubscribe()
        participantSubscription?.unsubscribe()
    }

    override fun onDestroy() {
        raceSubscription?.unsubscribe()
        participantSubscription?.unsubscribe()
        super.onDestroy()
    }

    fun onRaceUpdated(race: Race?) {
        raceName.text = race?.name() ?: Race.DEFAULT_RACE_NAME
        val miles = (race?.course()?.distance() ?: 0.0) * (race?.totalLaps() ?: 0L)
        raceDescription.text = race?.totalLaps().toString() + " laps | " + miles.toString().substring(0, 3) + " miles"
        spotsLeft.text = "+" + ((race?.course()?.maxRegistrants() ?: 0) - (race?.registrantIds()?.size ?: 0)).toString() + " Spots Available"
        registrantAvatarAdapter.maxRegistrants = (race?.course()?.maxRegistrants() ?: 0L)
        registrantAvatarAdapter.setRegistrants(race?.registrantImageUrls()!!)
        if (race?.registrantIds()?.contains(Storage.userId) == true) {
            signUpButton.setImageResource(R.drawable.ic_clear_white_24dp)
            signUpButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red))
        } else {
            signUpButton.setImageResource(R.drawable.flag)
            signUpButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.blue))
        }
        this.race = race
    }

    override fun onClick(v: View?) {
        val raceId = activity.intent.getStringExtra(Race.ID)
        if (race?.registrantIds()?.contains(Storage.userId) == true) {
            API.leaveRace(Storage.eventId, raceId, object : API.APICallback<Boolean> {
                override fun onSuccess(response: Boolean) {
                    API.getRaceParticipants(Storage.eventId, raceId)
                }
            })
        } else {
            API.joinRace(Storage.eventId, raceId, object : API.APICallback<UserRaceInfo> {
                override fun onSuccess(response: UserRaceInfo) {
                    API.getRaceParticipants(Storage.eventId, raceId)
                }
            })
        }
    }
}