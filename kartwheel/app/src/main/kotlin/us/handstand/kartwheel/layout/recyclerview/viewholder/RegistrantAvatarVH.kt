package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.RegistrantAvatarView


class RegistrantAvatarVH private constructor(val avatarView: RegistrantAvatarView) : RecyclerView.ViewHolder(avatarView) {
    companion object {
        fun constructNewInstance(parent: ViewGroup): RegistrantAvatarVH {
            return RegistrantAvatarVH(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_registrant_avatar, parent, false) as RegistrantAvatarView)
        }
    }

    fun bind(imageUrl: String) {
        avatarView.setRegistrantImageUrl(imageUrl)
    }
}