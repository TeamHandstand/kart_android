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
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AccelerateInterpolator
import android.widget.*
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
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class OnboardingActivity : AppCompatActivity(), View.OnClickListener, OnboardingStepCompletionListener {
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var pageNumber: TextView
    private lateinit var button: Button
    private lateinit var makeItRainText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var background: RelativeLayout
    private lateinit var behavior: BottomSheetBehavior<RecyclerView>

    private val controller = OnboardingController(this)
    private var fragment: OnboardingFragment? = null
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private var timer: ScheduledFuture<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        title = ViewUtil.findView(this, R.id.title)
        description = ViewUtil.findView(this, R.id.description)
        pageNumber = ViewUtil.findView(this, R.id.pageNumber)
        button = ViewUtil.findView(this, R.id.button)
        makeItRainText = ViewUtil.findView(this, R.id.makeItRainDescription)
        recyclerView = ViewUtil.findView(this, R.id.bottomSheet)
        background = ViewUtil.findView(this, R.id.onboardingBackground)
        background.setOnClickListener(this)

        behavior = BottomSheetBehavior.from(recyclerView)

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
        button.isEnabled = true
        if (nextFragment == null) {
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment as Fragment).commit()
            }
            if (next == POINT_SYSTEM) {
                startMedalRain()
            }
        } else if (!isFinishing && !supportFragmentManager.isDestroyed) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment, nextFragment as Fragment).commit()
        }
        fragment = nextFragment
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button -> {
                if (fragment is SelfieFragment && (isEmpty(Storage.userImageUrl) || !isEmpty(Storage.selfieUri))) {
                    button.isEnabled = !(fragment as SelfieFragment).startUpload()
                } else if (fragment is EmojiFragment && isEmpty(Storage.userBuddyUrl)) {
                    button.isEnabled = !(fragment as EmojiFragment).uploadBuddyEmoji()
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
        val bottomSheetBehavior: BottomSheetBehavior<RecyclerView>
            get() {
                return (getActivity() as OnboardingActivity).behavior
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
