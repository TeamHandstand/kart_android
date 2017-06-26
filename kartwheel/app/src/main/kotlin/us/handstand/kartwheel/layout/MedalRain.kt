package us.handstand.kartwheel.layout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.controller.OnboardingController.Companion.POINT_SYSTEM
import us.handstand.kartwheel.model.Storage
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class MedalRain(val activity: OnboardingActivity) {
    private var background: ViewGroup? = null

    fun onCreate(background: ViewGroup) {
        this.background = background
    }

    fun onDestroy() {
        timer?.cancel(true)
        timer = null
    }

    fun start() {
        timer = scheduler.scheduleWithFixedDelay({
            if (Storage.lastOnboardingState == POINT_SYSTEM && !activity.isDestroyed && !(timer?.isCancelled ?: true)) {
                activity.runOnUiThread { animateMedal() }
            } else {
                timer?.cancel(true)
            }
        }, 0, 350, TimeUnit.MILLISECONDS)
    }

    fun animateMedal() {
        val emojiImageView = getRandomEmojiImageView()
        val animator = ObjectAnimator.ofFloat(emojiImageView, "y", (background?.measuredHeight?.toFloat() ?: 0f) + emojiImageView.measuredHeight)
        animator.duration = 1500
        animator.interpolator = accelerateInterpolator
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                background?.addView(emojiImageView, emojiImageView.layoutParams)
            }

            override fun onAnimationEnd(animation: Animator?) {
                background?.removeView(emojiImageView)
            }
        })
        animator.start()
    }

    fun getRandomEmojiImageView(): ImageView {
        val randomEmoji = ImageView(activity)
        val emojiDrawableResource = pointSystemEmojis[nextRandom(0, pointSystemEmojis.size)]
        val size = pointSystemSizes[nextRandom(0, pointSystemSizes.size)]
        randomEmoji.setImageResource(emojiDrawableResource)
        randomEmoji.layoutParams = RelativeLayout.LayoutParams(size, size)
        randomEmoji.y = -size.toFloat()
        randomEmoji.x = getRandomXValue()
        return randomEmoji
    }

    fun getRandomXValue(): Float {
        val measuredWidth = background?.measuredWidth ?: 0
        val random = nextRandom(0, if (measuredWidth <= 0) 1 else measuredWidth)
        return if (random < 20) 20f else if (random > measuredWidth - 50f) random - 50f else random.toFloat()
    }

    companion object {
        private val scheduler = Executors.newSingleThreadScheduledExecutor()!!
        private var timer: ScheduledFuture<*>? = null
        val random = Random()
        val accelerateInterpolator = AccelerateInterpolator()
        val pointSystemEmojis = listOf<Int>(R.drawable.leaderboard_first_place, R.drawable.leaderboard_second_place, R.drawable.leaderboard_third_place, R.drawable.team_detail_medal_ribbon, R.drawable.onboarding_preview_crown)
        val pointSystemSizes = listOf<Int>(50, 60, 70, 80, 90)

        fun nextRandom(min: Int, max: Int): Int {
            return random.nextInt(max) + min
        }
    }
}