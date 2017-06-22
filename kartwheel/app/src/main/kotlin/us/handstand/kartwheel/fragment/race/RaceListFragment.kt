package us.handstand.kartwheel.fragment.race

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.RaceSignUpActivity
import us.handstand.kartwheel.controller.RaceListController
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.recyclerview.adapter.RaceListAdapter
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.RaceModel
import javax.inject.Inject


class RaceListFragment : Fragment(), RaceListController.RaceListListener {
    @Inject lateinit var controller: RaceListController
    private lateinit var raceRecyclerView: RecyclerView
    private lateinit var raceAdapter: RaceListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        KartWheel.injector.inject(this)
        val raceList = inflater.inflate(R.layout.fragment_race_list, container, false) as ViewGroup
        raceRecyclerView = ViewUtil.findView(raceList, R.id.raceListRecyclerView)
        raceRecyclerView.layoutManager = LinearLayoutManager(inflater.context)
        raceAdapter = RaceListAdapter(controller)
        raceRecyclerView.adapter = raceAdapter
        return raceList
    }

    override fun onResume() {
        super.onResume()
        controller.subscribe(this)
    }

    override fun onPause() {
        super.onPause()
        controller.unsubscribe()
    }

    override fun onRaceItemClicked(raceId: String) {
        val intent = Intent(activity, RaceSignUpActivity::class.java)
        intent.putExtra(RaceModel.ID, raceId)
        activity.startActivity(intent)
    }

    override fun onRacesUpdated(races: List<Race>) {
        activity.runOnUiThread { raceAdapter.setRaces(races) }
    }
}