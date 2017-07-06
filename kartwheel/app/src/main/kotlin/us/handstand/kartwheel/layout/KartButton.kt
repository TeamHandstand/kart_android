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
            if (value) {
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
        if (animatedValue <= halfCircumference) {
            segment.addArc(leftRect, 270f, -((halfCircumference - animatedValue) / halfCircumference) * 180f)
        }
        if (animatedValue > 0f && animatedValue < halfCircumference + legLength) {
            val start: Float
            if (animatedValue > halfCircumference) {
                start = centerXLeft + (animatedValue - halfCircumference)
            } else {
                start = centerXLeft
            }
            segment.moveTo(start, 0f)
            var end: Float
            if (animatedValue < halfCircumference) {
                end = start + animatedValue
            } else {
                end = start + halfCircumference
            }
            if (end > centerXRight) {
                end = centerXRight + paint.strokeWidth
            }
            segment.lineTo(end, 0f)
        }

        // Animated value (left side) will have something contained within the half circle if the value + halfCircumference is within the half circle
        if (animatedValue + halfCircumference > (halfCircumference + legLength) && animatedValue < 2 * halfCircumference + legLength) {
            // Has to grow and shrink
            segment.addArc(rightRect, 270f, -((animatedValue - (legLength + halfCircumference)) / halfCircumference) * 180f)
        }

        if (animatedValue > totalLength - (legLength + halfCircumference)) {
            val start: Float
            if (animatedValue > totalLength - legLength) {
                start = centerXRight - (animatedValue - (totalLength - legLength))
            } else {
                start = centerXRight
            }
            segment.moveTo(start + paint.strokeWidth, centerY + cornerRadius)
            var end: Float
            if (animatedValue > totalLength - legLength) {
                end = start - halfCircumference
            } else {
                end = start - (animatedValue - (totalLength - legLength))
            }
            if (end < centerXLeft + paint.strokeWidth) {
                end = centerXLeft + paint.strokeWidth
            }
            segment.lineTo(end, centerY + cornerRadius)
        }

        invalidate()
    }
}