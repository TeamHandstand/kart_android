package us.handstand.kartwheel.fragment


import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.layout.GameInfoPlayerView
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User


class GameInfoFragment : Fragment(), TicketActivity.TicketFragment {

    private var playerOne: GameInfoPlayerView? = null
    private var playerTwo: GameInfoPlayerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_game_info, container, false) as ViewGroup
        playerOne = ViewUtil.findView(fragmentView, R.id.playerOne)
        playerTwo = ViewUtil.findView(fragmentView, R.id.playerTwo)
        val query = User.FACTORY.select_from_team(Storage.teamId)
        Database.get().createQuery(User.TABLE_NAME, query.statement, *query.args)
                .subscribe({ updateUsers(it.run()) }, { it.printStackTrace() })
        return fragmentView
    }

    private fun updateUsers(cursor: Cursor?) {
        cursor.use { cursor ->
            while (cursor!!.moveToNext()) {
                val user = User.FACTORY.select_from_teamMapper().map(cursor)
                if (user.id() == Storage.userId) {
                    playerOne!!.setClaimed(true)
                    playerOne!!.setPlayerName(user.firstName() + " " + user.lastName())
                } else {
                    playerTwo!!.setClaimed(true)
                    playerTwo!!.setPlayerName(user.firstName() + " " + user.lastName())
                }
            }
        }
    }

    override fun onClick(v: View) {

    }
}
