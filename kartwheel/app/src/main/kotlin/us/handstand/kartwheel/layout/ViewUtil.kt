package us.handstand.kartwheel.layout


import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

object ViewUtil {

    fun <T : View> findView(viewGroup: ViewGroup, @IdRes id: Int): T {
        return viewGroup.findViewById(id) as T
    }

    fun <T : View> findView(activity: Activity, @IdRes id: Int): T {
        return activity.findViewById(id) as T
    }

    fun isEmpty(editText: EditText?): Boolean {
        return TextUtils.isEmpty(editText?.text?.toString())
    }

    fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun setButtonState(resources: Resources, button: AppCompatButton?, @ColorRes color: Int, @StringRes textRes: Int, enabled: Boolean) {
        if (button == null) {
            return
        }
        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(resources.getColor(color)))
        button.setText(textRes)
        button.isEnabled = enabled
    }
}
