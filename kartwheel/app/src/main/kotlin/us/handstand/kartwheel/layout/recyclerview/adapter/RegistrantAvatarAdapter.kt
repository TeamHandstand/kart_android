package us.handstand.kartwheel.layout.recyclerview.adapter

import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.layout.recyclerview.viewholder.RegistrantAvatarVH


class RegistrantAvatarAdapter : RecyclerView.Adapter<RegistrantAvatarVH>() {
    val registrants = ArrayList<String>()
    val handler = Handler(Looper.getMainLooper())
    var maxRegistrants = 0L

    fun setRegistrants(races: List<String>) {
        handler.post {
            synchronized(RaceListAdapter@ this, {
                this.registrants.clear()
                this.registrants.addAll(races)
                val openSpots = maxRegistrants - registrants.size
                for (i in 1..openSpots) {
                    this.registrants.add("")
                }
                notifyDataSetChanged()
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistrantAvatarVH {
        return RegistrantAvatarVH.constructNewInstance(parent)
    }

    override fun onBindViewHolder(holder: RegistrantAvatarVH, position: Int) {
        synchronized(RaceListAdapter@ this, {
            holder.bind(registrants[position])
        })
    }

    override fun getItemCount(): Int {
        synchronized(RaceListAdapter@ this, {
            return registrants.size
        })
    }
}

