package us.handstand.kartwheel.test.inject.provider

import android.support.design.widget.BottomSheetBehavior
import android.support.test.espresso.IdlingResource
import android.view.View
import dagger.Module
import us.handstand.kartwheel.inject.provider.BottomSheetCallbackProvider


@Module
class IdlingBottomSheetCallbackProvider : BottomSheetCallbackProvider() {
    override fun callback(): BSBCallbackIMPL = IdlingBSBCallbackIMPL()

    class IdlingBSBCallbackIMPL : BSBCallbackIMPL(), IdlingResource {
        private var mIsIdle: Boolean = true
        private var mResourceCallback: IdlingResource.ResourceCallback? = null

        override fun getName(): String = IdlingBSBCallbackIMPL::class.java.simpleName + tag
        override fun isIdleNow(): Boolean = mIsIdle
        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            mResourceCallback = callback
        }

        private fun isIdleState(state: Int): Boolean {
            return state != BottomSheetBehavior.STATE_DRAGGING && state != BottomSheetBehavior.STATE_SETTLING
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            super.onStateChanged(bottomSheet, newState)
            val wasIdle = mIsIdle
            mIsIdle = isIdleState(newState)
            if (!wasIdle && mIsIdle && mResourceCallback != null) {
                mResourceCallback?.onTransitionToIdle()
            }
        }
    }
}