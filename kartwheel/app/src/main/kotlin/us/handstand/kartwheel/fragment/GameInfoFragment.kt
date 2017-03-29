package us.handstand.kartwheel.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity


class GameInfoFragment : Fragment(), TicketActivity.TicketFragment {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_game_info, container, false)
        return fragmentView
    }

    override fun onClick(v: View) {

    }
}
