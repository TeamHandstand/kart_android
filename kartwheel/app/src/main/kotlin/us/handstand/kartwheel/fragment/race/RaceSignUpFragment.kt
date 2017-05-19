package us.handstand.kartwheel.fragment.race

import android.os.Bundle
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
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.TopCourseTimeView
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.recyclerview.adapter.RegistrantAvatarAdapter
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.util.StringUtil
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class RaceSignUpFragment : Fragment() {
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
    var registrantSubscription: Subscription? = null
    val registrantAvatarAdapter = RegistrantAvatarAdapter()
    val countdownScheduler = Executors.newSingleThreadScheduledExecutor()!!
    var race: Race? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentView = inflater.inflate(R.layout.fragment_race_sign_up, container, false) as ViewGroup
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
        // Get Race
        val query = Race.FACTORY.select_for_id(activity.intent.getStringExtra(Race.ID))
        raceSubscription = Database.get().createQuery(Race.TABLE_NAME, query.statement, *query.args)
                .mapToOne { Race.FACTORY.select_for_idMapper().map(it) }
                .subscribe { onRaceUpdated(it) }
    }

    override fun onPause() {
        super.onPause()
        raceSubscription?.unsubscribe()
        registrantSubscription?.unsubscribe()
    }

    fun onRaceUpdated(race: Race?) {
        raceName.text = race?.name() ?: Race.DEFAULT_RACE_NAME
        val miles = (race?.course()?.distance() ?: 0.0) * (race?.totalLaps() ?: 0L)
        raceDescription.text = race?.totalLaps().toString() + " laps | " + miles.toString().substring(0, 3) + " miles"
        spotsLeft.text = "+" + (race?.openSpots() ?: 0).toString() + " Spots Available"
        registrantAvatarAdapter.openSpots = (race?.openSpots() ?: 0L)
        // TODO: This is wrong. Just wanted to see a list populated
        registrantAvatarAdapter.setRegistrants(Collections.emptyList())
        this.race = race
        if (registrantSubscription?.isUnsubscribed != true) {
            val query = User.FACTORY.select_for_race_id(race?.id())
            registrantSubscription = Database.get().createQuery(User.TABLE_NAME, query.statement, *query.args)
                    .mapToList({ User.FACTORY.select_for_race_idMapper().map(it) })
                    .subscribe { registrantAvatarAdapter.setRegistrants(it) }
        }
    }
}