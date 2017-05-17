package us.handstand.kartwheel.fragment.ticket


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

class GameInfoFragment : android.support.v4.app.Fragment(), us.handstand.kartwheel.activity.TicketActivity.TicketFragment, us.handstand.kartwheel.controller.GameInfoController.Companion.GameInfoCompletionListener, us.handstand.kartwheel.layout.GameInfoPlayerView.Companion.OnPlayerActionClickListener {
    private var playerOne: us.handstand.kartwheel.layout.GameInfoPlayerView? = null
    private var playerTwo: us.handstand.kartwheel.layout.GameInfoPlayerView? = null
    private var controller: us.handstand.kartwheel.controller.GameInfoController? = null

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        // TODO: FAB Buttons
        val fragmentView = inflater!!.inflate(us.handstand.kartwheel.R.layout.fragment_game_info, container, false) as android.view.ViewGroup
        playerOne = us.handstand.kartwheel.layout.ViewUtil.findView(fragmentView, R.id.playerOne)
        playerTwo = us.handstand.kartwheel.layout.ViewUtil.findView(fragmentView, R.id.playerTwo)
        playerOne!!.setOnForfeitClickListener(this)
        playerTwo!!.setOnForfeitClickListener(this)
        controller = us.handstand.kartwheel.controller.GameInfoController(this, Database.get(), Storage.teamId, Storage.userId)
        return fragmentView
    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.unsubscribe()
        controller = null
    }

    override fun onPlayer1Info(user: us.handstand.kartwheel.model.User, ticket: us.handstand.kartwheel.model.Ticket) {
        activity.runOnUiThread { playerOne!!.update(user, ticket) }
    }

    override fun onPlayer2Info(user: us.handstand.kartwheel.model.User, ticket: us.handstand.kartwheel.model.Ticket) {
        activity.runOnUiThread { playerTwo!!.update(user, ticket) }
    }

    override fun getTitleResId(): Int {
        return us.handstand.kartwheel.R.string.app_name
    }

    override fun getAdvanceButtonTextResId(): Int {
        return us.handstand.kartwheel.R.string.next
    }

    override fun onPlayerForfeitClick(ticket: us.handstand.kartwheel.model.Ticket) {
        ticketController.ticket = ticket
        ticketController.transition(us.handstand.kartwheel.controller.TicketController.Companion.GAME_INFO, us.handstand.kartwheel.controller.TicketController.Companion.FORFEIT)
    }

    override fun onPlayerShareClick(ticket: us.handstand.kartwheel.model.Ticket) {
        android.support.v4.app.ShareCompat.IntentBuilder
                .from(activity)
                .setText(ticket.code())
                .setType("text/plain")
                .setChooserTitle(us.handstand.kartwheel.R.string.share_code)
                .startChooser()
    }
}
