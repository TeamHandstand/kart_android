package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.CircularImageView
import us.handstand.kartwheel.layout.recyclerview.adapter.AdapterVH
import us.handstand.kartwheel.layout.recyclerview.adapter.AdapterVHClickListener


class RegistrantAvatarVH private constructor(val avatarView: CircularImageView) : AdapterVH(avatarView) {
    override var adapterVHClickListener: AdapterVHClickListener<RegistrantAvatarVH>? = null

    companion object {
        fun constructNewInstance(parent: ViewGroup): RegistrantAvatarVH {
            return RegistrantAvatarVH(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_holder_registrant_avatar, parent, false) as CircularImageView)
        }
    }

    init {
        avatarView.setOnClickListener { adapterVHClickListener?.onAdapterVHClicked(this) }
        avatarView.setImageResource(-1, R.drawable.placeholder_registrant_avatar)
    }

    fun bind(imageUrl: String) {
        avatarView.setImageUrl(imageUrl, placeholder = R.drawable.placeholder_registrant_avatar)
    }
}