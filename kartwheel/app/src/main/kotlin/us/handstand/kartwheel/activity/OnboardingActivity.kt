package us.handstand.kartwheel.activity

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.OnboardingController
import us.handstand.kartwheel.controller.OnboardingController.Companion.ERROR
import us.handstand.kartwheel.controller.OnboardingController.Companion.NONE
import us.handstand.kartwheel.controller.OnboardingController.Companion.PICK_BUDDY
import us.handstand.kartwheel.controller.OnboardingController.Companion.POINT_SYSTEM
import us.handstand.kartwheel.controller.OnboardingController.Companion.SELFIE
import us.handstand.kartwheel.controller.OnboardingController.Companion.STARTED
import us.handstand.kartwheel.controller.OnboardingController.Companion.VIDEO
import us.handstand.kartwheel.controller.OnboardingStepCompletionListener
import us.handstand.kartwheel.fragment.onboarding.EmojiFragment
import us.handstand.kartwheel.fragment.onboarding.SelfieFragment
import us.handstand.kartwheel.fragment.onboarding.StartedFragment
import us.handstand.kartwheel.fragment.onboarding.VideoFragment
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Storage


class OnboardingActivity : AppCompatActivity(), View.OnClickListener, OnboardingStepCompletionListener {
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var pageNumber: TextView
    private lateinit var button: Button
    private lateinit var makeItRainText: TextView

    private val controller = OnboardingController(this)
    private var fragment: OnboardingFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        title = ViewUtil.findView(this, R.id.title)
        description = ViewUtil.findView(this, R.id.description)
        pageNumber = ViewUtil.findView(this, R.id.pageNumber)
        button = ViewUtil.findView(this, R.id.button)
        makeItRainText = ViewUtil.findView(this, R.id.makeItRainDescription)

        button.setOnClickListener(this)

        controller.transition(NONE, Storage.lastOnboardingState)
    }

    override fun showNextStep(previous: Long, next: Long) {
        // Make sure that we're starting with fresh data.
        if (next == ERROR) {
            KartWheel.logout()
        }
        var pageNumberVisibility = VISIBLE
        var makeItRainVisibility = INVISIBLE
        when (next) {
            STARTED -> pageNumberVisibility = INVISIBLE
            POINT_SYSTEM -> makeItRainVisibility = VISIBLE
        }
        title.text = resources.getString(OnboardingController.getTitleStringResIdForStep(next))
        description.text = resources.getString(OnboardingController.getDescriptionStringResIdForStep(next))
        button.text = resources.getString(OnboardingController.getButtonStringResIdForStep(next))
        pageNumber.text = next.toString() + " of 5"
        pageNumber.visibility = pageNumberVisibility
        makeItRainText.visibility = makeItRainVisibility

        Storage.lastOnboardingState = next
        val nextFragment = getFragmentForStep(next)
        if (nextFragment == null) {
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment as Fragment).commit()
            }
        } else if (!isFinishing && !supportFragmentManager.isDestroyed) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment, nextFragment as Fragment).commit()
        }
        fragment = nextFragment
    }

    override fun onClick(v: View) {
        // TODO: Enable/Disable button based on state of selfie/buddy upload
        when (v.id) {
            R.id.button -> {
                if (fragment is SelfieFragment) {
                    (fragment as SelfieFragment).startUpload()
                } else if (fragment?.readyForNextStep() == true) {
                    controller.onStepCompleted(Storage.lastOnboardingState)
                }
            }
        }
    }

    override fun showDialog(message: String) {
        Toast.makeText(this, message, LENGTH_LONG).show()
    }

    override fun onOnboardingFragmentStateChanged() {
        // TODO: Do I need this, since we're not using fragments?
    }

    interface OnboardingFragment {
        fun readyForNextStep(): Boolean {
            return false
        }

        fun getActivity(): Activity

        val controller: OnboardingController
            get() {
                return (getActivity() as OnboardingActivity).controller
            }
    }

    fun getFragmentForStep(step: Long): OnboardingFragment? {
        when (step) {
            STARTED -> return StartedFragment()
            SELFIE -> return SelfieFragment()
            PICK_BUDDY -> return EmojiFragment()
            VIDEO -> return VideoFragment()
        }
        return null
    }

}
