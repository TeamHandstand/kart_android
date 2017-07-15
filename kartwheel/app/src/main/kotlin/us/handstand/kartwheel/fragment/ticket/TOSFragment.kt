package us.handstand.kartwheel.fragment.ticket


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.layout.KartButton
import us.handstand.kartwheel.layout.TOSScrollView
import us.handstand.kartwheel.layout.ViewUtil

class TOSFragment : Fragment(), TicketActivity.TicketFragment {
    internal lateinit var scrollView: TOSScrollView
    private lateinit var button: KartButton
    private var scrolledToBottom: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_ticket_tos, container, false) as ViewGroup
        scrollView = ViewUtil.findView(fragmentView, R.id.tosScrollView)
        scrollView.listener = {
            activity.runOnUiThread {
                scrolledToBottom = true
                ticketController.onTicketFragmentStateChanged()
            }
        }
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button = ViewUtil.findView(activity, R.id.button)
        ViewUtil.setButtonState(resources, button, getAdvanceButtonColor(), getAdvanceButtonLoadingColor(), getAdvanceButtonTextResId(), isAdvanceButtonEnabled())
    }

    override fun getTitleResId(): Int {
        return R.string.app_name
    }

    override fun getAdvanceButtonTextResId(): Int {
        return if (isAdvanceButtonEnabled()) R.string.lets_go else R.string.scroll_down
    }

    override fun getAdvanceButtonColor(): Int {
        return if (isAdvanceButtonEnabled()) R.color.blue else super.getAdvanceButtonColor()
    }

    override fun getAdvanceButtonLoadingColor(): Int {
        return if (isAdvanceButtonEnabled()) R.color.blue_loading else super.getAdvanceButtonLoadingColor()
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return scrolledToBottom
    }
}
