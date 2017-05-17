package us.handstand.kartwheel.fragment.ticket


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

class TOSFragment : android.support.v4.app.Fragment(), us.handstand.kartwheel.activity.TicketActivity.TicketFragment {
    internal var scrollView: us.handstand.kartwheel.layout.TOSScrollView? = null
    private var button: android.support.v7.widget.AppCompatButton? = null
    private var scrolledToBottom: Boolean = false

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        val fragmentView = inflater!!.inflate(us.handstand.kartwheel.R.layout.fragment_tos, container, false) as android.view.ViewGroup
        scrollView = us.handstand.kartwheel.layout.ViewUtil.findView(fragmentView, R.id.tos_scroll_view)
        scrollView?.setScrolledToBottomListener {
            scrolledToBottom = true
            ticketController.onTicketFragmentStateChanged()
        }
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: android.os.Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button = us.handstand.kartwheel.layout.ViewUtil.findView(activity, R.id.button)
        us.handstand.kartwheel.layout.ViewUtil.setButtonState(resources, button, R.color.grey_button_disabled, R.string.scroll_down, false)
    }

    override fun getTitleResId(): Int {
        return us.handstand.kartwheel.R.string.app_name
    }

    override fun getAdvanceButtonTextResId(): Int {
        return if (scrolledToBottom) us.handstand.kartwheel.R.string.lets_go else us.handstand.kartwheel.R.string.scroll_down
    }

    override fun getAdvanceButtonColor(): Int {
        return if (scrolledToBottom) us.handstand.kartwheel.R.color.blue else super.getAdvanceButtonColor()
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return scrolledToBottom
    }

}
