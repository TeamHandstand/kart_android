package us.handstand.kartwheel.fragment.ticket

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
import us.handstand.kartwheel.model.TicketModel


class ForfeitFragment : android.support.v4.app.Fragment(), us.handstand.kartwheel.activity.TicketActivity.TicketFragment, android.view.View.OnClickListener {

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        val fragmentView = inflater!!.inflate(us.handstand.kartwheel.R.layout.fragment_forfeit, container, false) as android.view.ViewGroup
        fragmentView.findViewById(us.handstand.kartwheel.R.id.additionalButton).setOnClickListener(this)
        val codeLink = us.handstand.kartwheel.layout.ViewUtil.findView<TextView>(fragmentView, R.id.forfeit_code_link)
        codeLink.text = activity.resources.getText(us.handstand.kartwheel.R.string.copy_code).toString() + ticketController.ticket?.code()
        codeLink.setOnClickListener(this)
        return fragmentView
    }

    override fun onClick(v: android.view.View) {
        if (v.id == us.handstand.kartwheel.R.id.forfeit_code_link) {
            android.widget.Toast.makeText(activity, "Code copied!", android.widget.Toast.LENGTH_LONG).show()
            val clipboard = activity.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Code copied!", activity.intent.getStringExtra(us.handstand.kartwheel.model.TicketModel.CODE))
            clipboard.primaryClip = clip
        } else if (v.id == us.handstand.kartwheel.R.id.additionalButton) {
            ticketController.transition(us.handstand.kartwheel.controller.TicketController.Companion.FORFEIT, us.handstand.kartwheel.controller.TicketController.Companion.GAME_INFO)
        }
    }

    override fun getTitleResId(): Int {
        return us.handstand.kartwheel.R.string.forfeit_ticket_title
    }

    override fun getAdvanceButtonTextResId(): Int {
        return us.handstand.kartwheel.R.string.forfeit_ticket
    }

    override fun getAdvanceButtonColor(): Int {
        return us.handstand.kartwheel.R.color.red
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return true
    }
}

