package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.util.DateFormatter


class RaceSummaryView : RelativeLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    var startTime: TextView
    var courseName: TextView
    var distance: TextView
    var spotsLeft: TextView

    init {
        View.inflate(context, R.layout.view_holder_race_summary, this)
        startTime = ViewUtil.findView(this, R.id.startTime)
        courseName = ViewUtil.findView(this, R.id.courseName)
        distance = ViewUtil.findView(this, R.id.distance)
        spotsLeft = ViewUtil.findView(this, R.id.spotsLeft)
    }

    fun setRace(race: Race) {
        startTime.text = DateFormatter.getTimeOfDay(race.startTime()!!).replace(' ', '\n')
        courseName.text = "#" + race.raceOrder().toString() + " - " + race.name()
        if (race.course() != null) {
            val miles = race.course()!!.distance()!! * race.totalLaps()!!
            distance.text = race.totalLaps().toString() + " laps | " + miles.toString().substring(0, 3) + " miles"
        }
        spotsLeft.text = race.openSpots().toString() + " spots open"
    }

}