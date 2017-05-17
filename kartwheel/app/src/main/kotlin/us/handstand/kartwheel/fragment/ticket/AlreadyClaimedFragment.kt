package us.handstand.kartwheel.fragment.ticket


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity

class AlreadyClaimedFragment : android.support.v4.app.Fragment(), us.handstand.kartwheel.activity.TicketActivity.TicketFragment, android.view.View.OnClickListener {

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        val fragmentViewGroup = inflater!!.inflate(us.handstand.kartwheel.R.layout.fragment_already_claimed, container, false) as android.view.ViewGroup
        fragmentViewGroup.findViewById(us.handstand.kartwheel.R.id.alreadyClaimedLink).setOnClickListener(this)
        fragmentViewGroup.findViewById(us.handstand.kartwheel.R.id.additionalButton).setOnClickListener(this)
        return fragmentViewGroup
    }

    override fun getTitleResId(): Int {
        return us.handstand.kartwheel.R.string.already_claimed_title
    }

    override fun getAdvanceButtonTextResId(): Int {
        return us.handstand.kartwheel.R.string.try_different_code
    }

    override fun getAdvanceButtonColor(): Int {
        return us.handstand.kartwheel.R.color.blue
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return true
    }

    override fun onClick(v: android.view.View) {
        if (v.id == us.handstand.kartwheel.R.id.alreadyClaimedLink) {
            // TODO: Where do we send the user?
            //        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("")));
        } else if (v.id == us.handstand.kartwheel.R.id.additionalButton) {
            val emailIntent = android.content.Intent(android.content.Intent.ACTION_SEND)
                    .setType("plain/text")
                    .putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf(resources.getString(us.handstand.kartwheel.R.string.support_email)))
                    .putExtra(android.content.Intent.EXTRA_SUBJECT, resources.getString(us.handstand.kartwheel.R.string.contact_us_subject_line))
                    .putExtra(android.content.Intent.EXTRA_TEXT, resources.getString(us.handstand.kartwheel.R.string.contact_us_body, ticketController.code))
            startActivity(android.content.Intent.createChooser(emailIntent, resources.getString(us.handstand.kartwheel.R.string.contact_us)))
        }
    }
}
