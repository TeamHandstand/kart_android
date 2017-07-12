package us.handstand.kartwheel.layout.recyclerview.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.controller.RaceListController
import us.handstand.kartwheel.layout.recyclerview.viewholder.RaceSummaryVH
import us.handstand.kartwheel.model.Race


class RaceListAdapter(val controller: RaceListController) : RecyclerView.Adapter<RaceSummaryVH>() {
    val races = ArrayList<Race.RaceWithCourse>()

    fun setRaces(races: List<Race.RaceWithCourse>) {
        this.races.clear()
        this.races.addAll(races)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceSummaryVH {
        val rvh = RaceSummaryVH.constructNewInstance(parent)
        rvh.raceListController = controller
        return rvh
    }

    override fun onBindViewHolder(holder: RaceSummaryVH, position: Int) {
        holder.bind(races[position])
    }

    override fun getItemCount(): Int {
        return races.size
    }

}
