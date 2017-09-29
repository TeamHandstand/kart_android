package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
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


class RaceSummaryView : RelativeLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    var startTime: TextView
    var raceName: TextView
    var details: TextView
    var spotsLeft: TextView
    var avatar: CircularImageView
    private val runnerEmojiCode = 0x1F3C3

    init {
        View.inflate(context, R.layout.recycler_view_holder_race_list, this)
        startTime = findViewById(R.id.startTime)
        raceName = findViewById(R.id.raceName)
        details = findViewById(R.id.raceDetails)
        spotsLeft = findViewById(R.id.spotsLeft)
        avatar = findViewById(R.id.avatar)
    }

    fun setRace(race: Race.RaceWithCourse) {
        startTime.text = DateFormatter.getTimeOfDay(race.r().startTime()).replace(' ', '\n')
        raceName.text = resources.getString(R.string.race_name, race.r().raceOrder(), race.r().name() ?: Race.DEFAULT_RACE_NAME)
        val distance = (race.c()?.distance() ?: 0.0) * (race.r().totalLaps() ?: 0L)
        details.text = resources.getString(R.string.race_details, race.r().totalLaps(), distance)

        alpha = 1f
        var spotsLeftTextColorRes = R.color.textDarkGrey
        avatar.visibility = View.GONE
        val timeUntilRace = race.timeUntilRace
        val raceStatus = race.raceStatus(Storage.userId, timeUntilRace)
        when (raceStatus) {
            FINISHED -> {
                alpha = 0.75f
                spotsLeft.setText(R.string.finished)
            }
            REGISTERED -> {
                spotsLeft.text = resources.getString(R.string.registered, String(Character.toChars(runnerEmojiCode)))
                avatar.visibility = View.VISIBLE
                avatar.setImageUrl(Storage.userImageUrl)
                // TODO: Show timer
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
        startTime.setBackgroundResource(backgroundFromStartTime(raceStatus, timeUntilRace))
        @Suppress("DEPRECATION")
        spotsLeft.setTextColor(resources.getColor(spotsLeftTextColorRes))
    }

    private fun backgroundFromStartTime(raceStatus: Long, timeUntilRace: Long): Int = when {
        raceStatus == Race.REGISTRATION_CLOSED || raceStatus == Race.FINISHED -> R.drawable.background_race_list_time_red
        timeUntilRace <= Race.MINUTES_BEFORE_START_TIME_TO_SHOW_COUNTDOWN -> R.drawable.background_race_list_time_yellow
        else -> R.drawable.background_race_list_time_green
    }
}
