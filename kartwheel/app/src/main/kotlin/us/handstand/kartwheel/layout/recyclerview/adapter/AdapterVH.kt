package us.handstand.kartwheel.layout.recyclerview.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class AdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract val adapterVHClickListener: AdapterVHClickListener<*>?
}
