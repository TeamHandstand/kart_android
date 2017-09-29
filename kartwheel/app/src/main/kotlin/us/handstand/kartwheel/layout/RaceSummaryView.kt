package us.handstand.kartwheel.layout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.Race.Companion.FINISHED
import us.handstand.kartwheel.model.Race.Companion.HAS_OPEN_SPOTS
import us.handstand.kartwheel.model.Race.Companion.RACE_IS_FULL
import us.handstand.kartwheel.model.Race.Companion.REGISTERED
import us.handstand.kartwheel.model.Race.Companion.REGISTRATION_CLOSED
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.util.DateFormatter
import us.handstand.kartwheel.util.StringUtil


class RaceSummaryView : RelativeLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val startTime: TextView
    private val raceName: TextView
    private val details: TextView
    private val spotsLeft: TextView
    private val avatar: CircularImageView
    private var timerRunning = false

    init {
        View.inflate(context, R.layout.recycler_view_holder_race_list, this)
        startTime = findViewById(R.id.startTime)
        raceName = findViewById(R.id.raceName)
        details = findViewById(R.id.raceDetails)
        spotsLeft = findViewById(R.id.spotsLeft)
        avatar = findViewById(R.id.avatar)
    }

    fun setRace(race: Race.RaceWithCourse, valueAnimator: ValueAnimator) {
        timerRunning = false
        startTime.text = DateFormatter.getTimeOfDay(race.r().startTime()).replace(' ', '\n')
        raceName.text = resources.getString(R.string.race_name, race.r().raceOrder(), race.r().name() ?: Race.DEFAULT_RACE_NAME)
        val distance = (race.c()?.distance() ?: 0.0) * (race.r().totalLaps() ?: 0L)
        details.text = resources.getString(R.string.race_details, race.r().totalLaps(), distance)

        alpha = 1f
        avatar.visibility = View.INVISIBLE
        spotsLeft.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)

        val timeUntilRace = race.timeUntilRace
        val raceStatus = race.raceStatus(Storage.userId, timeUntilRace)
        var spotsLeftTextColorRes = R.color.textDarkGrey
        val color = colorForRegisteredTimer(raceStatus, timeUntilRace)

        when (raceStatus) {
            FINISHED -> {
                alpha = 0.75f
                spotsLeft.setText(R.string.finished)
            }
            REGISTERED -> {
                avatar.visibility = View.VISIBLE
                avatar.setImageUrl(Storage.userImageUrl)
                spotsLeft.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21f)
                spotsLeftTextColorRes = textColorFromColor(color)
                if (race.registrationClosed) {
                    spotsLeft.setText(R.string.start_race)
                } else {
                    valueAnimator.cancel()
                    val animationTime = timeUntilRace - Race.ALLOWABLE_SECONDS_BEFORE_START_TIME_TO_REGISTER
                    valueAnimator.duration = animationTime
                    valueAnimator.removeAllUpdateListeners()
                    valueAnimator.removeAllListeners()
                    valueAnimator.addUpdateListener {
                        if (timerRunning) {
                            spotsLeft.text = StringUtil.hourMinSecFromMs(timeUntilRace - ((it.animatedValue as Float) * timeUntilRace).toLong())
                        }
                    }
                    valueAnimator.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            if (timerRunning) {
                                spotsLeft.setText(R.string.start_race)
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                            spotsLeft.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                            spotsLeft.setTextColor(resources.getColor(R.color.textDarkGrey))
                        }
                    })
                    timerRunning = true
                    valueAnimator.start()
                }
            }
            REGISTRATION_CLOSED -> {
                spotsLeft.setText(R.string.registration_closed)
            }
            RACE_IS_FULL -> {
                spotsLeft.setText(R.string.race_full)
            }
            HAS_OPEN_SPOTS -> {
                spotsLeftTextColorRes = if (race.hasLowRegistrantCount()) R.color.green else R.color.yellow
                spotsLeft.text = resources.getString(R.string.spots_left, race.r().openSpots())
            }
        }
        startTime.setBackgroundResource(backgroundFromColor(color))
        @Suppress("DEPRECATION")
        spotsLeft.setTextColor(resources.getColor(spotsLeftTextColorRes))
    }

    private fun backgroundFromColor(color: Long): Int = when (color) {
        RED -> R.drawable.background_race_list_time_red
        YELLOW -> R.drawable.background_race_list_time_yellow
        else -> R.drawable.background_race_list_time_green
    }

    private fun textColorFromColor(color: Long): Int = when (color) {
        RED -> R.color.red
        YELLOW -> R.color.yellow
        else -> R.color.green
    }

    private fun colorForRegisteredTimer(raceStatus: Long, timeUntilRace: Long): Long = when {
        raceStatus == Race.REGISTRATION_CLOSED || raceStatus == Race.FINISHED -> RED
        timeUntilRace <= Race.MINUTES_BEFORE_START_TIME_TO_SHOW_COUNTDOWN -> YELLOW
        else -> GREEN
    }

    private val RED = 0L
    private val YELLOW = 1L
    private val GREEN = 2L
}
