package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.CircularImageView
import us.handstand.kartwheel.layout.recyclerview.adapter.AdapterVH
import us.handstand.kartwheel.layout.recyclerview.adapter.AdapterVHClickListener


class EmojiVH private constructor(val viewGroup: ViewGroup) : AdapterVH(viewGroup) {
    override var adapterVHClickListener: AdapterVHClickListener<EmojiVH>? = null
    private val avatarView: CircularImageView = viewGroup.findViewById(R.id.emoji) as CircularImageView

    companion object {
        fun constructNewInstance(parent: ViewGroup): EmojiVH {
            return EmojiVH(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_emoji, parent, false) as ViewGroup)
        }
    }

    init {
        avatarView.setOnClickListener { adapterVHClickListener?.onAdapterVHClicked(this) }
    }

    fun bind(imageUrl: String) {
        avatarView.setImageUrl(imageUrl)
    }
}