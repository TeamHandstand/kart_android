package us.handstand.kartwheel.layout.recyclerview.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.recyclerview.viewholder.OnboardingItemVH

class EndlessItemAdapter : RecyclerView.Adapter<OnboardingItemVH>() {
    var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemVH {
        return OnboardingItemVH.constructNewInstance(parent)
    }

    override fun onBindViewHolder(holder: OnboardingItemVH, position: Int) {
        synchronized(EmojiAdapter@ this, {
            holder.bind(itemResList[position % itemResList.size])
        })
    }

    override fun getItemCount(): Int {
        synchronized(EmojiAdapter@ this, {
            return Int.MAX_VALUE
        })
    }

    companion object {
        val itemResList = listOf(R.drawable.race_item_banana,
                R.drawable.onboarding_preview_blue_shell,
                R.drawable.pancakes,
                R.drawable.race_item_shield,
                R.drawable.onboarding_preview_ribbon,
                R.drawable.race_item_acorn,
                R.drawable.onboarding_preview_red_shell,
                R.drawable.race_item_earthquake,
                R.drawable.onboarding_preview_crown,
                R.drawable.onboarding_preview_green_shell,
                R.drawable.item_zone_active,
                R.drawable.mini_game_water_bottle_upright,
                R.drawable.race_item_dehydrator,
                R.drawable.waffles)
    }
}
