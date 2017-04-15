package us.handstand.kartwheel.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.layout.TOSScrollView
import us.handstand.kartwheel.layout.ViewUtil

class TOSFragment : Fragment(), TicketActivity.TicketFragment {
    internal var scrollView: TOSScrollView? = null
    private var button: AppCompatButton? = null
    private var scrolledToBottom: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_tos, container, false) as ViewGroup
        scrollView = ViewUtil.findView(fragmentView, R.id.tos_scroll_view)
        scrollView?.setScrolledToBottomListener {
            scrolledToBottom = true
            ticketController.onTicketFragmentStateChanged()
        }
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button = ViewUtil.findView(activity, R.id.button)
        ViewUtil.setButtonState(resources, button, R.color.grey_button_disabled, R.string.scroll_down, false)
    }

    override fun getTitleResId(): Int {
        return R.string.app_name
    }

    override fun getAdvanceButtonTextResId(): Int {
        return if (scrolledToBottom) R.string.lets_go else R.string.scroll_down
    }

    override fun getAdvanceButtonColor(): Int {
        return if (scrolledToBottom) R.color.blue else super.getAdvanceButtonColor()
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return scrolledToBottom
    }

}
