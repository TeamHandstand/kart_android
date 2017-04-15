package us.handstand.kartwheel.fragment


import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.controller.TicketController.Companion.FORFEIT
import us.handstand.kartwheel.controller.TicketController.Companion.GAME_INFO
import us.handstand.kartwheel.layout.GameInfoPlayerView
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.Ticket
import us.handstand.kartwheel.model.User

class GameInfoFragment : Fragment(), TicketActivity.TicketFragment, GameInfoPlayerView.Companion.OnForfeitClickListener {
    private var playerOne: GameInfoPlayerView? = null

    private var playerTwo: GameInfoPlayerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // TODO: FAB Buttons
        val fragmentView = inflater!!.inflate(R.layout.fragment_game_info, container, false) as ViewGroup
        playerOne = ViewUtil.findView(fragmentView, R.id.playerOne)
        playerTwo = ViewUtil.findView(fragmentView, R.id.playerTwo)
        playerOne!!.setOnForfeitClickListener(this)
        playerTwo!!.setOnForfeitClickListener(this)
        val query = User.FACTORY.select_from_team(Storage.teamId)
        Database.get().createQuery(User.TABLE_NAME, query.statement, *query.args)
                .subscribe({ updateUsers(it.run()) }, { it.printStackTrace() })
        return fragmentView
    }

    override fun getTitleResId(): Int {
        return R.string.app_name
    }

    override fun getAdvanceButtonTextResId(): Int {
        return R.string.next
    }

    private fun updateUsers(cursor: Cursor?) {
        cursor.use { cursor ->
            while (cursor!!.moveToNext()) {
                val user = User.FACTORY.select_from_teamMapper().map(cursor)
                if (user.id() == Storage.userId) {
                    playerOne!!.setClaimed(true)
                    playerOne!!.setUser(user)
                } else {
                    playerTwo!!.setClaimed(true)
                    playerTwo!!.setUser(user)
                }
            }
        }
    }

    override fun onPlayerForfeitClick(ticket: Ticket) {
        ticketController.ticket = ticket
        ticketController.transition(GAME_INFO, FORFEIT)
    }
}
