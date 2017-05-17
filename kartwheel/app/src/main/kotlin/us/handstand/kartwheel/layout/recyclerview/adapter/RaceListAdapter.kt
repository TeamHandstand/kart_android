package us.handstand.kartwheel.layout.recyclerview.adapter

import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.layout.recyclerview.viewholder.RaceSummaryVH
import us.handstand.kartwheel.model.Race


class RaceListAdapter : RecyclerView.Adapter<RaceSummaryVH>() {
    val races = ArrayList<Race>()
    val handler = Handler(Looper.getMainLooper())

    fun setRaces(races: List<Race>) {
        handler.post {
            synchronized(RaceListAdapter@ this, {
                this.races.clear()
                this.races.addAll(races)
                notifyDataSetChanged()
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceSummaryVH {
        return RaceSummaryVH.constructNewInstance(parent)
    }

    override fun onBindViewHolder(holder: RaceSummaryVH, position: Int) {
        synchronized(RaceListAdapter@ this, {
            holder.bind(races[position])
        })
    }

    override fun getItemCount(): Int {
        synchronized(RaceListAdapter@ this, {
            return races.size
        })
    }
}

