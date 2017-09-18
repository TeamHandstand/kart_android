package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.RegistrantInfo
import us.handstand.kartwheel.layout.CircularImageView
import us.handstand.kartwheel.layout.recyclerview.adapter.AdapterVH
import us.handstand.kartwheel.layout.recyclerview.adapter.AdapterVHClickListener


class RegistrantAvatarVH private constructor(val parent: ViewGroup) : AdapterVH(parent) {
    override var adapterVHClickListener: AdapterVHClickListener<RegistrantAvatarVH>? = null
    val avatarView: CircularImageView = parent.findViewById(R.id.avatar)
    val firstName: TextView = parent.findViewById(R.id.firstName)

    companion object {
        fun constructNewInstance(parent: ViewGroup): RegistrantAvatarVH {
            return RegistrantAvatarVH(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_holder_registrant_avatar, parent, false) as ViewGroup)
        }

    }

    init {
        avatarView.setOnClickListener { adapterVHClickListener?.onAdapterVHClicked(this) }
    }

    fun bind(registrantInfo: RegistrantInfo) {
        if (TextUtils.isEmpty(registrantInfo.imageUrl)) {
            avatarView.setImageResource(R.drawable.placeholder_registrant_avatar)
        } else {
            avatarView.setImageUrl(registrantInfo.imageUrl, placeholder = R.drawable.placeholder_registrant_avatar)
        }
        this.firstName.text = registrantInfo.firstName
    }
}