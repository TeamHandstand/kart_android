package us.handstand.kartwheel.layout.recyclerview.adapter

import android.support.v7.widget.RecyclerView

interface AdapterVHClickListener<in T : RecyclerView.ViewHolder> {
    fun onAdapterVHClicked(viewHolder: T)
}
