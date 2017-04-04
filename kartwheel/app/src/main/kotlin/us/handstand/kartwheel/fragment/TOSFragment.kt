package us.handstand.kartwheel.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.layout.TOSScrollView
import us.handstand.kartwheel.layout.ViewUtil

class TOSFragment : Fragment(), TicketActivity.TicketFragment, TOSScrollView.Companion.ScrolledToBottomListener {
    internal var scrollView: TOSScrollView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_tos, container, false) as ViewGroup
        scrollView = ViewUtil.findView(fragmentView, R.id.tos_scroll_view)
        scrollView?.setScrolledToBottomListener(this)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as TicketActivity).setButtonState(R.color.grey_button_disabled, R.string.scroll_down, false)
    }

    override fun onScrolledToBottom() {
        (activity as TicketActivity).setButtonState(R.color.blue, R.string.lets_go, true)
    }

    override fun onClick(v: View) {}
}
