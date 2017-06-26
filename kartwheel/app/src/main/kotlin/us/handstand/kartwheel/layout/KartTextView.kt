package us.handstand.kartwheel.layout

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet


class KartTextView : AppCompatTextView, KartFontView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setLineSpacingFromAttrs(attrs)
        setTypefaceFromAttrs(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setLineSpacingFromAttrs(attrs)
        setTypefaceFromAttrs(attrs)
    }
}