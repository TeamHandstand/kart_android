package us.handstand.kartwheel.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AccelerateInterpolator
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
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
import us.handstand.kartwheel.fragment.onboarding.PickBuddyFragment
import us.handstand.kartwheel.fragment.onboarding.SelfieFragment
import us.handstand.kartwheel.fragment.onboarding.StartedFragment
import us.handstand.kartwheel.fragment.onboarding.VideoFragment
import us.handstand.kartwheel.inject.provider.BottomSheetCallbackProvider.BSBCallbackIMPL
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.recyclerview.adapter.PickBuddyAdapter
import us.handstand.kartwheel.model.Storage
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class OnboardingActivity : AppCompatActivity(), View.OnClickListener, OnboardingStepCompletionListener {
    private lateinit var background: RelativeLayout
    private lateinit var button: Button
    private lateinit var description: TextView
    private lateinit var makeItRainText: TextView
    private lateinit var pageNumber: TextView
    private lateinit var pickBuddyRecyclerView: RecyclerView
    private lateinit var title: TextView
    private lateinit var video: SimpleExoPlayerView
    lateinit var pickBuddyBehavior: BottomSheetBehavior<RecyclerView>
    lateinit var videoBehavior: BottomSheetBehavior<SimpleExoPlayerView>
    @Inject lateinit var pickBuddyBehaviorCallback: BSBCallbackIMPL
    @Inject lateinit var videoBehaviorCallback: BSBCallbackIMPL

    private val controller = OnboardingController(this)
    private var fragment: OnboardingFragment? = null
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private var timer: ScheduledFuture<*>? = null

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
        videoBehavior = BottomSheetBehavior.from(video)

        background.setOnClickListener(this)
        button.setOnClickListener(this)
        pickBuddyRecyclerView.adapter = PickBuddyAdapter()
        pickBuddyBehaviorCallback.layoutId = R.id.bottomSheet
        pickBuddyBehavior.setBottomSheetCallback(pickBuddyBehaviorCallback)
        videoBehaviorCallback.layoutId = R.id.video
        videoBehavior.setBottomSheetCallback(videoBehaviorCallback)

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
        button.visibility = VISIBLE
        button.isEnabled = true
        Storage.selfieUri = ""
        Storage.selectedBuddyUrl = ""
        Log.e("ONBOARDING", "showNextStep $previous to $next")
        if (nextFragment == null) {
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment as Fragment).commit()
            }
            if (next == POINT_SYSTEM) {
                startMedalRain()
            }
        } else if (!isFinishing && !supportFragmentManager.isDestroyed) {
            button.visibility = if (nextFragment.readyForNextStep()) VISIBLE else INVISIBLE
            supportFragmentManager.beginTransaction().replace(R.id.fragment, nextFragment as Fragment).commit()
        }
        fragment = nextFragment
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button -> {
                if (fragment is SelfieFragment && (isEmpty(Storage.userImageUrl) || !isEmpty(Storage.selfieUri))) {
                    (fragment as SelfieFragment).selfieController.upload()
                } else if (fragment is PickBuddyFragment && (isEmpty(Storage.userBuddyUrl) || !isEmpty(Storage.selectedBuddyUrl))) {
                    button.isEnabled = !(fragment as PickBuddyFragment).uploadBuddyEmoji()
                } else if (fragment?.readyForNextStep() == true || fragment == null) {
                    controller.onStepCompleted(Storage.lastOnboardingState)
                }
            }
            R.id.onboardingBackground -> {
                if (Storage.lastOnboardingState == POINT_SYSTEM) {
                    addRandomMedal()
                }
            }
        }
    }

    fun startMedalRain() {
        timer = scheduler.scheduleWithFixedDelay({
            if (Storage.lastOnboardingState == POINT_SYSTEM && !isDestroyed) {
                runOnUiThread { addRandomMedal() }
            } else {
                timer?.cancel(true)
            }
        }, 0, 350, TimeUnit.MILLISECONDS)
    }

    // TODO: Figure out why the RecyclerView log is so noisy when this runs this is so noisy.
    fun addRandomMedal() {
        val emojiImageView = getRandomEmojiImageView()
        val animator = ObjectAnimator.ofFloat(emojiImageView, "y", background.measuredHeight.toFloat() + emojiImageView.measuredHeight)
        animator.duration = 1500
        animator.interpolator = accelerateInterpolator
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                background.addView(emojiImageView, emojiImageView.layoutParams)
            }

            override fun onAnimationEnd(animation: Animator?) {
                background.removeView(emojiImageView)
            }
        })
        animator.start()
    }

    fun getRandomEmojiImageView(): ImageView {
        val randomEmoji = ImageView(this)
        val emojiDrawableResource = pointSystemEmojis[nextRandom(0, pointSystemEmojis.size)]
        val size = pointSystemSizes[nextRandom(0, pointSystemSizes.size)]
        randomEmoji.setImageResource(emojiDrawableResource)
        randomEmoji.layoutParams = RelativeLayout.LayoutParams(size, size)
        randomEmoji.y = -size.toFloat()
        randomEmoji.x = getRandomXValue()
        return randomEmoji
    }

    fun getRandomXValue(): Float {
        val random = nextRandom(0, if (background.measuredWidth <= 0) 1 else background.measuredWidth)
        return if (random < 20) 20f else if (random > background.measuredWidth - 50f) random - 50f else random.toFloat()
    }

    override fun showDialog(message: String) {
        Toast.makeText(this, message, LENGTH_LONG).show()
    }

    override fun onOnboardingFragmentStateChanged() {
        if (fragment?.readyForNextStep() ?: true) {
            Log.e("readyForNext?", "true")
            button.visibility = VISIBLE
        } else {
            Log.e("readyForNext?", "false")
        }
    }

    interface OnboardingFragment {
        fun readyForNextStep(): Boolean {
            return false
        }

        fun updateOnboardingState() {
            (getActivity() as OnboardingActivity).onOnboardingFragmentStateChanged()
        }

        fun getActivity(): Activity

        val button: Button
            get() {
                return (getActivity() as OnboardingActivity).button
            }
        val controller: OnboardingController
            get() {
                return (getActivity() as OnboardingActivity).controller
            }
        val video: SimpleExoPlayerView
            get() {
                return (getActivity() as OnboardingActivity).video
            }
        val recyclerViewBehavior: BottomSheetBehavior<RecyclerView>
            get() {
                return (getActivity() as OnboardingActivity).pickBuddyBehavior
            }
        val videoBehavior: BottomSheetBehavior<SimpleExoPlayerView>
            get() {
                return (getActivity() as OnboardingActivity).videoBehavior
            }

        val pickBuddyBehaviorCallback: BSBCallbackIMPL
            get() {
                return (getActivity() as OnboardingActivity).pickBuddyBehaviorCallback
            }
        val videoBehaviorCallback: BSBCallbackIMPL
            get() {
                return (getActivity() as OnboardingActivity).videoBehaviorCallback
            }

    }

    fun getFragmentForStep(step: Long): OnboardingFragment? {
        when (step) {
            STARTED -> return StartedFragment()
            SELFIE -> return SelfieFragment()
            PICK_BUDDY -> return PickBuddyFragment()
            VIDEO -> return VideoFragment()
        }
        return null
    }

    companion object {
        val random = Random()
        val accelerateInterpolator = AccelerateInterpolator()
        val pointSystemEmojis = listOf<Int>(R.drawable.leaderboard_first_place, R.drawable.leaderboard_second_place, R.drawable.leaderboard_third_place, R.drawable.team_detail_medal_ribbon, R.drawable.onboarding_preview_crown)
        val pointSystemSizes = listOf<Int>(50, 60, 70, 80, 90)

        fun nextRandom(min: Int, max: Int): Int {
            return random.nextInt(max) + min
        }
    }
}
