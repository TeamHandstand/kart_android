package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.animation.ValueAnimator
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.controller.RaceListController
import us.handstand.kartwheel.layout.RaceSummaryView
import us.handstand.kartwheel.layout.recyclerview.binding.RaceListBinding


class RaceSummaryVH private constructor(private val raceSummaryView: RaceSummaryView) : RecyclerView.ViewHolder(raceSummaryView) {
    companion object {
        fun constructNewInstance(parent: ViewGroup): RaceSummaryVH = RaceSummaryVH(RaceSummaryView(parent.context))
    }

    var raceId: String = ""
    var raceListController: RaceListController? = null

    init {
        raceSummaryView.setOnClickListener { raceListController?.onRaceItemClicked(raceId) }
    }

    fun bind(binding: RaceListBinding, valueAnimator: ValueAnimator) {
        raceId = binding.raceId
        raceSummaryView.bind(binding, valueAnimator)
    }
}