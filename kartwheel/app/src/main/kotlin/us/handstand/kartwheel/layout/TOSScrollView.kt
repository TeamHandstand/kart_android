package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class TOSScrollView : ScrollView {
    private var scrolledToBottomListen: (() -> Unit)? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        val child = getChildAt(childCount - 1)
        if (child.bottom - (height + scrollY) == 0) {
            scrolledToBottomListen?.invoke()
        }
    }

    fun setScrolledToBottomListener(listener: () -> Unit) {
        scrolledToBottomListen = listener
    }
}


