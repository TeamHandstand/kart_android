package us.handstand.kartwheel.layout.behavior

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IntDef
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import us.handstand.kartwheel.R
import java.lang.ref.WeakReference
import java.util.*

class AnchoredBottomSheetBehavior<V : View> : CoordinatorLayout.Behavior<V> {

    /**
     * Callback for monitoring events about bottom sheets.
     */
    abstract class BottomSheetCallback {

        /**
         * Called when the bottom sheet changes its state.
         * @param bottomSheet The bottom sheet view.
         *
         * @param newState    The new state. This will be one of [STATE_DRAGGING],
         * *                    [STATE_SETTLING], [STATE_ANCHOR_POINT],
         * *                    [STATE_EXPANDED], [STATE_COLLAPSED], or [STATE_HIDDEN].
         */
        abstract fun onStateChanged(bottomSheet: View, @State newState: Long)

        /**
         * Called when the bottom sheet is being dragged.

         * @param bottomSheet The bottom sheet view.
         *
         * @param slideOffset The new offset of this bottom sheet within its range, from 0 to 1
         *                     when it is moving upward, and from 0 to -1 when it moving downward.
         */
        abstract fun onSlide(bottomSheet: View, slideOffset: Float)
    }

    @IntDef(STATE_EXPANDED, STATE_COLLAPSED, STATE_DRAGGING,
            STATE_ANCHOR_POINT, STATE_SETTLING, STATE_HIDDEN)
    @Retention(AnnotationRetention.SOURCE)
    annotation class State

    private val anchorPoint: Int
    private val minimumVelocity: Float
    /**
     * Gets/sets the height of the bottom sheet when it is collapsed in pixels.
     *
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Params_behavior_peekHeight
     */
    var peekHeight: Int = 0
        set(peekHeight) {
            field = Math.max(0, peekHeight)
            maxOffset = parentHeight - peekHeight
        }
    /**
     * Gets/sets whether this bottom sheet can hide when it is swiped down.
     *
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Params_behavior_hideable
     *
     * `true` if this bottom sheet can hide.
     */
    private var isHideable: Boolean = false
    private var minOffset: Int = 0
    private var maxOffset: Int = 0
    @State private var mState = STATE_ANCHOR_POINT
    @State private var lastStableState = STATE_ANCHOR_POINT
    private var viewDragHelper: ViewDragHelper? = null
    private var ignoreEvents: Boolean = false
    private var nestedScrolled: Boolean = false
    private var parentHeight: Int = 0
    private var viewRef: WeakReference<V>? = null
    private var nestedScrollingChildRef: WeakReference<View>? = null
    private var bottomSheetCallbacks: Vector<BottomSheetCallback>? = null
    private var activePointerId: Int = 0
    private var initialY: Int = 0
    private var touchingScrollingChild: Boolean = false

    @Suppress("unused")
    constructor(context: Context) : super() {
        anchorPoint = DEFAULT_ANCHOR_POINT.toInt()
        minimumVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity.toFloat()
    }

    /**
     * Default constructor for inflating BottomSheetBehaviors from layout.
     * @param context The [Context].
     * @param attrs   The [AttributeSet].
     */
    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // Get Android design library attributes
        var a = context.obtainStyledAttributes(attrs, android.support.design.R.styleable.BottomSheetBehavior_Layout)
        try {
            peekHeight = a.getDimensionPixelSize(android.support.design.R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight, 0)
            isHideable = a.getBoolean(android.support.design.R.styleable.BottomSheetBehavior_Layout_behavior_hideable, false)
        } finally {
            a.recycle()
        }

        // Get the custom anchorPoint
        a = context.obtainStyledAttributes(attrs, R.styleable.AnchoredBottomSheetBehavior)
        try {
            anchorPoint = a.getDimension(R.styleable.AnchoredBottomSheetBehavior_anchorPoint, DEFAULT_ANCHOR_POINT).toInt()
        } finally {
            a.recycle()
        }
        minimumVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity.toFloat()
    }

    override fun onSaveInstanceState(parent: CoordinatorLayout?, child: V?): Parcelable {
        return SavedState(super.onSaveInstanceState(parent, child), mState)
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout?, child: V?, state: Parcelable?) {
        val ss = state as SavedState?
        super.onRestoreInstanceState(parent, child, ss!!.superState)
        // Intermediate states are restored as collapsed state
        if (ss.state == STATE_DRAGGING || ss.state == STATE_SETTLING) {
            mState = STATE_COLLAPSED
        } else {
            mState = ss.state
        }

        lastStableState = mState
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        // First let the parent lay it out
        if (mState != STATE_DRAGGING && mState != STATE_SETTLING) {
            if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
                ViewCompat.setFitsSystemWindows(child, true)
            }
            parent.onLayoutChild(child, layoutDirection)
        }
        // Offset the bottom sheet
        parentHeight = parent.height
        minOffset = Math.max(0, parentHeight - child.height)
        maxOffset = Math.max(parentHeight - peekHeight, minOffset)

        if (mState == STATE_ANCHOR_POINT) {
            ViewCompat.offsetTopAndBottom(child, anchorPoint)
        } else if (mState == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, minOffset)
        } else if (isHideable && mState == STATE_HIDDEN) {
            ViewCompat.offsetTopAndBottom(child, parentHeight)
        } else if (mState == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, maxOffset)
        }
        if (viewDragHelper == null) {
            viewDragHelper = ViewDragHelper.create(parent, dragCallback)
        }
        viewRef = WeakReference(child)
        nestedScrollingChildRef = WeakReference<View>(findScrollingChild(child))
        return true
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        if (!child.isShown) {
            return false
        }

        val action = MotionEventCompat.getActionMasked(event)
        if (action == MotionEvent.ACTION_DOWN) {
            reset()
        }

        when (action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchingScrollingChild = false
                activePointerId = MotionEvent.INVALID_POINTER_ID
                // Reset the ignore flag
                if (ignoreEvents) {
                    ignoreEvents = false
                    return false
                }
            }
            MotionEvent.ACTION_DOWN -> {
                val initialX = event.x.toInt()
                initialY = event.y.toInt()
                if (mState == STATE_ANCHOR_POINT) {
                    activePointerId = event.getPointerId(event.actionIndex)
                    touchingScrollingChild = true
                } else {
                    val scroll = nestedScrollingChildRef!!.get()
                    if (scroll != null && parent.isPointInChildBounds(scroll, initialX, initialY)) {
                        activePointerId = event.getPointerId(event.actionIndex)
                        touchingScrollingChild = true
                    }
                }
                ignoreEvents = activePointerId == MotionEvent.INVALID_POINTER_ID && !parent.isPointInChildBounds(child, initialX, initialY)
            }
            MotionEvent.ACTION_MOVE -> {
            }
        }

        if (action == MotionEvent.ACTION_CANCEL) {
            // We don't want to trigger a BottomSheet fling as a result of a Cancel MotionEvent (e.g., parent horizontal scroll view taking over touch events)
            scrollVelocityTracker.clear()
        }

        if (!ignoreEvents && viewDragHelper!!.shouldInterceptTouchEvent(event)) {
            return true
        }
        // We have to handle cases that the ViewDragHelper does not capture the bottom sheet because
        // it is not the top most view of its parent. This is not necessary when the touch event is
        // happening over the scrolling content as nested scrolling logic handles that case.
        val scroll = nestedScrollingChildRef!!.get()
        val ret = action == MotionEvent.ACTION_MOVE && scroll != null &&
                !ignoreEvents && mState != STATE_DRAGGING &&
                !parent.isPointInChildBounds(scroll, event.x.toInt(), event.y.toInt()) &&
                Math.abs(initialY - event.y) > viewDragHelper!!.touchSlop
        return ret
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        if (!child.isShown) {
            return false
        }

        val action = MotionEventCompat.getActionMasked(event)
        if (mState == STATE_DRAGGING && action == MotionEvent.ACTION_DOWN) {
            return true
        }

        viewDragHelper!!.processTouchEvent(event)

        if (action == MotionEvent.ACTION_DOWN) {
            reset()
        }

        // The ViewDragHelper tries to capture only the top-most View. We have to explicitly tell it
        // to capture the bottom sheet in case it is not captured and the touch slop is passed.
        if (action == MotionEvent.ACTION_MOVE && !ignoreEvents) {
            if (Math.abs(initialY - event.y) > viewDragHelper!!.touchSlop) {
                viewDragHelper!!.captureChildView(child, event.getPointerId(event.actionIndex))
            }
        }
        return !ignoreEvents
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View?, target: View?, nestedScrollAxes: Int): Boolean {
        nestedScrolled = false
        return nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    private val scrollVelocityTracker = object {
        private var previousScrollTime: Long = 0L
        var scrollVelocity = 0f
            private set

        fun recordScroll(dy: Int) {
            val now = System.currentTimeMillis()
            if (previousScrollTime != 0L) {
                val elapsed = now - previousScrollTime
                scrollVelocity = dy.toFloat() / elapsed * 1000 // pixels per sec
            }
            previousScrollTime = now
        }

        fun clear() {
            previousScrollTime = 0
            scrollVelocity = 0f
        }
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View?, dx: Int, dy: Int, consumed: IntArray) {
        val scrollingChild = nestedScrollingChildRef!!.get()
        if (target !== scrollingChild) {
            return
        }

        scrollVelocityTracker.recordScroll(dy)

        val currentTop = child.top
        val newTop = currentTop - dy

        // Force stop at the anchor - do not go from collapsed to expanded in one scroll.
        if ((lastStableState == STATE_COLLAPSED && newTop < anchorPoint)
                || (lastStableState == STATE_EXPANDED && newTop > anchorPoint)) {
            consumed[1] = dy
            ViewCompat.offsetTopAndBottom(child, anchorPoint - currentTop)
            dispatchOnSlide(child.top)
            nestedScrolled = true
            setStateInternal(STATE_ANCHOR_POINT)
            return
        }

        if (dy > 0) { // Upward
            if (newTop < minOffset) {
                consumed[1] = currentTop - minOffset
                ViewCompat.offsetTopAndBottom(child, -consumed[1])
                setStateInternal(STATE_EXPANDED)
            } else {
                consumed[1] = dy
                ViewCompat.offsetTopAndBottom(child, -dy)
                setStateInternal(STATE_DRAGGING)
            }
        } else if (dy < 0) { // Downward
            if (!ViewCompat.canScrollVertically(target, -1)) {
                if (newTop <= maxOffset || isHideable) {
                    consumed[1] = dy
                    ViewCompat.offsetTopAndBottom(child, -dy)
                    setStateInternal(STATE_DRAGGING)
                } else {
                    consumed[1] = currentTop - maxOffset
                    ViewCompat.offsetTopAndBottom(child, -consumed[1])
                    setStateInternal(STATE_COLLAPSED)
                }
            }
        }
        dispatchOnSlide(child.top)
        nestedScrolled = true
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View?) {
        if (child.top == minOffset) {
            setStateInternal(STATE_EXPANDED)
            lastStableState = STATE_EXPANDED
            return
        }
        if (target !== nestedScrollingChildRef!!.get() || !nestedScrolled) {
            return
        }
        val top: Int
        val targetState: Long

        // Are we flinging up?
        val scrollVelocity = scrollVelocityTracker.scrollVelocity
        if (scrollVelocity > minimumVelocity) {
            if (lastStableState == STATE_COLLAPSED) {
                // Fling from collapsed to anchor
                top = anchorPoint
                targetState = STATE_ANCHOR_POINT
            } else if (lastStableState == STATE_ANCHOR_POINT) {
                // Fling from anchor to expanded
                top = minOffset
                targetState = STATE_EXPANDED
            } else {
                // We are already expanded
                top = minOffset
                targetState = STATE_EXPANDED
            }

            // Are we flinging down?
        } else if (scrollVelocity < -minimumVelocity) {
            if (lastStableState == STATE_EXPANDED) {
                // Fling to from expanded to anchor
                top = anchorPoint
                targetState = STATE_ANCHOR_POINT
            } else if (lastStableState == STATE_ANCHOR_POINT) {
                // Fling from anchor to collapsed
                top = maxOffset
                targetState = STATE_COLLAPSED
            } else {
                // We are already collapsed
                top = maxOffset
                targetState = STATE_COLLAPSED
            }
            // Not flinging, just settle to the nearest state
        } else {
            // Collapse?
            val currentTop = child.top
            // Multiply by 1.25 to account for parallax. The currentTop needs to be pulled down 50% of the anchor point before collapsing.
            if (currentTop > anchorPoint) {
                top = maxOffset
                targetState = STATE_COLLAPSED
            } else if (currentTop < anchorPoint) {
                top = minOffset
                targetState = STATE_EXPANDED
            } else {
                top = anchorPoint
                targetState = STATE_ANCHOR_POINT
            }// Snap back to the anchor
            // Expand?
        }

        lastStableState = targetState
        if (viewDragHelper!!.smoothSlideViewTo(child, child.left, top)) {
            setStateInternal(STATE_SETTLING)
            ViewCompat.postOnAnimation(child, SettleRunnable(child, targetState))
        } else {
            setStateInternal(targetState)
        }
        nestedScrolled = false
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout?, child: V?, target: View?, velocityX: Float, velocityY: Float): Boolean {
        return target === nestedScrollingChildRef!!.get()
                && (mState != STATE_EXPANDED || super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY))
    }

    /**
     * Adds a callback to be notified of bottom sheet events.
     *
     * @param callback The callback to notify when bottom sheet events occur.
     */
    fun addBottomSheetCallback(callback: BottomSheetCallback) {
        if (bottomSheetCallbacks == null) {
            bottomSheetCallbacks = Vector<BottomSheetCallback>()
        }
        bottomSheetCallbacks!!.add(callback)
    }

    /**
     * Gets and sets the current state of the bottom sheet
     * to STATE_EXPANDED, STATE_ANCHOR_POINT, STATE_COLLAPSED,
     * STATE_DRAGGING, and STATE_SETTLING
     */
    var state: Long
        @State
        get() = mState
        set(@State state) {
            if (state == mState) {
                return
            }
            if (viewRef == null) {
                if (state == STATE_COLLAPSED || state == STATE_EXPANDED || state == STATE_ANCHOR_POINT || (isHideable && state == STATE_HIDDEN)) {
                    mState = state
                    lastStableState = state
                }
                return
            }
            val child = viewRef!!.get() ?: return
            val top: Int
            if (state == STATE_COLLAPSED) {
                top = maxOffset
            } else if (state == STATE_ANCHOR_POINT) {
                top = anchorPoint
            } else if (state == STATE_EXPANDED) {
                top = minOffset
            } else if (isHideable && state == STATE_HIDDEN) {
                top = parentHeight
            } else {
                throw IllegalArgumentException("Illegal state argument: " + state)
            }
            setStateInternal(STATE_SETTLING)
            if (viewDragHelper!!.smoothSlideViewTo(child, child.left, top)) {
                ViewCompat.postOnAnimation(child, SettleRunnable(child, state))
            }
        }

    private fun setStateInternal(@State state: Long) {
        if (mState == state) {
            return
        }
        mState = state
        val bottomSheet = viewRef!!.get()
        if (bottomSheet != null && bottomSheetCallbacks != null) {
            //            bottomSheetCallbacks.onStateChanged(bottomSheet, state);
            notifyStateChangedToListeners(bottomSheet, state)
        }
    }

    private fun notifyStateChangedToListeners(bottomSheet: View, @State newState: Long) {
        for (bottomSheetCallback in bottomSheetCallbacks!!) {
            bottomSheetCallback.onStateChanged(bottomSheet, newState)
        }
    }

    private fun notifyOnSlideToListeners(bottomSheet: View, slideOffset: Float) {
        for (bottomSheetCallback in bottomSheetCallbacks!!) {
            bottomSheetCallback.onSlide(bottomSheet, slideOffset)
        }
    }

    private fun reset() {
        activePointerId = ViewDragHelper.INVALID_POINTER
    }

    private fun shouldHide(child: View, yvel: Float): Boolean {
        if (child.top < maxOffset) {
            // It should not hide, but collapse.
            return false
        }
        val newTop = child.top + yvel * HIDE_FRICTION
        return Math.abs(newTop - maxOffset) / peekHeight.toFloat() > HIDE_THRESHOLD
    }

    private fun findScrollingChild(view: View): View? {
        if (view is NestedScrollingChild) {
            return view
        }
        if (view is ViewGroup) {
            val group = view
            var i = 0
            val count = group.childCount
            while (i < count) {
                val scrollingChild = findScrollingChild(group.getChildAt(i))
                if (scrollingChild != null) {
                    return scrollingChild
                }
                i++
            }
        }
        return null
    }

    private val dragCallback = object : ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (mState == STATE_DRAGGING) {
                return false
            }
            if (touchingScrollingChild) {
                return false
            }
            if (mState == STATE_EXPANDED && activePointerId == pointerId) {
                val scroll = nestedScrollingChildRef!!.get()
                if (scroll != null && ViewCompat.canScrollVertically(scroll, -1)) {
                    // Let the content scroll up
                    return false
                }
            }
            return viewRef != null && viewRef!!.get() === child
        }

        override fun onViewPositionChanged(changedView: View?, left: Int, top: Int, dx: Int, dy: Int) {
            dispatchOnSlide(top)
        }

        override fun onViewDragStateChanged(state: Int) {
            if (state == ViewDragHelper.STATE_DRAGGING) {
                setStateInternal(STATE_DRAGGING)
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val top: Int
            @State val targetState: Long
            if (yvel < 0) { // Moving up
                top = minOffset
                targetState = STATE_EXPANDED
            } else if (isHideable && shouldHide(releasedChild, yvel)) {
                top = parentHeight
                targetState = STATE_HIDDEN
            } else if (yvel == 0f) {
                val currentTop = releasedChild.top
                if (Math.abs(currentTop - minOffset) < Math.abs(currentTop - maxOffset)) {
                    top = minOffset
                    targetState = STATE_EXPANDED
                } else {
                    top = maxOffset
                    targetState = STATE_COLLAPSED
                }
            } else {
                top = maxOffset
                targetState = STATE_COLLAPSED
            }
            if (viewDragHelper!!.settleCapturedViewAt(releasedChild.left, top)) {
                setStateInternal(STATE_SETTLING)
                ViewCompat.postOnAnimation(releasedChild,
                        SettleRunnable(releasedChild, targetState))
            } else {
                setStateInternal(targetState)
            }
        }

        override fun clampViewPositionVertical(child: View?, top: Int, dy: Int): Int {
            return constrain(top, minOffset, if (isHideable) parentHeight else maxOffset)
        }

        internal fun constrain(amount: Int, low: Int, high: Int): Int {
            return if (amount < low) low else if (amount > high) high else amount
        }

        override fun clampViewPositionHorizontal(child: View?, left: Int, dx: Int): Int {
            return child!!.left
        }

        override fun getViewVerticalDragRange(child: View?): Int {
            if (isHideable) {
                return parentHeight - minOffset
            } else {
                return maxOffset - minOffset
            }
        }
    }

    private fun dispatchOnSlide(top: Int) {
        val bottomSheet = viewRef!!.get()
        if (bottomSheet != null && bottomSheetCallbacks != null) {
            if (top > maxOffset && lastStableState != STATE_COLLAPSED) {
                notifyOnSlideToListeners(bottomSheet, (maxOffset - top).toFloat() / peekHeight)
            } else {
                notifyOnSlideToListeners(bottomSheet, (maxOffset - top).toFloat() / (maxOffset - minOffset))
            }
        }
    }

    private inner class SettleRunnable internal constructor(private val mView: View, @param:State private val mTargetState: Long) : Runnable {
        override fun run() {
            if (viewDragHelper != null && viewDragHelper!!.continueSettling(true)) {
                ViewCompat.postOnAnimation(mView, this)
            } else {
                setStateInternal(mTargetState)
            }
        }
    }

    private class SavedState : View.BaseSavedState {

        @State
        internal val state: Long

        constructor(source: Parcel) : super(source) {
            state = source.readLong()
        }

        constructor(superState: Parcelable, @State state: Long) : super(superState) {
            this.state = state
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeLong(state)
        }

        companion object {
            @Suppress("unused")
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {

        /**
         * The bottom sheet is dragging.
         */
        const val STATE_DRAGGING = 1L

        /**
         * The bottom sheet is settling.
         */
        const val STATE_SETTLING = 2L

        /**
         * The bottom sheet is expanded to the anchor point
         */
        const val STATE_ANCHOR_POINT = 3L

        /**
         * The bottom sheet is expanded.
         */
        const val STATE_EXPANDED = 4L

        /**
         * The bottom sheet is collapsed.
         */
        const val STATE_COLLAPSED = 5L

        /**
         * The bottom sheet is hidden.
         */
        const val STATE_HIDDEN = 6L

        private val HIDE_THRESHOLD = 0.5f

        private val HIDE_FRICTION = 0.1f

        private val DEFAULT_ANCHOR_POINT = 200f

        /**
         * A utility function to get the [AnchoredBottomSheetBehavior] associated with the `view`.
         * @param view The [View] with [AnchoredBottomSheetBehavior].
         * @return The [AnchoredBottomSheetBehavior] associated with the `view`.
         */
        fun <V : View> from(view: V): AnchoredBottomSheetBehavior<V> {
            val params = view.layoutParams as? CoordinatorLayout.LayoutParams ?: throw IllegalArgumentException("The view is not a child of CoordinatorLayout")
            val behavior = params.behavior as? AnchoredBottomSheetBehavior<*> ?: throw IllegalArgumentException("The view is not associated with " + AnchoredBottomSheetBehavior::class.java.simpleName)
            @Suppress("UNCHECKED_CAST")
            return behavior as AnchoredBottomSheetBehavior<V>
        }
    }

}