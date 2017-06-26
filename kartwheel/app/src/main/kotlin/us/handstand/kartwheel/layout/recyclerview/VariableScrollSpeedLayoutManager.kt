package us.handstand.kartwheel.layout.recyclerview

import android.content.Context
import android.graphics.PointF
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.DisplayMetrics


class VariableScrollSpeedLayoutManager : LinearLayoutManager {
    constructor(context: Context) : super(context)
    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    // Higher values scroll slower (since the scroll time is speed/distance, where distance is constant)
    var scrollSpeed: Float = 0f

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        val linearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {

            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@VariableScrollSpeedLayoutManager.computeScrollVectorForPosition(targetPosition)
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 2f
                //return if (scrollSpeed > 0) scrollSpeed / displayMetrics.densityDpi else 1f
            }
        }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }
}
