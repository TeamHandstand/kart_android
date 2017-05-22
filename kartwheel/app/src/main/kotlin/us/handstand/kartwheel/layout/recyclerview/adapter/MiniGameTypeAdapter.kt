package us.handstand.kartwheel.layout.recyclerview.adapter

import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.layout.recyclerview.viewholder.MiniGameTypeVH
import us.handstand.kartwheel.model.MiniGameType


class MiniGameTypeAdapter : RecyclerView.Adapter<MiniGameTypeVH>() {
    val miniGameTypes = ArrayList<MiniGameType>()
    val handler = Handler(Looper.getMainLooper())

    fun setMiniGameTypes(miniGameTypes: List<MiniGameType>) {
        handler.post {
            synchronized(MiniGameTypeAdapter@ this, {
                this.miniGameTypes.clear()
                this.miniGameTypes.addAll(miniGameTypes)
                notifyDataSetChanged()
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniGameTypeVH {
        return MiniGameTypeVH.constructNewInstance(parent)
    }

    override fun onBindViewHolder(holder: MiniGameTypeVH, position: Int) {
        synchronized(MiniGameTypeAdapter@ this, {
            holder.bind(miniGameTypes[position])
        })
    }

    override fun getItemCount(): Int {
        synchronized(MiniGameTypeAdapter@ this, {
            return miniGameTypes.size
        })
    }

}
