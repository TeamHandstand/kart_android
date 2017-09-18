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
    lateinit private var playerNumber: TextView
    lateinit private var playerName: TextView
    lateinit private var forfeitOrShare: ImageView
    private var isClaimed = false
    private var isUser = false
    private var code: String? = null

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
        View.inflate(context, R.layout.view_game_info_player, this)
        playerNumber = findViewById(R.id.player_number)
        playerName = findViewById(R.id.player_name)
        forfeitOrShare = findViewById(R.id.forfeit_or_share)
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.GameInfoPlayerView, 0, 0)
        val number: Int
        try {
            number = a.getInt(R.styleable.GameInfoPlayerView_playerNumber, 1)
            playerNumber.text = "P" + number.toString()
            playerName.text = a.getString(R.styleable.GameInfoPlayerView_playerName)
            isUser = a.getBoolean(R.styleable.GameInfoPlayerView_isUser, false)
        } finally {
            a.recycle()
        }
        playerNumber.setTextColor(if (number == 1) resources.getColor(android.R.color.black) else resources.getColor(R.color.red))
        forfeitOrShare.setOnClickListener(this)
        setBackgroundResource(R.drawable.game_info_player_background)

        val p = ViewUtil.dpToPx(context, 16)
        setPadding(p, p, p, p)
    }

    // TODO: Prevent forfeitting the User that isn't logged in
    fun update(user: User, ticket: Ticket) {
        isClaimed = ticket.isClaimed || user.hasAllInformation()
        playerName.text = if (isClaimed) user.firstName() + " " + user.lastName() else resources.getString(R.string.unclaimed_ticket) + String(Character.toChars(pointerCode))
        code = ticket.code()
        if (!isUser) {
            if (isClaimed) {
                forfeitOrShare.visibility = GONE
            } else {
                forfeitOrShare.setImageResource(R.drawable.share_button)
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.forfeit_or_share && playerActionClickListener != null) {
            if (isUser) {
                playerActionClickListener!!.onPlayerForfeitClick()
            } else {
                playerActionClickListener!!.onPlayerShareClick(code!!)
            }
        }
    }

    companion object {
        private const val pointerCode = 0x1F449

        interface OnPlayerActionClickListener {
            fun onPlayerForfeitClick()
            fun onPlayerShareClick(code: String)
        }
    }
}
