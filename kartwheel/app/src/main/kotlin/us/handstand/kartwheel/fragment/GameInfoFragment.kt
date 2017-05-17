package us.handstand.kartwheel.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.controller.GameInfoController
import us.handstand.kartwheel.controller.TicketController.Companion.FORFEIT
import us.handstand.kartwheel.controller.TicketController.Companion.GAME_INFO
import us.handstand.kartwheel.layout.GameInfoPlayerView
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.Ticket
import us.handstand.kartwheel.model.User

class GameInfoFragment : Fragment(), TicketActivity.TicketFragment, GameInfoController.Companion.GameInfoCompletionListener, GameInfoPlayerView.Companion.OnPlayerActionClickListener {
    private var playerOne: GameInfoPlayerView? = null
    private var playerTwo: GameInfoPlayerView? = null
    private var controller: GameInfoController? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // TODO: FAB Buttons
        val fragmentView = inflater!!.inflate(R.layout.fragment_game_info, container, false) as ViewGroup
        playerOne = ViewUtil.findView(fragmentView, R.id.playerOne)
        playerTwo = ViewUtil.findView(fragmentView, R.id.playerTwo)
        playerOne!!.setOnForfeitClickListener(this)
        playerTwo!!.setOnForfeitClickListener(this)
        controller = GameInfoController(this, Database.get(), Storage.teamId, Storage.userId)
        return fragmentView
    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.unsubscribe()
        controller = null
    }

    override fun onPlayer1Info(user: User, ticket: Ticket) {
        activity.runOnUiThread { playerOne!!.update(user, ticket) }
    }

    override fun onPlayer2Info(user: User, ticket: Ticket) {
        activity.runOnUiThread { playerTwo!!.update(user, ticket) }
    }

    override fun getTitleResId(): Int {
        return R.string.app_name
    }

    override fun getAdvanceButtonTextResId(): Int {
        return R.string.next
    }

    override fun onPlayerForfeitClick(ticket: Ticket) {
        ticketController.ticket = ticket
        ticketController.transition(GAME_INFO, FORFEIT)
    }

    override fun onPlayerShareClick(ticket: Ticket) {
        ShareCompat.IntentBuilder
                .from(activity)
                .setText(ticket.code())
                .setType("text/plain")
                .setChooserTitle(R.string.share_code)
                .startChooser()
    }
}
