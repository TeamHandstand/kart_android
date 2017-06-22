package us.handstand.kartwheel.layout.recyclerview.adapter

import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import us.handstand.kartwheel.layout.recyclerview.viewholder.EmojiVH

class EmojiAdapter : RecyclerView.Adapter<EmojiVH>(), AdapterVHClickListener<EmojiVH> {
    var recyclerView: RecyclerView? = null
    var selectedEmojiUrl: String? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiVH {
        val rvh = EmojiVH.constructNewInstance(parent)
        rvh.adapterVHClickListener = this
        return rvh
    }

    override fun onBindViewHolder(holder: EmojiVH, position: Int) {
        synchronized(EmojiAdapter@ this, {
            holder.bind(emojiUrls[position])
        })
    }

    override fun getItemCount(): Int {
        synchronized(EmojiAdapter@ this, {
            return emojiUrls.size
        })
    }

    override fun onAdapterVHClicked(viewHolder: EmojiVH) {
        // TODO: Highlight the buddy
        selectedEmojiUrl = emojiUrls[viewHolder.adapterPosition]
        // Collapse the RecyclerView
        BottomSheetBehavior.from(recyclerView).state = STATE_COLLAPSED
    }

    companion object {
        val emojiUrls = listOf("https://s3.amazonaws.com/kartwheel-production/buddies/buddy-wolf.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-snowman.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-waffles.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-tiger.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-whale.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-ogre.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-pig.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-sun.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-gorilla.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-horse.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-chipmunk.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-dog.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-bird.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-alien.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-megatron.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-redface.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-clown.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-cowboy.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-rabbit.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-mouse.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-lion.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-butterfly.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-chicken.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-fox.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-frog.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-koala.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-squid.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-fish.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-unicorn.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-hearno.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-octopus.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-dragon.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-squirtle.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-charmander.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-pancakes.png",
                "https://s3.amazonaws.com/kartwheel-production/buddies/buddy-robot.png")
    }
}
