package us.handstand.kartwheel.fragment.ticket


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
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

class GameInfoFragment : Fragment(), TicketActivity.TicketFragment, GameInfoController.Companion.GameInfoCompletionListener, OnPlayerActionClickListener {
    private lateinit var playerOne: GameInfoPlayerView
    private lateinit var playerTwo: GameInfoPlayerView
    private lateinit var controller: GameInfoController

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // TODO: FAB Buttons
        val fragmentView = inflater!!.inflate(R.layout.fragment_game_info, container, false) as ViewGroup
        playerOne = findView(fragmentView, R.id.playerOne)
        playerTwo = findView(fragmentView, R.id.playerTwo)
        playerOne.playerActionClickListener = this
        playerTwo.playerActionClickListener = this
        controller = GameInfoController(this, Database.get(), Storage.teamId, Storage.userId)
        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        controller.subscribe()
    }

    override fun onPause() {
        controller.unsubscribe()
        super.onPause()
    }

    override fun onPlayer1Info(user: User, ticket: Ticket) {
        activity.runOnUiThread { playerOne.update(user, ticket) }
    }

    override fun onPlayer2Info(user: User, ticket: Ticket) {
        activity.runOnUiThread { playerTwo.update(user, ticket) }
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
        ShareCompat.IntentBuilder
                .from(activity)
                .setText(Storage.code)
                .setType("text/plain")
                .setChooserTitle(string.share_code)
                .startChooser()
    }
}
