package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.Ticket
import us.handstand.kartwheel.model.User

class GameInfoPlayerView : RelativeLayout, View.OnClickListener {

    var forfeitClickListener: OnForfeitClickListener? = null
    private var playerNumber: TextView? = null
    private var playerName: TextView? = null
    private var ticket: Ticket? = null
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
        val forfeit = findViewById(R.id.forfeit)
        var isUser = false
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.GameInfoPlayerView, 0, 0)
        try {
            playerNumber!!.text = "P" + a.getString(R.styleable.GameInfoPlayerView_playerNumber)
            playerName!!.text = a.getString(R.styleable.GameInfoPlayerView_playerName)
            isUser = a.getBoolean(R.styleable.GameInfoPlayerView_isUser, false)
        } finally {
            a.recycle()
        }
        if (isUser) {
            forfeit.visibility = View.VISIBLE
            forfeit.setOnClickListener(this)
        } else {
            forfeit.visibility = View.GONE
        }
        setBackgroundResource(R.drawable.game_info_player_background)

        val p = ViewUtil.dpToPx(context, 16)
        setPadding(p, p, p, p)
    }

    fun setUser(user: User) {
        this.playerName!!.text = user.firstName() + " " + user.lastName()
    }

    fun setTicket(ticket: Ticket) {
        this.ticket = ticket
    }

    fun setClaimed(claimed: Boolean) {
        isClaimed = claimed
        playerNumber!!.setTextColor(if (isClaimed) resources.getColor(R.color.green) else resources.getColor(R.color.red))
    }

    fun setOnForfeitClickListener(clickListener: OnForfeitClickListener) {
        forfeitClickListener = clickListener
    }

    override fun onClick(v: View) {
        if (v.id == R.id.forfeit && forfeitClickListener != null && ticket != null) {
            forfeitClickListener!!.onPlayerForfeitClick(ticket!!)
        }
    }

    companion object {
        interface OnForfeitClickListener {
            fun onPlayerForfeitClick(ticket: Ticket)
        }
    }

}
