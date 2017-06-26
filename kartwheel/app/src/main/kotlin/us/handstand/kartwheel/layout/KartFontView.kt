package us.handstand.kartwheel.layout

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import us.handstand.kartwheel.R


interface KartFontView {
    fun isInEditMode(): Boolean
    fun getContext(): Context
    fun setLineSpacing(add: Float, mult: Float)
    fun setTypeface(typeface: Typeface?)

    fun setLineSpacingFromAttrs(attrs: AttributeSet?) {
        val typedArray = getContext().obtainStyledAttributes(attrs, intArrayOf(android.R.attr.lineSpacingMultiplier))
        try {
            setLineSpacing(0f, typedArray.getFloat(0, 1f))
        } finally {
            typedArray.recycle()
        }
    }

    fun setTypefaceFromAttrs(attrs: AttributeSet?) {
        if (isInEditMode()) {
            return
        }
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.KartTextView, 0, 0)
        try {
            val kartFont = typedArray.getInt(R.styleable.KartTextView_typeface, Font.REGULAR.toInt())
            setTypeface(Font.get[kartFont.toLong()])
        } finally {
            typedArray.recycle()
        }
    }
}