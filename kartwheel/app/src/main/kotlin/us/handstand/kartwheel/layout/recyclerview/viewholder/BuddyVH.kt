package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.GlideApp
import us.handstand.kartwheel.layout.recyclerview.adapter.AdapterVH
import us.handstand.kartwheel.layout.recyclerview.adapter.AdapterVHClickListener


class BuddyVH private constructor(val viewGroup: ViewGroup) : AdapterVH(viewGroup) {
    override var adapterVHClickListener: AdapterVHClickListener<BuddyVH>? = null
    private val buddy: ImageView = viewGroup.findViewById(R.id.buddy)

    companion object {
        fun constructNewInstance(parent: ViewGroup): BuddyVH =
                BuddyVH(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_holder_buddy, parent, false) as ViewGroup)
    }

    init {
        buddy.setOnClickListener { adapterVHClickListener?.onAdapterVHClicked(this) }
    }

    fun bind(imageUrl: String) {
        GlideApp.with(buddy.context)
                .load(imageUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.buddy_placeholder))
                .into(buddy)
    }
}