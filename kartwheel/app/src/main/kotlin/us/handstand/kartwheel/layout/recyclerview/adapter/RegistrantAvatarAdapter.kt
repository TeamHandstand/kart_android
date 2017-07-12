package us.handstand.kartwheel.layout.recyclerview.adapter

import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.controller.RegistrantInfo
import us.handstand.kartwheel.layout.recyclerview.viewholder.RegistrantAvatarVH


class RegistrantAvatarAdapter : RecyclerView.Adapter<RegistrantAvatarVH>() {
    val registrantInfos = ArrayList<RegistrantInfo>()
    val handler = Handler(Looper.getMainLooper())
    var openSpots = 0L

    fun setRegistrantInfos(registrantInfos: List<RegistrantInfo>) {
        handler.post {
            synchronized(RegistrantAvatarAdapter@ this, {
                this.registrantInfos.clear()
                this.registrantInfos.addAll(registrantInfos)
                notifyOpenSpotsChanged()
            })
        }
    }

    fun notifyOpenSpotsChanged() {
        handler.post {
            synchronized(RegistrantAvatarAdapter@ this, {
                for (i in 1..openSpots) {
                    this.registrantInfos.add(RegistrantInfo())
                }
                notifyDataSetChanged()
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistrantAvatarVH {
        return RegistrantAvatarVH.constructNewInstance(parent)
    }

    override fun onBindViewHolder(holder: RegistrantAvatarVH, position: Int) {
        synchronized(RegistrantAvatarAdapter@ this, {
            holder.bind(registrantInfos[position])
        })
    }

    override fun getItemCount(): Int {
        synchronized(RegistrantAvatarAdapter@ this, {
            return registrantInfos.size
        })
    }
}

