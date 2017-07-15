package us.handstand.kartwheel.layout

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import us.handstand.kartwheel.R


class KartButton : AppCompatButton, KartFontView, ValueAnimator.AnimatorUpdateListener {


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setLineSpacingFromAttrs(attrs)
        setTypefaceFromAttrs(attrs)
        setLoadingParams(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setLineSpacingFromAttrs(attrs)
        setTypefaceFromAttrs(attrs)
        setLoadingParams(attrs)
    }

    private val segment = Path()
    private val paint = Paint()
    private val leftRect = RectF()
    private val rightRect = RectF()
    private var cornerRadius = 0f
    private var totalLength = 0f
    private var centerXLeft = 0f
    private var centerXRight = 0f
    private var centerY = 0f
    private var legLength = 0f
    private var halfCircumference = 0f
    private var animator: ValueAnimator? = null
    var loading: Boolean = false
        set(value) {
            if (value && !field) {
                animator = ValueAnimator.ofFloat(0f, totalLength)
                animator!!.repeatCount = ValueAnimator.INFINITE
                animator!!.duration = 3000L
                animator!!.addUpdateListener(this)
                animator!!.start()
            } else if (animator != null) {
                /*
                if (animator!!.currentPlayTime > animator!!.duration) {
                    animator!!.cancel()
                    animator = null
                } else {
                    postDelayed({
                        animator!!.cancel()
                        animator = null
                    }, animator!!.duration)
                }
                **/
            }
        }

    init {
        paint.style = Paint.Style.STROKE
        setBackgroundResource(R.drawable.button_long)
    }

    fun setLoadingParams(attrs: AttributeSet?) {
        if (isInEditMode) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KartButton, 0, 0)
        try {
            val loadingColor = typedArray.getResourceId(R.styleable.KartButton_loadingSpinnerColor, android.R.color.black)
            val strokeWidth = typedArray.getDimension(R.styleable.KartButton_loadingSpinnerWidth, ViewUtil.dpToPx(context, 2).toFloat())
            cornerRadius = typedArray.getDimension(R.styleable.KartButton_cornerRadius, ViewUtil.dpToPx(context, 33).toFloat())
            halfCircumference = Math.PI.toFloat() * cornerRadius
            paint.color = context.resources.getColor(loadingColor)
            paint.strokeWidth = strokeWidth

        } finally {
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        centerXLeft = cornerRadius
        centerXRight = measuredWidth - cornerRadius
        centerY = cornerRadius
        legLength = centerXRight - centerXLeft
        totalLength = 2 * halfCircumference + 2 * legLength

        leftRect.left = 0f
        leftRect.right = 2 * cornerRadius + leftRect.left
        leftRect.top = 0f
        leftRect.bottom = measuredHeight.toFloat()

        rightRect.right = measuredWidth.toFloat()
        rightRect.left = rightRect.right - 2 * cornerRadius
        rightRect.top = 0f
        rightRect.bottom = measuredHeight.toFloat()

        loading = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!loading && animator?.isRunning == false) {
//            return
        }
        canvas.save()
        canvas.drawPath(segment, paint)
        canvas.restore()
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        // The animatedValue is the value of the left side of the segment
        // and may span from 0 to totalLength - legLength.
        val animatedValue = animation.getAnimatedValue() as Float
        segment.reset()

        val leftArc = getLeftArc(animatedValue)
        segment.addArc(leftRect, leftArc.startAngle, leftArc.sweepAngle)

        val lineMovementTop = getTopLine(animatedValue)
        segment.moveTo(lineMovementTop.start, 0f)
        segment.lineTo(lineMovementTop.end, 0f)

        val rightArc = getRightArc(animatedValue)
        segment.addArc(rightRect, rightArc.startAngle, rightArc.sweepAngle)

        val lineMovementBottom = getBottomLine(animatedValue)
        segment.moveTo(lineMovementBottom.start, centerY + cornerRadius)
        segment.lineTo(lineMovementBottom.end, centerY + cornerRadius)

        invalidate()
    }

    private fun getLeftArc(animatedValue: Float): Arc {
        var startAngle = 0f
        var sweepAngle = 0f
        if (animatedValue <= halfCircumference) {
            startAngle = 270f
            sweepAngle = -((halfCircumference - animatedValue) / halfCircumference) * 180f
        }
        return Arc(startAngle, sweepAngle)
    }

    private fun getRightArc(animatedValue: Float): Arc {
        val startOfRightArc = totalLength - legLength - halfCircumference
        val endOfRightArc = totalLength - legLength
        // Animated value (left side) will have something contained within the half circle if the value + halfCircumference is within the half circle
        val amountWithinHalfCircle = halfCircumference - /* amount to animate in circle */(totalLength - legLength - animatedValue)
        var valueStartInCircle = 0f
        var valueEndInCircle = 0f
        var startAngle = 0f
        var sweepAngle = 0f
        if (animatedValue < startOfRightArc && animatedValue > startOfRightArc - halfCircumference) {
            // animated value is before start of circle
            valueEndInCircle = (animatedValue + halfCircumference) - startOfRightArc
            // Start at 270deg plus the remainder of the sweep angle and continue clockwise up to 180deg
            startAngle = 270f
            // sweepAngle will be the fraction of valueStartInCircle to halfCircumference * 180deg
            sweepAngle = (valueEndInCircle / halfCircumference) * 180f
        } else if (animatedValue >= startOfRightArc && animatedValue < endOfRightArc) {
            // animated value is in the circle
            valueStartInCircle = endOfRightArc - animatedValue
            valueEndInCircle = halfCircumference

            startAngle = 270f + (1 - valueStartInCircle / valueEndInCircle) * 180f
            sweepAngle =180f - (1 - valueStartInCircle / valueEndInCircle) * 180f
        }
        return Arc(startAngle, sweepAngle)
    }

    private fun getTopLine(animatedValue: Float): LineMovement {
        var start = 0f
        var end = 0f
        if (animatedValue > 0f && animatedValue < halfCircumference + legLength) {
            if (animatedValue > halfCircumference) {
                start = centerXLeft + (animatedValue - halfCircumference)
            } else {
                start = centerXLeft
            }
            if (animatedValue < halfCircumference) {
                end = start + animatedValue
            } else {
                end = start + halfCircumference
            }
            if (end > centerXRight) {
                end = centerXRight + paint.strokeWidth
            }
        }
        return LineMovement(start, end)
    }

    private fun getBottomLine(animatedValue: Float): LineMovement {
        var start = 0f
        var end = 0f
        val lineStart = totalLength - legLength
        val lineEnd = totalLength
        if (animatedValue >= lineStart - halfCircumference) {
            if (animatedValue > lineStart) {
                start = centerXRight - (animatedValue - lineStart)
                end = start - halfCircumference
            } else {
                start = centerXRight
                end = start - (animatedValue - (lineStart - halfCircumference))
            }
            if (end < centerXLeft) {
                end = centerXLeft
            }
        }
        return LineMovement(start, end)
    }

    private data class LineMovement(val start: Float, val end: Float)

    private data class Arc(val startAngle: Float, val sweepAngle: Float)
}