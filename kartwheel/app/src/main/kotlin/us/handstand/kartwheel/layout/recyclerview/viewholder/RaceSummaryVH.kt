package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.controller.RaceListController
import us.handstand.kartwheel.layout.RaceSummaryView
import us.handstand.kartwheel.model.Race


class RaceSummaryVH private constructor(private val raceSummaryView: RaceSummaryView) : RecyclerView.ViewHolder(raceSummaryView) {
    companion object {
        fun constructNewInstance(parent: ViewGroup): RaceSummaryVH = RaceSummaryVH(RaceSummaryView(parent.context))
    }

    var raceId: String = ""
    var raceListController: RaceListController? = null

    init {
        raceSummaryView.setOnClickListener { raceListController?.onRaceItemClicked(raceId) }
    }

    fun bind(race: Race.RaceWithCourse) {
        raceId = race.r().id()
        raceSummaryView.setRace(race)
    }
}