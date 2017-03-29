package us.handstand.kartwheel.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity

class AlreadyClaimedFragment : Fragment(), TicketActivity.TicketFragment, View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentViewGroup = inflater!!.inflate(R.layout.fragment_already_claimed, container, false) as ViewGroup
        fragmentViewGroup.findViewById(R.id.already_claimed_link).setOnClickListener(this)
        return fragmentViewGroup
    }

    override fun onClick(v: View) {
        if (v.id == R.id.already_claimed_link) {
            // TODO: Where do we send the user?
            //        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("")));
        }
    }
}
