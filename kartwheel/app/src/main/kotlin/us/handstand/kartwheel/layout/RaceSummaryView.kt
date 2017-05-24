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
    var courseName: TextView
    var distance: TextView
    var spotsLeft: TextView
    var avatar: RegistrantAvatarView
    val runnerEmojiCode = 0x1F3C3

    init {
        View.inflate(context, R.layout.view_holder_race_summary, this)
        startTime = ViewUtil.findView(this, R.id.startTime)
        courseName = ViewUtil.findView(this, R.id.courseName)
        distance = ViewUtil.findView(this, R.id.distance)
        spotsLeft = ViewUtil.findView(this, R.id.spotsLeft)
        avatar = ViewUtil.findView(this, R.id.avatar)
    }

    fun setRace(race: Race) {
        startTime.text = DateFormatter.getTimeOfDay(race.startTime()!!).replace(' ', '\n')
        courseName.text = "#" + race.raceOrder().toString() + " - " + (race.name() ?: Race.DEFAULT_RACE_NAME)
        val miles = (race.course()?.distance() ?: 0.0) * (race.totalLaps() ?: 0L)
        distance.text = race.totalLaps().toString() + " laps | " + miles.toString().substring(0, 3) + " miles"

        alpha = 1f
        var spotsLeftTextColorRes = R.color.textDarkGrey
        avatar.visibility = View.GONE
        when (race.raceStatus) {
            FINISHED -> {
                alpha = 0.75f
                spotsLeft.text = "Finished"
            }
            REGISTERED -> {
                spotsLeft.text = "Registered " + String(Character.toChars(runnerEmojiCode))
                avatar.visibility = View.VISIBLE
                avatar.setRegistrantImageUrl(Storage.userImageUrl)
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
        @Suppress("DEPRECATION")
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