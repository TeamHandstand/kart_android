package us.handstand.kartwheel.util

import android.text.TextUtils.isEmpty


object AlertManager {
    fun showAlert(message: String?, vibrate: Boolean, showFireAction: Boolean, soundName: String?) {
        if (!isEmpty(soundName)) {
            playSound(soundName!!)
        }
        if (vibrate) {
            // TODO: vibrate now, 200ms, 500ms
        }
        if (!isEmpty(message)) {
            generateLocalNotification(message!!, showFireAction)
        }
    }

    private fun generateLocalNotification(pushMessage: String, showFireAction: Boolean) {
        // TODO: Show notification
    }

    fun playSound(soundName: String) {
        // TODO: Play sound
    }
}