package us.handstand.kartwheel.layout


import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView


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

    fun hideKeyboard(context: Activity) {
        if (context.currentFocus != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(context.currentFocus.windowToken, 0)
        }
    }

    fun setButtonState(resources: Resources, button: AppCompatButton?, @ColorRes color: Int, @StringRes textRes: Int, enabled: Boolean) {
        if (button == null) {
            return
        }
        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(resources.getColor(color)))
        button.setText(textRes)
        button.isEnabled = enabled
    }

    fun setTypeface(assets: AssetManager, textView: TextView) {
        val font = Typeface.createFromAsset(assets, "Chantelli_Antiqua.ttf")
        textView.typeface = font
    }


}
