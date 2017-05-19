package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.Race.*
import us.handstand.kartwheel.util.DateFormatter


class RaceSummaryView : RelativeLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    var startTime: TextView
    var courseName: TextView
    var distance: TextView
    var spotsLeft: TextView
    val runnerEmojiCode = ""

    init {
        View.inflate(context, R.layout.view_holder_race_summary, this)
        startTime = ViewUtil.findView(this, R.id.startTime)
        courseName = ViewUtil.findView(this, R.id.courseName)
        distance = ViewUtil.findView(this, R.id.distance)
        spotsLeft = ViewUtil.findView(this, R.id.spotsLeft)
    }

    fun setRace(race: Race) {
        startTime.text = DateFormatter.getTimeOfDay(race.startTime()!!).replace(' ', '\n')
        courseName.text = "#" + race.raceOrder().toString() + " - " + (race.name() ?: "Racey McRacers")
        val miles = (race.course()?.distance() ?: 0.0) * (race.totalLaps() ?: 0L)
        distance.text = race.totalLaps().toString() + " laps | " + miles.toString().substring(0, 3) + " miles"

        alpha = 1f
        var spotsLeftTextColorRes = R.color.textDarkGrey
        when (race.raceStatus) {
            FINISHED -> {
                alpha = 0.75f
                spotsLeft.text = "Finished"
            }
            REGISTERED -> {
                spotsLeft.text = "Registered " + runnerEmojiCode
            }
            REGISTRATION_CLOSED -> {
                spotsLeft.text = "Registration is closed"
            }
            RACE_IS_FULL -> {
                spotsLeft.text = "Race is full!"
            }
            HAS_OPEN_SPOTS -> {
                spotsLeftTextColorRes = if (race.hasLowRegistrantCount()) R.color.green else R.color.yellow
                spotsLeft.text = race.openSpots().toString() + " spots left"
            }
        }
        spotsLeft.setTextColor(resources.getColor(spotsLeftTextColorRes))
        startTime.setBackgroundResource(backgroundFromStartTime(race))
    }

    fun backgroundFromStartTime(race: Race): Int {
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