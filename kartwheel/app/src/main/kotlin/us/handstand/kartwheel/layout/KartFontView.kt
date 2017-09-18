package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import us.handstand.kartwheel.R


object KartFontView {

    fun setLineSpacingFromAttrs(textView: TextView, context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.lineSpacingMultiplier))
        try {
            textView.setLineSpacing(0f, typedArray.getFloat(0, 1f))
        } finally {
            typedArray.recycle()
        }
    }

    fun setTypefaceFromAttrs(textView: TextView, context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KartTextView, 0, 0)
        try {
            val kartFont = typedArray.getInt(R.styleable.KartTextView_typeface, Font.REGULAR.toInt())
            textView.setTypeface(Font.get[kartFont.toLong()])
        } finally {
            typedArray.recycle()
        }
    }
}