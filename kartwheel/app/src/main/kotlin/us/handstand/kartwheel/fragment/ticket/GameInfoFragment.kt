package us.handstand.kartwheel.fragment.ticket


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.R.layout
import us.handstand.kartwheel.R.string
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.controller.GameInfoController
import us.handstand.kartwheel.controller.TicketController.Companion.FORFEIT
import us.handstand.kartwheel.controller.TicketController.Companion.GAME_INFO
import us.handstand.kartwheel.layout.GameInfoPlayerView
import us.handstand.kartwheel.layout.GameInfoPlayerView.Companion.OnPlayerActionClickListener
import us.handstand.kartwheel.layout.ViewUtil.findView
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.Ticket
import us.handstand.kartwheel.model.User

class GameInfoFragment : android.support.v4.app.Fragment(), TicketActivity.TicketFragment, GameInfoController.Companion.GameInfoCompletionListener, OnPlayerActionClickListener {
    private var playerOne: GameInfoPlayerView? = null
    private var playerTwo: GameInfoPlayerView? = null
    private var controller: GameInfoController? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // TODO: FAB Buttons
        val fragmentView = inflater!!.inflate(layout.fragment_game_info, container, false) as ViewGroup
        playerOne = findView(fragmentView, R.id.playerOne)
        playerTwo = findView(fragmentView, R.id.playerTwo)
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
        return string.app_name
    }

    override fun getAdvanceButtonTextResId(): Int {
        return string.next
    }

    override fun onPlayerForfeitClick() {
        ticketController.transition(GAME_INFO, FORFEIT)
    }

    override fun onPlayerShareClick() {
        android.support.v4.app.ShareCompat.IntentBuilder
                .from(activity)
                .setText(Storage.code)
                .setType("text/plain")
                .setChooserTitle(string.share_code)
                .startChooser()
    }
}
