package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
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
    }

    fun bind(imageUrl: String) {
        if (TextUtils.isEmpty(imageUrl)) {
            avatarView.setImageResource(R.drawable.placeholder_registrant_avatar)
        } else {
            avatarView.setImageUrl(imageUrl, placeholder = R.drawable.placeholder_registrant_avatar)
        }
    }
}