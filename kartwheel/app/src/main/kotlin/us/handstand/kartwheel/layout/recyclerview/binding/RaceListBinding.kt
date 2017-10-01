package us.handstand.kartwheel.layout.recyclerview.binding

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.Race.Companion.FINISHED
import us.handstand.kartwheel.model.Race.Companion.HAS_OPEN_SPOTS
import us.handstand.kartwheel.model.Race.Companion.RACE_IS_FULL
import us.handstand.kartwheel.model.Race.Companion.REGISTERED
import us.handstand.kartwheel.model.Race.Companion.REGISTRATION_CLOSED
import us.handstand.kartwheel.util.DateFormatter
import us.handstand.kartwheel.util.StringUtil


class RaceListBinding(race: Race.RaceWithCourse, resources: Resources, val avatarUrl: String, userId: String) {
    val detailsText: String = resources.getString(R.string.race_details, race.r().totalLaps(), (race.c()?.distance() ?: 0.0) * (race.r().totalLaps() ?: 0L))
    val raceName: String = resources.getString(R.string.race_name, race.r().raceOrder(), race.r().name() ?: Race.DEFAULT_RACE_NAME)
    val startTimeText: String = DateFormatter.getTimeOfDay(race.r().startTime()).replace(' ', '\n')
    val timeUntilRace: Long = race.timeUntilRace

    val alpha: Float
    val animationTime: Long
    val avatarVisibility: Int
    val backgroundResId: Int
    val raceId = race.r().id()
    val spotsLeftColor: Int
    val spotsLeftSize: Float
    val spotsLeftText: String
    val timerRunning: Boolean

    init {
        val raceStatus = race.raceStatus(userId)
        var spotsLeftTextColorRes = R.color.textDarkGrey
        val color = colorForRegisteredTimer(raceStatus, timeUntilRace)
        backgroundResId = backgroundFromColor(color)
        var size = 16f
        when (raceStatus) {
            FINISHED -> {
                alpha = 0.75f
                animationTime = 0L
                avatarVisibility = View.GONE
                spotsLeftText = resources.getString(R.string.finished)
                timerRunning = false
            }
            REGISTERED -> {
                alpha = 1f
                avatarVisibility = View.VISIBLE
                size = 21f
                spotsLeftTextColorRes = textColorFromColor(color)
                timerRunning = !race.registrationClosed
                if (race.registrationClosed) {
                    animationTime = 0L
                    spotsLeftText = resources.getString(R.string.start_race)
                } else {
                    animationTime = timeUntilRace - Race.ALLOWABLE_SECONDS_BEFORE_START_TIME_TO_REGISTER
                    spotsLeftText = StringUtil.hourMinSecFromMs(timeUntilRace)
                }
            }
            REGISTRATION_CLOSED -> {
                alpha = 1f
                animationTime = 0L
                avatarVisibility = View.GONE
                spotsLeftText = resources.getString(R.string.registration_closed)
                timerRunning = false
            }
            RACE_IS_FULL -> {
                alpha = 1f
                animationTime = 0L
                avatarVisibility = View.GONE
                spotsLeftText = resources.getString(R.string.race_full)
                timerRunning = false
            }
            HAS_OPEN_SPOTS -> {
                alpha = 1f
                animationTime = 0L
                avatarVisibility = View.GONE
                spotsLeftText = resources.getString(R.string.spots_left, race.r().openSpots())
                spotsLeftTextColorRes = if (race.hasLowRegistrantCount()) R.color.green else R.color.yellow
                timerRunning = false
            }
            else -> {
                alpha = 1f
                animationTime = 0L
                avatarVisibility = View.GONE
                spotsLeftText = ""
                timerRunning = false
            }
        }
        spotsLeftSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, resources.getDisplayMetrics())
        @Suppress("DEPRECATION")
        spotsLeftColor = resources.getColor(spotsLeftTextColorRes)
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