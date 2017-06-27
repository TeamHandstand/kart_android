package us.handstand.kartwheel.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.TicketController
import us.handstand.kartwheel.controller.TicketController.Companion.ALREADY_CLAIMED
import us.handstand.kartwheel.controller.TicketController.Companion.CODE_ENTRY
import us.handstand.kartwheel.controller.TicketController.Companion.CRITICAL_INFO
import us.handstand.kartwheel.controller.TicketController.Companion.ERROR
import us.handstand.kartwheel.controller.TicketController.Companion.FORFEIT
import us.handstand.kartwheel.controller.TicketController.Companion.FragmentType
import us.handstand.kartwheel.controller.TicketController.Companion.GAME_INFO
import us.handstand.kartwheel.controller.TicketController.Companion.NONE
import us.handstand.kartwheel.controller.TicketController.Companion.ONBOARDING
import us.handstand.kartwheel.controller.TicketController.Companion.RACE_LIST
import us.handstand.kartwheel.controller.TicketController.Companion.TOS
import us.handstand.kartwheel.controller.TicketController.Companion.WELCOME
import us.handstand.kartwheel.fragment.ticket.*
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Storage

class TicketActivity : AppCompatActivity(), View.OnClickListener, TicketController.Companion.TicketStepCompletionListener {

    interface TicketFragment {

        fun getTitleResId(): Int

        fun getAdvanceButtonTextResId(): Int

        fun getAdvanceButtonColor(): Int {
            return R.color.grey_button_disabled
        }

        fun isAdvanceButtonEnabled(): Boolean {
            return false
        }

        fun canAdvanceToNextStep(): Boolean {
            return true
        }

        fun getActivity(): Activity

        companion object {
            const val INTENT_EXTRA_FRAGMENT_TYPE = "fragment_type"
            fun getFragment(@FragmentType fragmentType: Long): TicketFragment {
                when (fragmentType) {
                    TOS -> return TOSFragment()
                    CODE_ENTRY -> return CodeEntryFragment()
                    ALREADY_CLAIMED -> return AlreadyClaimedFragment()
                    CRITICAL_INFO -> return CriticalInfoFragment()
                    WELCOME -> return WelcomeFragment()
                    GAME_INFO -> return GameInfoFragment()
                    FORFEIT -> return ForfeitFragment()
                }
                return TOSFragment()
            }
        }

        val ticketController: TicketController
            get() {
                return (getActivity() as TicketActivity).ticketController
            }
    }

    val ticketController = TicketController(this)
    private var ticketFragment: TicketFragment? = null
    internal var title: TextView? = null
    internal var button: AppCompatButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)
        val parentView = findViewById(R.id.parent)
        parentView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                parentView.viewTreeObserver.removeOnPreDrawListener(this)
                parentView.background = ViewUtil.drawStripes(this@TicketActivity, parentView.measuredWidth.toFloat(), parentView.measuredHeight.toFloat(), android.R.color.white, R.color.textLightGrey_40p)
                return true
            }
        })
        title = ViewUtil.findView(this, R.id.title_text)
        button = ViewUtil.findView(this, R.id.button)
        button!!.setOnClickListener(this)
        ticketController.transition(NONE, Storage.lastTicketState)
    }

    override fun showDialog(message: String) {
        Toast.makeText(this, message, LENGTH_LONG).show()
    }

    override fun showNextStep(@FragmentType previous: Long, @FragmentType next: Long) {
        // Make sure that we're starting with fresh data.
        if (next == ERROR || next == CODE_ENTRY) {
            KartWheel.logout()
        }

        if (next != ERROR) {
            ViewUtil.hideKeyboard(this)
            ticketFragment = TicketFragment.getFragment(next)
            title!!.text = resources.getString(ticketFragment!!.getTitleResId())
            Storage.lastTicketState = next
            onTicketFragmentStateChanged()
            if (next == RACE_LIST) {
                startActivity(Intent(this, LoggedInActivity::class.java))
                finish()
            } else if (next == ONBOARDING) {
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish()
            } else if (!isFinishing && !supportFragmentManager.isDestroyed) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment, ticketFragment as Fragment?).commit()
            }
        }
    }

    override fun onTicketFragmentStateChanged() {
        if (ticketFragment != null) {
            try {
                ViewUtil.setButtonState(resources, button,
                        ticketFragment!!.getAdvanceButtonColor(),
                        ticketFragment!!.getAdvanceButtonTextResId(),
                        ticketFragment!!.isAdvanceButtonEnabled()
                )
            } catch (e: UninitializedPropertyAccessException) {
                ViewUtil.setButtonState(resources, button, R.color.grey_button_disabled, R.string.next, false)
            }
        }
    }

    override fun onClick(v: View) {
        if (v.isEnabled && v.id == R.id.button && ticketFragment!!.isAdvanceButtonEnabled() && ticketFragment!!.canAdvanceToNextStep()) {
            ticketController.onStepCompleted(Storage.lastTicketState)
        }
    }
}
