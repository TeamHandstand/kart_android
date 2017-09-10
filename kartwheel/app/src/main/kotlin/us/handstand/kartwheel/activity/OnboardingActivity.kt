package us.handstand.kartwheel.activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils.isEmpty
import android.view.KeyEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.OnboardingController
import us.handstand.kartwheel.controller.OnboardingController.Companion.ERROR
import us.handstand.kartwheel.controller.OnboardingController.Companion.FINISHED
import us.handstand.kartwheel.controller.OnboardingController.Companion.NONE
import us.handstand.kartwheel.controller.OnboardingController.Companion.PICK_BUDDY
import us.handstand.kartwheel.controller.OnboardingController.Companion.POINT_SYSTEM
import us.handstand.kartwheel.controller.OnboardingController.Companion.SELFIE
import us.handstand.kartwheel.controller.OnboardingController.Companion.STARTED
import us.handstand.kartwheel.controller.OnboardingController.Companion.VIDEO
import us.handstand.kartwheel.controller.OnboardingStepCompletionListener
import us.handstand.kartwheel.controller.TicketController
import us.handstand.kartwheel.fragment.onboarding.PickBuddyFragment
import us.handstand.kartwheel.fragment.onboarding.SelfieFragment
import us.handstand.kartwheel.fragment.onboarding.StartedFragment
import us.handstand.kartwheel.fragment.onboarding.VideoFragment
import us.handstand.kartwheel.inject.provider.BottomSheetCallbackProvider.BSBCallbackIMPL
import us.handstand.kartwheel.layout.KartButton
import us.handstand.kartwheel.layout.MedalRain
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.recyclerview.adapter.PickBuddyAdapter
import us.handstand.kartwheel.layout.setCandyCaneBackground
import us.handstand.kartwheel.model.Storage
import javax.inject.Inject

class OnboardingActivity : AppCompatActivity(), View.OnClickListener, OnboardingStepCompletionListener {
    private lateinit var background: RelativeLayout
    private lateinit var button: KartButton
    private lateinit var description: TextView
    private lateinit var makeItRainText: TextView
    private lateinit var pageNumber: TextView
    private lateinit var pickBuddyRecyclerView: RecyclerView
    private lateinit var title: TextView
    private lateinit var video: SimpleExoPlayerView
    lateinit var pickBuddyBehavior: BottomSheetBehavior<RecyclerView>
    lateinit var videoBehavior: BottomSheetBehavior<FrameLayout>
    @Inject lateinit var pickBuddyBehaviorCallback: BSBCallbackIMPL
    @Inject lateinit var videoBehaviorCallback: BSBCallbackIMPL

    private val medalRain = MedalRain(this)
    private val controller = OnboardingController(this)
    private var fragment: OnboardingFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        KartWheel.injector.inject(this)
        background = ViewUtil.findView(this, R.id.onboardingBackground)
        button = ViewUtil.findView(this, R.id.button)
        description = ViewUtil.findView(this, R.id.description)
        makeItRainText = ViewUtil.findView(this, R.id.makeItRainDescription)
        pageNumber = ViewUtil.findView(this, R.id.pageNumber)
        pickBuddyRecyclerView = ViewUtil.findView(this, R.id.bottomSheet)
        title = ViewUtil.findView(this, R.id.title)
        video = ViewUtil.findView(this, R.id.video)
        pickBuddyBehavior = BottomSheetBehavior.from(pickBuddyRecyclerView)
        videoBehavior = BottomSheetBehavior.from(ViewUtil.findView(this, R.id.videoSheet))

        background.setCandyCaneBackground(R.color.blue_background, R.color.blue)
        background.setOnClickListener(this)
        button.setOnClickListener(this)
        medalRain.onCreate(background)
        pickBuddyRecyclerView.adapter = PickBuddyAdapter()
        pickBuddyBehaviorCallback.tag = "_pickBuddy"
        pickBuddyBehavior.setBottomSheetCallback(pickBuddyBehaviorCallback)
        videoBehaviorCallback.tag = "_video"
        videoBehavior.setBottomSheetCallback(videoBehaviorCallback)

        controller.transition(NONE, Storage.lastOnboardingState)
    }

    override fun onDestroy() {
        medalRain.onDestroy()
        super.onDestroy()
    }

    override fun showNextStep(previous: Long, next: Long) {
        if (next == ERROR) {
            return
        } else if (next == FINISHED) {
            Storage.lastOnboardingState = FINISHED
            if (Storage.showRaces) {
                startActivity(Intent(this, LoggedInActivity::class.java))
            } else {
                Storage.lastTicketState = TicketController.GAME_INFO
                startActivity(Intent(this, TicketActivity::class.java))
            }
            finish()
            return
        }
        runOnUiThread {
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
            pickBuddyBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            videoBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            Storage.lastOnboardingState = next
            val nextFragment = getFragmentForStep(next)
            button.visibility = VISIBLE
            button.isEnabled = true
            button.loading = false
            Storage.selfieUri = ""
            Storage.selectedBuddyUrl = ""
            if (nextFragment == null) {
                if (fragment != null) {
                    supportFragmentManager.beginTransaction().remove(fragment as Fragment).commit()
                }
                if (next == POINT_SYSTEM) {
                    medalRain.start()
                }
            } else if (!isFinishing && !supportFragmentManager.isDestroyed) {
                button.visibility = if (nextFragment.readyForNextStep()) VISIBLE else INVISIBLE
                supportFragmentManager.beginTransaction().replace(R.id.fragment, nextFragment as Fragment).commit()
            }
            fragment = nextFragment
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button -> {
                if (fragment is SelfieFragment && (isEmpty(Storage.userImageUrl) || !isEmpty(Storage.selfieUri))) {
                    (fragment as SelfieFragment).selfieController.upload()
                } else if (fragment is PickBuddyFragment && (isEmpty(Storage.userBuddyUrl) || !isEmpty(Storage.selectedBuddyUrl))) {
                    button.isEnabled = !(fragment as PickBuddyFragment).uploadBuddyEmoji()
                    button.loading = !button.isEnabled
                } else if (fragment?.readyForNextStep() == true || fragment == null) {
                    controller.onStepCompleted(Storage.lastOnboardingState)
                }
            }
            R.id.onboardingBackground -> {
                if (Storage.lastOnboardingState == POINT_SYSTEM) {
                    medalRain.animateMedal()
                }
            }
        }
    }

    override fun showDialog(message: String) {
        runOnUiThread { Toast.makeText(this, message, LENGTH_LONG).show() }
    }

    override fun onOnboardingFragmentStateChanged() {
        if (fragment?.readyForNextStep() == true) {
            button.visibility = VISIBLE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && hasVisibleBottomSheet()) {
            event?.startTracking()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val behavior = getVisibleBottomSheetBehavior()
            if (behavior != null) {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun hasVisibleBottomSheet(): Boolean = getVisibleBottomSheetBehavior() != null

    private fun getVisibleBottomSheetBehavior(): BottomSheetBehavior<*>? {
        if (pickBuddyBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            return pickBuddyBehavior
        } else if (videoBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            return videoBehavior
        }
        return null
    }

    interface OnboardingFragment {
        fun readyForNextStep(): Boolean = false

        fun updateOnboardingState() {
            (getActivity() as OnboardingActivity).onOnboardingFragmentStateChanged()
        }

        fun getActivity(): Activity

        val button: KartButton
            get() = (getActivity() as OnboardingActivity).button
        val controller: OnboardingController
            get() = (getActivity() as OnboardingActivity).controller
        val video: SimpleExoPlayerView
            get() = (getActivity() as OnboardingActivity).video
        val recyclerViewBehavior: BottomSheetBehavior<RecyclerView>
            get() = (getActivity() as OnboardingActivity).pickBuddyBehavior
        val videoBehavior: BottomSheetBehavior<FrameLayout>
            get() = (getActivity() as OnboardingActivity).videoBehavior

        val pickBuddyBehaviorCallback: BSBCallbackIMPL
            get() = (getActivity() as OnboardingActivity).pickBuddyBehaviorCallback
        val videoBehaviorCallback: BSBCallbackIMPL
            get() = (getActivity() as OnboardingActivity).videoBehaviorCallback

    }

    private fun getFragmentForStep(step: Long): OnboardingFragment? {
        when (step) {
            STARTED -> return StartedFragment()
            SELFIE -> return SelfieFragment()
            PICK_BUDDY -> return PickBuddyFragment()
            VIDEO -> return VideoFragment()
        }
        return null
    }
}
