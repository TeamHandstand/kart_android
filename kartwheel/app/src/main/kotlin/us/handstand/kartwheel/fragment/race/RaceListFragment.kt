package us.handstand.kartwheel.fragment.race

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rx.Subscription
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.RaceSignUpActivity
import us.handstand.kartwheel.controller.RaceListController
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.recyclerview.adapter.RaceListAdapter
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.network.API


class RaceListFragment : Fragment(), RaceListController.Companion.StartFragmentInterface {
    lateinit var raceRecyclerView: RecyclerView

    val raceAdapter = RaceListAdapter()
    val raceListController = RaceListController()
    var raceSubscription: Subscription? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val raceList = inflater.inflate(R.layout.fragment_race_list, container, false) as ViewGroup
        raceRecyclerView = ViewUtil.findView(raceList, R.id.raceListRecyclerView)
        raceRecyclerView.layoutManager = LinearLayoutManager(inflater.context)
        raceRecyclerView.adapter = raceAdapter
        raceAdapter.controller = raceListController
        return raceList
    }

    override fun onResume() {
        super.onResume()
        raceListController.fragmentInterface = this
        API.getRacesWithCourses(Storage.eventId)
        val raceQuery = Race.FACTORY.select_for_event_id(Storage.eventId)
        raceSubscription = Database.get().createQuery(RaceModel.TABLE_NAME, raceQuery.statement, *raceQuery.args)
                .mapToList { Race.FACTORY.select_for_event_idMapper().map(it) }
                .subscribe { raceAdapter.setRaces(it) }
    }

    override fun onPause() {
        super.onPause()
        raceListController.fragmentInterface = null
        raceSubscription?.unsubscribe()
    }

    override fun showRaceSignUp(raceId: String) {
        val intent = Intent(activity, RaceSignUpActivity::class.java)
        intent.putExtra(RaceModel.ID, raceId)
        activity.startActivity(intent)
    }
}