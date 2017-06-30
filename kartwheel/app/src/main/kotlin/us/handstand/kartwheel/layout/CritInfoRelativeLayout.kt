package us.handstand.kartwheel.layout

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import us.handstand.kartwheel.R


class CritInfoRelativeLayout : RelativeLayout, ValueAnimator.AnimatorUpdateListener {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val radius = ViewUtil.dpToPx(context, 55)
    val circumference = 2 * Math.PI.toFloat() * radius
    var totalLength = 0f
    val paint = Paint()
    val circleLeft = RectF()
    val circleRight = RectF()
    val segment = Path()
    var leftIsSelected = false
    var rightIsSelected = false
    var currentPos = 0f
    var centerXLeft = 0f
    var centerXRight = 0f
    var centerY = 0f
    val leftImageView: ImageView
    val rightImageView: ImageView
    val question: TextView


    init {
        View.inflate(context, R.layout.view_crit_info, this)
        leftImageView = ViewUtil.findView(this, R.id.leftImage)
        rightImageView = ViewUtil.findView(this, R.id.rightImage)
        question = ViewUtil.findView(this, R.id.question)
        paint.color = context.resources.getColor(R.color.yellow)
        paint.strokeWidth = ViewUtil.dpToPx(context, 2).toFloat()
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeJoin = Paint.Join.ROUND
        setWillNotDraw(false)
    }

    fun leftImageTapped() {
        animateLineTo(0f, true)
        leftIsSelected = true
        rightIsSelected = false
    }

    fun rightImageTapped() {
        animateLineTo(totalLength - circumference, false)
        leftIsSelected = false
        rightIsSelected = true
    }

    fun nextQuestion(leftImageRes: Int, rightImageRes: Int, questionRes: Int, colorRes: Int) {
        leftImageView.setImageResource(leftImageRes)
        rightImageView.setImageResource(rightImageRes)
        question.setText(questionRes)
        currentPos = 0f
        updateCoords()
        paint.color = context.resources.getColor(colorRes)
        if (leftIsSelected) {
            val animator = ValueAnimator.ofFloat(360f, 0f)
            animator.duration = 300L
            animator.addUpdateListener {
                segment.reset()
                val animatedValue = it.getAnimatedValue() as Float
                segment.addArc(circleLeft, 270f, -animatedValue)
                invalidate()
            }
            animator.start()
        } else if (rightIsSelected) {
            val animator = ValueAnimator.ofFloat(360f, 0f)
            animator.duration = 300L
            animator.addUpdateListener {
                segment.reset()
                val animatedValue = it.getAnimatedValue() as Float
                segment.addArc(circleRight, -90f, animatedValue)
                invalidate()
            }
            animator.start()
        }
        leftIsSelected = false
        rightIsSelected = false
    }

    fun animateLineTo(start: Float, leftSelection: Boolean) {
        updateCoords()
        if ((leftIsSelected && leftSelection) || (rightIsSelected && !leftSelection)) {
            return
        }
        if (!leftIsSelected && !rightIsSelected) {
            if (leftSelection) {
                val animator = ValueAnimator.ofFloat(0f, 360f)
                animator.duration = 300L
                animator.addUpdateListener {
                    segment.reset()
                    val animatedValue = it.getAnimatedValue() as Float
                    segment.addArc(circleLeft, 270f, -animatedValue)
                    invalidate()
                }
                animator.start()
            } else {
                val animator = ValueAnimator.ofFloat(0f, 360f)
                animator.duration = 300L
                animator.addUpdateListener {
                    segment.reset()
                    val animatedValue = it.getAnimatedValue() as Float
                    segment.addArc(circleRight, -90f, animatedValue)
                    invalidate()
                }
                animator.start()
            }
        } else {
            val animator = ValueAnimator.ofFloat(currentPos, start)
            animator.duration = 300L
            animator.addUpdateListener(this)
            animator.start()
        }
        currentPos = start
    }


    fun updateCoords() {
        centerXLeft = leftImageView.left + (leftImageView.measuredWidth / 2f)
        centerXRight = rightImageView.left + (rightImageView.measuredWidth / 2f)
        centerY = rightImageView.bottom - (rightImageView.measuredHeight / 2f)
        totalLength = 2 * circumference + (centerXRight - centerXLeft)

        circleLeft.left = centerXLeft - radius
        circleLeft.top = 0f
        circleLeft.right = centerXLeft + radius
        circleLeft.bottom = radius * 2f

        circleRight.left = centerXRight - radius
        circleRight.top = 0f
        circleRight.right = centerXRight + radius
        circleRight.bottom = radius * 2f
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        // The animatedValue is the value of the left side of the segment
        // and may span from 0 to totalLength - circumference.
        // The length of any segment will be equal to circumference.
        val animatedValue = animation.getAnimatedValue() as Float
        segment.reset()
        if (animatedValue <= circumference) {
            segment.addArc(circleLeft, 270f, -((circumference - animatedValue) / circumference) * 360f)
        }
        if (animatedValue > 0f && animatedValue < totalLength - circumference) {
            val start: Float
            if (animatedValue < circumference) {
                start = centerXLeft - paint.strokeWidth
            } else {
                start = centerXLeft - paint.strokeWidth + (animatedValue - circumference)
            }
            segment.moveTo(start, 0f)
            var end: Float
            if (animatedValue < circumference) {
                end = start + animatedValue
            } else {
                end = start + circumference
            }
            if (end > centerXRight + paint.strokeWidth) {
                end = centerXRight + paint.strokeWidth
            }
            segment.lineTo(end, 0f)
        }
        if (animatedValue > totalLength - (2f * circumference)) {
            segment.addArc(circleRight, -90f, ((animatedValue - (totalLength - (2f * circumference))) / circumference) * 360f)
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(0f, centerY - radius.toFloat())
        canvas.drawPath(segment, paint)
        canvas.restore()
    }
}