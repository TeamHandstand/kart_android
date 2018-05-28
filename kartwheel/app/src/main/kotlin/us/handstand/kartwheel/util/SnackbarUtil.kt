package us.handstand.kartwheel.util

import android.app.Activity
import android.support.design.widget.Snackbar
import us.handstand.kartwheel.R


object SnackbarUtil {
    var currentSnackbar: Snackbar? = null

    fun show(activity: Activity?, id: Int) {
        show(activity, activity?.resources?.getString(id) ?: "")
    }

    fun show(activity: Activity?, message: CharSequence) {
        if (activity == null) {
            return
        }
        if (currentSnackbar != null && currentSnackbar?.isShown == true) {
            currentSnackbar?.dismiss()
        }
        currentSnackbar = Snackbar.make(activity.findViewById(R.id.parent), message, Snackbar.LENGTH_SHORT)
        currentSnackbar?.setAction(android.R.string.ok, {
            currentSnackbar?.dismiss()
        })
        activity.runOnUiThread {
            currentSnackbar?.show()
        }
    }
}