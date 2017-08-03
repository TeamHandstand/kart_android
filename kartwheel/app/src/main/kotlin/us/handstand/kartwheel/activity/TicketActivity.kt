package us.handstand.kartwheel.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.view.View
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
import us.handstand.kartwheel.layout.setCandyCaneBackground
import us.handstand.kartwheel.model.Database
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

    val ticketController = TicketController(Database.get(), this)
    private var ticketFragment: TicketFragment? = null
    internal var title: TextView? = null
    internal var button: AppCompatButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)
        findViewById(R.id.parent).setCandyCaneBackground(android.R.color.white, R.color.textLightGrey_40p)
        title = ViewUtil.findView(this, R.id.title_text)
        button = ViewUtil.findView(this, R.id.button)
        button!!.setOnClickListener(this)
        ticketController.transition(NONE, Storage.lastTicketState)
    }

    override fun onDestroy() {
        ticketController.onDestroy()
        super.onDestroy()
    }

    override fun showDialog(@FragmentType step: Long, message: String) {
        runOnUiThread {
            if (step == CODE_ENTRY && ticketFragment is CodeEntryFragment) {
                (ticketFragment as CodeEntryFragment).setProgressVisibility(View.INVISIBLE)
            }
            Toast.makeText(this, message, LENGTH_LONG).show()
        }
    }

    override fun showNextStep(@FragmentType previous: Long, @FragmentType next: Long) {
        runOnUiThread {
            // Make sure that we're starting with fresh data.
            if (next == CODE_ENTRY) {
                KartWheel.logout(lastTicketState = CODE_ENTRY)
            }

            if (next != ERROR) {
                ViewUtil.hideKeyboard(this)
                Storage.lastTicketState = next
                if (next == RACE_LIST) {
                    startActivity(Intent(this, LoggedInActivity::class.java))
                    finish()
                } else if (next == ONBOARDING) {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                    finish()
                } else if (!isFinishing && !supportFragmentManager.isDestroyed) {
                    ticketFragment = TicketFragment.getFragment(next)
                    title!!.text = resources.getString(ticketFragment!!.getTitleResId())
                    onTicketFragmentStateChanged()
                    supportFragmentManager.beginTransaction().replace(R.id.fragment, ticketFragment as Fragment?).commit()
                }
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
            if (ticketFragment is CodeEntryFragment) {
                (ticketFragment as CodeEntryFragment).setProgressVisibility(View.VISIBLE)
            }
            val lastState = Storage.lastTicketState
            ticketController.onStepCompleted(lastState)
        }
    }
}
