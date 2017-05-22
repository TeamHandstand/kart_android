package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.layout.MiniGameTypeView
import us.handstand.kartwheel.model.MiniGameType


class MiniGameTypeVH private constructor(val miniGameTypeView: MiniGameTypeView) : RecyclerView.ViewHolder(miniGameTypeView) {
    companion object {
        fun constructNewInstance(parent: ViewGroup): MiniGameTypeVH {
            return MiniGameTypeVH(MiniGameTypeView(parent.context))
        }
    }

    fun bind(miniGameType: MiniGameType) {
        miniGameTypeView.setMiniGameType(miniGameType)
    }
}