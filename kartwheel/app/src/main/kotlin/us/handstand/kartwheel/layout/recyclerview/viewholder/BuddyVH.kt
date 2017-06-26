package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.GlideImageView
import us.handstand.kartwheel.layout.recyclerview.adapter.AdapterVH
import us.handstand.kartwheel.layout.recyclerview.adapter.AdapterVHClickListener


class BuddyVH private constructor(val viewGroup: ViewGroup) : AdapterVH(viewGroup) {
    override var adapterVHClickListener: AdapterVHClickListener<BuddyVH>? = null
    private val buddy: GlideImageView = viewGroup.findViewById(R.id.buddy) as GlideImageView

    companion object {
        fun constructNewInstance(parent: ViewGroup): BuddyVH {
            return BuddyVH(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_buddy, parent, false) as ViewGroup)
        }
    }

    init {
        buddy.setOnClickListener { adapterVHClickListener?.onAdapterVHClicked(this) }
    }

    fun bind(imageUrl: String) {
        buddy.setImageUrl(imageUrl)
    }
}