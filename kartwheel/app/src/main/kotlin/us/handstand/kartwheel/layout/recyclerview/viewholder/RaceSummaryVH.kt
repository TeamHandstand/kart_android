package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.layout.RaceSummaryView
import us.handstand.kartwheel.model.Race


class RaceSummaryVH private constructor(val raceSummaryView: RaceSummaryView) : RecyclerView.ViewHolder(raceSummaryView) {
    companion object {
        fun constructNewInstance(parent: ViewGroup): RaceSummaryVH {
            return RaceSummaryVH(RaceSummaryView(parent.context))
        }
    }

    fun bind(race: Race) {
        raceSummaryView.setRace(race)
    }
}