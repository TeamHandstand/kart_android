package us.handstand.kartwheel.fragment.ticket

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity


class AlreadyClaimedFragment : Fragment(), TicketActivity.TicketFragment, View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentViewGroup = inflater.inflate(R.layout.fragment_ticket_already_claimed, container, false) as ViewGroup
        fragmentViewGroup.findViewById<View>(R.id.additionalButton).setOnClickListener(this)
        return fragmentViewGroup
    }

    override fun getTitleResId(): Int {
        return us.handstand.kartwheel.R.string.already_claimed_title
    }

    override fun getAdvanceButtonTextResId(): Int {
        return us.handstand.kartwheel.R.string.try_different_code
    }

    override fun getAdvanceButtonColor(): Int {
        return R.color.blue
    }

    override fun getAdvanceButtonLoadingColor(): Int {
        return R.color.blue_loading
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return true
    }

    override fun onClick(v: View) {
        if (v.id == R.id.additionalButton) {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
                    .setType("plain/text")
                    .putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.support_email)))
                    .putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.contact_us_subject_line))
                    .putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.contact_us_body, ticketController.code))
            startActivity(Intent.createChooser(emailIntent, resources.getString(R.string.contact_us)))
        }
    }
}
