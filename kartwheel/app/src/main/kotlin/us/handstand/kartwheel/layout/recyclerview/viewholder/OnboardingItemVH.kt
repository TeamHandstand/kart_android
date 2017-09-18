package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import us.handstand.kartwheel.R


class OnboardingItemVH private constructor(viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup) {
    private val imageView: ImageView = viewGroup.findViewById(R.id.onboardingItem)

    companion object {
        fun constructNewInstance(parent: ViewGroup): OnboardingItemVH {
            return OnboardingItemVH(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_holder_onboarding_started, parent, false) as ViewGroup)
        }
    }

    fun bind(imageRes: Int) {
        imageView.setImageResource(imageRes)
    }
}