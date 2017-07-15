package us.handstand.kartwheel.fragment.ticket

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.controller.TicketController.Companion
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.TicketModel


class ForfeitFragment : Fragment(), TicketActivity.TicketFragment, OnClickListener {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_ticket_forfeit, container, false) as ViewGroup
        fragmentView.findViewById(R.id.additionalButton).setOnClickListener(this)
        val codeLink = ViewUtil.findView<TextView>(fragmentView, R.id.forfeit_code_link)
        codeLink.text = activity.resources.getText(R.string.copy_code).toString() + Storage.code
        codeLink.setOnClickListener(this)
        return fragmentView
    }

    override fun onClick(v: View) {
        if (v.id == R.id.forfeit_code_link) {
            ViewUtil.copyToClipboard(activity, activity.intent.getStringExtra(TicketModel.CODE))
        } else if (v.id == R.id.additionalButton) {
            ticketController.transition(Companion.FORFEIT, Companion.GAME_INFO)
        }
    }

    override fun getTitleResId(): Int {
        return R.string.forfeit_ticket_title
    }

    override fun getAdvanceButtonTextResId(): Int {
        return R.string.forfeit_ticket
    }

    override fun getAdvanceButtonColor(): Int {
        return R.color.red
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return true
    }
}

