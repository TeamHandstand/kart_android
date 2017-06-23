package us.handstand.kartwheel.layout.recyclerview.viewholder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.ViewUtil


class OnboardingItemVH private constructor(viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup) {
    private val imageView: ImageView = ViewUtil.findView(viewGroup, R.id.onboardingItem)

    companion object {
        fun constructNewInstance(parent: ViewGroup): OnboardingItemVH {
            return OnboardingItemVH(LayoutInflater.from(parent.context).inflate(R.layout.view_onboarding_item, parent, false) as ViewGroup)
        }
    }

    fun bind(imageRes: Int) {
        imageView.setImageResource(imageRes)
    }
}