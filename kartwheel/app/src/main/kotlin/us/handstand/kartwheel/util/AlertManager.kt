package us.handstand.kartwheel.util

import android.text.TextUtils.isEmpty


@Suppress("UNUSED_PARAMETER")
object AlertManager {
    fun showAlert(message: String?, vibrate: Boolean, showFireAction: Boolean, soundName: String?) {
        if (!isEmpty(soundName)) {
            playSound(soundName!!)
        }
        if (vibrate) {
            // TODO: vibrate now, 200ms, 500ms
        }
        if (!isEmpty(message)) {
            // TODO: Generate local notification
        }
    }


    fun playSound(soundName: String) {
        // TODO: Play sound
    }
}