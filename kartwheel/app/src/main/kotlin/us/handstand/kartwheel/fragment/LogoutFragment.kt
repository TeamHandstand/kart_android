package us.handstand.kartwheel.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.JsonElement
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.R.layout
import us.handstand.kartwheel.activity.LaunchActivity
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.TicketModel
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.SnackbarUtil


class LogoutFragment : Fragment(), View.OnClickListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(layout.fragment_logout, container, false) as ViewGroup
        fragmentView.findViewById<View>(R.id.keepTicketButton).setOnClickListener(this)
        fragmentView.findViewById<View>(R.id.logoutButton).setOnClickListener(this)
        val codeLink = fragmentView.findViewById<TextView>(R.id.forfeit_code_link)
        codeLink.text = requireActivity().resources.getText(R.string.copy_code).toString() + Storage.code
        codeLink.setOnClickListener(this)
        return fragmentView
    }

    override fun onClick(v: View) {
        if (v.id == R.id.forfeit_code_link) {
            ViewUtil.copyToClipboard(requireActivity(), requireActivity().intent.getStringExtra(TicketModel.CODE))
        } else if (v.id == R.id.keepTicketButton) {
            (requireActivity().findViewById<ViewPager>(R.id.pager)).setCurrentItem(0, true)
        } else if (v.id == R.id.logoutButton) {
            API.forfeitTicket(Storage.ticketId, object : API.APICallback<JsonElement> {
                override fun onSuccess(response: JsonElement) {
                    KartWheel.logout()

                    requireActivity().runOnUiThread {
                        ViewUtil.copyToClipboard(requireActivity(), requireActivity().intent.getStringExtra(TicketModel.CODE) ?: Storage.code)
                        startActivity(Intent(requireActivity(), LaunchActivity::class.java))
                        requireActivity().finish()
                    }
                }

                override fun onFailure(errorCode: Int, errorResponse: String) {
                    super.onFailure(errorCode, errorResponse)
                    requireActivity().runOnUiThread {
                        SnackbarUtil.show(requireActivity(), "Unable to logout. Try again.")
                    }
                }
            })
        }
    }
}
