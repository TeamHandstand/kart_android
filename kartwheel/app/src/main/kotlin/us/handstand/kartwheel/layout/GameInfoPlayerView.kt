package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import us.handstand.kartwheel.R

class GameInfoPlayerView : RelativeLayout, View.OnClickListener {

    private var playerNumber: TextView? = null
    private var playerName: TextView? = null
    private var isClaimed: Boolean = false

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.game_info_player_view, this)
        playerNumber = findViewById(R.id.player_number) as TextView
        playerName = findViewById(R.id.player_name) as TextView
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.GameInfoPlayerView, 0, 0)
        try {
            playerNumber!!.text = "P" + a.getString(R.styleable.GameInfoPlayerView_playerNumber)
            playerName!!.text = a.getString(R.styleable.GameInfoPlayerView_playerName)
            findViewById(R.id.forfeit).setOnClickListener(this)
        } finally {
            a.recycle()
        }
        setBackgroundResource(R.drawable.game_info_player_background)

        val p = ViewUtil.dpToPx(context, 16)
        setPadding(p, p, p, p)

    }

    fun setPlayerName(playerName: String) {
        this.playerName!!.text = playerName
    }

    fun setClaimed(claimed: Boolean) {
        isClaimed = claimed
        playerNumber!!.setTextColor(if (isClaimed) resources.getColor(R.color.green) else resources.getColor(R.color.red))
    }

    override fun onClick(v: View?) {
    }

}
