package us.handstand.kartwheel.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.controller.TicketController.Companion.FORFEIT
import us.handstand.kartwheel.controller.TicketController.Companion.GAME_INFO
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Ticket


class ForfeitFragment : Fragment(), TicketActivity.TicketFragment, View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_forfeit, container, false) as ViewGroup
        fragmentView.findViewById(R.id.additionalButton).setOnClickListener(this)
        val codeLink = ViewUtil.findView<TextView>(fragmentView, R.id.forfeit_code_link)
        codeLink.text = activity.resources.getText(R.string.copy_code).toString() + ticketController.ticket?.code()
        codeLink.setOnClickListener(this)
        return fragmentView
    }

    override fun onClick(v: View) {
        if (v.id == R.id.forfeit_code_link) {
            Toast.makeText(activity, "Code copied!", Toast.LENGTH_LONG).show()
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Code copied!", activity.intent.getStringExtra(Ticket.CODE))
            clipboard.primaryClip = clip
        } else if (v.id == R.id.additionalButton) {
            ticketController.transition(FORFEIT, GAME_INFO)
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

