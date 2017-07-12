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
    val runnerEmojiCode = 0x1F3C3

    init {
        View.inflate(context, R.layout.recycler_view_holder_race_list, this)
        startTime = ViewUtil.findView(this, R.id.startTime)
        raceName = ViewUtil.findView(this, R.id.raceName)
        details = ViewUtil.findView(this, R.id.raceDetails)
        spotsLeft = ViewUtil.findView(this, R.id.spotsLeft)
        avatar = ViewUtil.findView(this, R.id.avatar)
    }

    fun setRace(race: Race.RaceWithCourse) {
        startTime.text = DateFormatter.getTimeOfDay(race.r().startTime()).replace(' ', '\n')
        raceName.text = resources.getString(R.string.race_name, race.r().raceOrder(), race.r().name() ?: Race.DEFAULT_RACE_NAME)
        val distance = (race.c()?.distance() ?: 0.0) * (race.r().totalLaps() ?: 0L)
        details.text = resources.getString(R.string.race_details, race.r().totalLaps(), distance)

        alpha = 1f
        var spotsLeftTextColorRes = R.color.textDarkGrey
        avatar.visibility = View.GONE
        when (race.raceStatus) {
            FINISHED -> {
                alpha = 0.75f
                spotsLeft.setText(R.string.finished)
            }
            REGISTERED -> {
                spotsLeft.text = resources.getString(R.string.registered, String(Character.toChars(runnerEmojiCode)))
                avatar.visibility = View.VISIBLE
                avatar.setImageUrl(Storage.userImageUrl)
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
        @Suppress("DEPRECATION")
        spotsLeft.setTextColor(resources.getColor(spotsLeftTextColorRes))
        startTime.setBackgroundResource(backgroundFromStartTime(race))
    }

    fun backgroundFromStartTime(race: Race.RaceWithCourse): Int {
        val timeSinceNow = race.timeUntilRace
        if (timeSinceNow < 60 * 2) {
            return R.drawable.background_race_list_time_red
        } else if (timeSinceNow < 60 * 5) {
            return R.drawable.background_race_list_time_yellow
        } else {
            return R.drawable.background_race_list_time_green
        }
    }
}