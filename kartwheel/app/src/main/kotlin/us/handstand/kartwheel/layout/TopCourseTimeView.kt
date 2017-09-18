package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.model.UserRaceInfo
import us.handstand.kartwheel.util.StringUtil

class TopCourseTimeView : RelativeLayout {
    lateinit var name: TextView
    lateinit var time: TextView
    lateinit var avatar: CircularImageView

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_top_course_time, this)
        name = findViewById(R.id.name)
        time = findViewById(R.id.time)
        avatar = findViewById(R.id.avatar)
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.TopCourseTimeView, 0, 0)
        try {
            val rank = a.getInt(R.styleable.TopCourseTimeView_rank, 1)
            findViewById<TextView>(R.id.rank).text = if (rank == 1) "1st" else if (rank == 2) "2nd" else "3rd"
        } finally {
            a.recycle()
        }
    }

    fun setRegistrant(user: User, userRaceInfo: UserRaceInfo) {
        name.text = user.firstName() + " " + user.lastName()
        time.text = StringUtil.minSecMilliFromMs(userRaceInfo.totalTime() ?: 0.0)
    }
}
