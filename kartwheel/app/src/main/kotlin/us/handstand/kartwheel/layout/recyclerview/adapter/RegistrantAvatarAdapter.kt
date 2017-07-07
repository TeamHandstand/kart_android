package us.handstand.kartwheel.layout.recyclerview.adapter

import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.layout.recyclerview.viewholder.RegistrantAvatarVH


class RegistrantAvatarAdapter : RecyclerView.Adapter<RegistrantAvatarVH>() {
    val registrantImageUrls = ArrayList<String>()
    val handler = Handler(Looper.getMainLooper())
    var openSpots = 0L

    fun setRegistrantImageUrls(registrantImageUrls: List<String>) {
        handler.post {
            synchronized(RegistrantAvatarAdapter@ this, {
                this.registrantImageUrls.clear()
                this.registrantImageUrls.addAll(registrantImageUrls)
                notifyOpenSpotsChanged()
            })
        }
    }

    fun notifyOpenSpotsChanged() {
        handler.post {
            synchronized(RegistrantAvatarAdapter@ this, {
                for (i in 1..openSpots) {
                    this.registrantImageUrls.add("")
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
            holder.bind(registrantImageUrls[position])
        })
    }

    override fun getItemCount(): Int {
        synchronized(RegistrantAvatarAdapter@ this, {
            return registrantImageUrls.size
        })
    }
}

