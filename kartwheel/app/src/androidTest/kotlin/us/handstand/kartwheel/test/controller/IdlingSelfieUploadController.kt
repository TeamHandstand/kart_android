package us.handstand.kartwheel.test.controller

import android.content.Context
import android.support.test.espresso.IdlingResource
import us.handstand.kartwheel.controller.SelfieUploadController
import us.handstand.kartwheel.network.storage.TransferState
import javax.inject.Inject

/**
 * When the SelfieUploadController becomes COMPLETED, we need to wait for the server to respond with our URL.
 * But, when the SelfieUploadControllerListener becomes COMPLETED, we are done waiting
 */
class IdlingSelfieUploadController @Inject constructor(val idlingResource: SelfieUploadIdlingResource, context: Context) : SelfieUploadController(context) {
    // Backing field
    private var listener_: SelfieUploadControllerListener? = null
    override var listener: SelfieUploadControllerListener?
        get() {
            return listenerInterceptor
        }
        set(value) {
            listener_ = value
        }
    val listenerInterceptor = object : SelfieUploadControllerListener {
        override fun onStateChanged(state: TransferState) {
            listener_?.onStateChanged(state)
            if (state == TransferState.COMPLETED) {
                idlingResource.isIdle = true
                idlingResource.resourceCallback?.onTransitionToIdle()
            }
        }

        override fun showMessage(stringResId: Int) {}
    }

    override fun onStateChanged(id: Int, state: TransferState) {
        if (state == TransferState.COMPLETED) {
            idlingResource.isIdle = false
        }
        super.onStateChanged(id, state)
    }

    class SelfieUploadIdlingResource : IdlingResource {
        var isIdle: Boolean = true
        var resourceCallback: IdlingResource.ResourceCallback? = null

        fun unregister() {
            isIdle = true
            resourceCallback = null
        }

        override fun getName(): String = IdlingSelfieUploadController::class.java.simpleName
        override fun isIdleNow(): Boolean = isIdle

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            resourceCallback = callback
        }
    }
}