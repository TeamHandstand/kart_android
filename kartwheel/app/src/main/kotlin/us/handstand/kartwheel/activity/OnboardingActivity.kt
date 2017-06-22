package us.handstand.kartwheel.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
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

        behavior = BottomSheetBehavior.from(recyclerView)

        button.setOnClickListener(this)

        controller.transition(NONE, POINT_SYSTEM);//Storage.lastOnboardingState)
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
        if (previous == SELFIE) {
            button.isEnabled = true
        }
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
        when (v.id) {
            R.id.button -> {
                if (fragment is SelfieFragment && (isEmpty(Storage.userImageUrl) || !isEmpty(Storage.selfieUri))) {
                    button.isEnabled = !(fragment as SelfieFragment).startUpload()
                } else if (fragment is EmojiFragment && isEmpty(Storage.userBuddyUrl)) {
                    button.isEnabled = !(fragment as EmojiFragment).uploadBuddyEmoji()
                } else if (fragment?.readyForNextStep() == true) {
                    controller.onStepCompleted(Storage.lastOnboardingState)
                }
            }
            R.id.onboardingBackground -> {
                if (Storage.lastOnboardingState == POINT_SYSTEM) {
                    val emojiImageView = getRandomEmojiImageView()
                    val xAnimator = ObjectAnimator.ofFloat(emojiImageView, "x", getRandomXValue())
                    val yAnimator = ObjectAnimator.ofFloat(emojiImageView, "y", background.measuredHeight.toFloat() + emojiImageView.measuredHeight)
                    val animatorSet = AnimatorSet()
                    animatorSet.duration = (100 + (Math.random() * (600) + 1)).toLong()
                    animatorSet.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            background.addView(emojiImageView)
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            background.removeView(emojiImageView)
                        }
                    })
                    animatorSet.playTogether(xAnimator, yAnimator)
                }
            }
        }
    }

    fun getRandomEmojiImageView(): ImageView {
        val randomEmoji = ImageView(this)
        val emojiDrawableResource = pointSystemEmojis[((Math.random() * 10) % pointSystemEmojis.size).toInt()]
        val size = pointSystemSizes[((Math.random() * 10) % pointSystemSizes.size).toInt()]
        randomEmoji.setImageResource(emojiDrawableResource)
        randomEmoji.layoutParams = RelativeLayout.LayoutParams(size, size)
        randomEmoji.x = getRandomXValue()
        return randomEmoji
    }

    fun getRandomXValue(): Float {
        val random = (Math.random() / Int.MAX_VALUE).toFloat() * background.measuredWidth
        return if (random < 20) 20f else if (random > background.measuredHeight - 50f) random - 50f else random
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
        val pointSystemEmojis = listOf<Int>(R.drawable.leaderboard_third_place, R.drawable.leaderboard_third_place, R.drawable.leaderboard_third_place, R.drawable.leaderboard_third_place, R.drawable.leaderboard_third_place)
        val pointSystemSizes = listOf<Int>(50, 60, 70, 80, 90)
    }
}
