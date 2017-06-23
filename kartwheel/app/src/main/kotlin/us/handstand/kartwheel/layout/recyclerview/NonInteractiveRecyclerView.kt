package us.handstand.kartwheel.layout.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import us.handstand.kartwheel.layout.recyclerview.adapter.EndlessItemAdapter

class NonInteractiveRecyclerView : RecyclerView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init {
        adapter = EndlessItemAdapter()
        layoutManager = VariableScrollSpeedLayoutManager(context, HORIZONTAL, false)
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return true
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return true
    }
}
