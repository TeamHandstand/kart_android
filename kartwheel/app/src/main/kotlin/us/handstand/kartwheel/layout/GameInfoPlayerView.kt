package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.Ticket
import us.handstand.kartwheel.model.User

class GameInfoPlayerView : RelativeLayout, View.OnClickListener {

    var playerActionClickListener: OnPlayerActionClickListener? = null
    private var playerNumber: TextView? = null
    private var playerName: TextView? = null
    private var forfeitOrShare: ImageView? = null
    private var isClaimed: Boolean = false
    private var isUser: Boolean = false
    private val pointerCode = 0x1F449

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
        forfeitOrShare = findViewById(R.id.forfeit_or_share) as ImageView
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.GameInfoPlayerView, 0, 0)
        try {
            playerNumber!!.text = "P" + a.getString(R.styleable.GameInfoPlayerView_playerNumber)
            playerName!!.text = a.getString(R.styleable.GameInfoPlayerView_playerName)
            isUser = a.getBoolean(R.styleable.GameInfoPlayerView_isUser, false)
        } finally {
            a.recycle()
        }

        forfeitOrShare!!.setOnClickListener(this)
        setBackgroundResource(R.drawable.game_info_player_background)

        val p = ViewUtil.dpToPx(context, 16)
        setPadding(p, p, p, p)
    }

    fun update(user: User, ticket: Ticket) {
        isClaimed = ticket.isClaimed || user.hasAllInformation()
        playerNumber!!.setTextColor(if (isClaimed) resources.getColor(R.color.green) else resources.getColor(R.color.red))
        playerName!!.text = if (isClaimed) user.firstName() + " " + user.lastName() else resources.getString(R.string.unclaimed_ticket) + String(Character.toChars(pointerCode))
        if (!isUser) {
            if (isClaimed) {
                forfeitOrShare!!.visibility = GONE
            } else {
                forfeitOrShare!!.setImageResource(R.mipmap.share_button)
            }
        }
        visibility = VISIBLE
    }

    fun setOnForfeitClickListener(clickListener: OnPlayerActionClickListener) {
        playerActionClickListener = clickListener
    }

    override fun onClick(v: View) {
        if (v.id == R.id.forfeit_or_share && playerActionClickListener != null) {
            if (isUser) {
                playerActionClickListener!!.onPlayerForfeitClick()
            } else {
                playerActionClickListener!!.onPlayerShareClick()
            }
        }
    }

    companion object {
        interface OnPlayerActionClickListener {
            fun onPlayerForfeitClick()
            fun onPlayerShareClick()
        }
    }

}
