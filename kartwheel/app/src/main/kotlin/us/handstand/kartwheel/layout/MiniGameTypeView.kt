package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.MiniGameType


class MiniGameTypeView : RelativeLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var name: TextView
    var timeEstimate: TextView
    var meetupInstructions: TextView
    var image: ImageView

    init {
        View.inflate(context, R.layout.recycler_view_holder_mini_game, this)
        name = findViewById(R.id.miniGameName)
        timeEstimate = findViewById(R.id.miniGameTime)
        meetupInstructions = findViewById(R.id.miniGameMeetupInstructions)
        image = findViewById(R.id.miniGameImage)
    }

    fun setMiniGameType(miniGame: MiniGameType) {
        name.text = miniGame.name()
        timeEstimate.text = miniGame.timeEstimate()
        meetupInstructions.text = miniGame.meetupInstructions()
        Glide.with(context)
                .load(miniGame.imageUrl())
                .placeholder(R.drawable.placeholder_registrant_avatar_grey)
                .into(image)
    }
}