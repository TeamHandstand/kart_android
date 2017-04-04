package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class TOSScrollView : ScrollView {
    private var scrolledToBottomListen: ScrolledToBottomListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        val child = getChildAt(childCount - 1)
        if (child.bottom - (height + scrollY) == 0) {
            scrolledToBottomListen?.onScrolledToBottom()
        }
    }

    fun setScrolledToBottomListener(listener: ScrolledToBottomListener) {
        scrolledToBottomListen = listener
    }

    companion object {
        interface ScrolledToBottomListener {
            fun onScrolledToBottom()
        }
    }
}


