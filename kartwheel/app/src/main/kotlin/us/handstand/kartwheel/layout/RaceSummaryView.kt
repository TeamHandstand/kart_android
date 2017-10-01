package us.handstand.kartwheel.layout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.recyclerview.binding.RaceListBinding
import us.handstand.kartwheel.util.Permissions
import us.handstand.kartwheel.util.StringUtil


class RaceSummaryView : ConstraintLayout {
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
        setBackgroundResource(R.drawable.background_race_list_content)
        val lp = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        val eightDp = ViewUtil.dpToPx(context, 8)
        lp.setMargins(eightDp, eightDp, eightDp, eightDp / 2)
        layoutParams = lp
        if (Permissions.hasApi(Build.VERSION_CODES.LOLLIPOP)) {
            elevation = ViewUtil.dpToPx(context, 5).toFloat()
        }
        startTime = findViewById(R.id.startTime)
        raceName = findViewById(R.id.raceName)
        details = findViewById(R.id.raceDetails)
        spotsLeft = findViewById(R.id.spotsLeft)
        avatar = findViewById(R.id.avatar)
    }

    fun bind(binding: RaceListBinding, valueAnimator: ValueAnimator) {
        timerRunning = binding.timerRunning
        raceName.text = binding.raceName
        details.text = binding.detailsText
        spotsLeft.text = binding.spotsLeftText
        spotsLeft.setTextSize(TypedValue.COMPLEX_UNIT_DIP, binding.spotsLeftSize)
        spotsLeft.setTextColor(binding.spotsLeftColor)
        startTime.text = binding.startTimeText
        startTime.setBackgroundResource(binding.backgroundResId)
        alpha = binding.alpha
        avatar.visibility = binding.avatarVisibility
        if (binding.avatarVisibility == View.VISIBLE) {
            avatar.setImageUrl(binding.avatarUrl)
        }
        if (binding.timerRunning) {
            valueAnimator.cancel()
            valueAnimator.duration = binding.animationTime
            valueAnimator.removeAllUpdateListeners()
            valueAnimator.removeAllListeners()
            valueAnimator.addUpdateListener {
                if (timerRunning) {
                    spotsLeft.text = StringUtil.hourMinSecFromMs(binding.timeUntilRace - ((it.animatedValue as Float) * binding.timeUntilRace).toLong())
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
            valueAnimator.start()
        }

    }
}
