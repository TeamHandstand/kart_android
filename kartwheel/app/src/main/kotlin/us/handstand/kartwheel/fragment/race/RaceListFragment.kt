package us.handstand.kartwheel.fragment.race

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.recyclerview.adapter.RaceListAdapter
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.network.API


class RaceListFragment : Fragment() {
    lateinit var raceRecyclerView: RecyclerView
    val raceAdapter = RaceListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val raceList = inflater.inflate(R.layout.fragment_race_list, container, false) as ViewGroup
        raceRecyclerView = ViewUtil.findView(raceList, R.id.raceListRecyclerView)
        raceRecyclerView.layoutManager = LinearLayoutManager(inflater.context)
        raceRecyclerView.adapter = raceAdapter
        val raceQuery = Race.FACTORY.select_for_event_id(Storage.eventId)
        Database.get().createQuery(Race.TABLE_NAME, raceQuery.statement, *raceQuery.args)
                .mapToList { Race.FACTORY.select_for_event_idMapper().map(it) }
                .subscribe { raceAdapter.setRaces(it) }
        return raceList
    }

    override fun onResume() {
        super.onResume()
        API.getRacesWithCourses(Storage.eventId)
    }
}