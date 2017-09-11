package us.handstand.kartwheel.util

import android.app.Activity
import android.support.design.widget.Snackbar
import us.handstand.kartwheel.R


object SnackbarUtil {

    fun show(activity: Activity, id: Int) {
        show(activity, activity.resources.getString(id))
    }

    fun show(activity: Activity, message: CharSequence) {
        activity.runOnUiThread {
            val snackbar = Snackbar.make(activity.findViewById(R.id.parent), message, Snackbar.LENGTH_SHORT)
            snackbar.setAction(android.R.string.ok, {
                snackbar.dismiss()
            })
            snackbar.show()
        }
    }
}