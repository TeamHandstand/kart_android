package us.handstand.kartwheel.layout.recyclerview.adapter

import android.animation.ValueAnimator
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import us.handstand.kartwheel.controller.RaceListController
import us.handstand.kartwheel.layout.recyclerview.binding.RaceListBinding
import us.handstand.kartwheel.layout.recyclerview.viewholder.RaceSummaryVH


class RaceListAdapter(val controller: RaceListController) : RecyclerView.Adapter<RaceSummaryVH>() {
    val races = ArrayList<RaceListBinding>()
    private val timeAnimator = ValueAnimator.ofFloat(0f, 1f)

    init {
        timeAnimator.interpolator = LinearInterpolator()
    }

    fun setBindings(races: List<RaceListBinding>) {
        this.races.clear()
        this.races.addAll(races)
        timeAnimator.cancel()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceSummaryVH {
        val rvh = RaceSummaryVH.constructNewInstance(parent)
        rvh.raceListController = controller
        return rvh
    }

    override fun onBindViewHolder(holder: RaceSummaryVH, position: Int) {
        holder.bind(races[position], timeAnimator)
    }

    override fun getItemCount(): Int = races.size

}
