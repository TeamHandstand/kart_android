package us.handstand.kartwheel.inject.provider

import android.support.design.widget.BottomSheetBehavior
import android.view.View
import dagger.Module
import dagger.Provides


@Module
open class BottomSheetCallbackProvider {
    @Provides open fun callback(): BSBCallbackIMPL = BSBCallbackIMPL()

    open class BSBCallbackIMPL : BottomSheetBehavior.BottomSheetCallback() {
        var delegate: BottomSheetBehavior.BottomSheetCallback? = null
        var layoutId: Int = 0

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            delegate?.onSlide(bottomSheet, slideOffset)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            delegate?.onStateChanged(bottomSheet, newState)
        }
    }
}