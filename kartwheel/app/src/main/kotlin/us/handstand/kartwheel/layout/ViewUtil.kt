package us.handstand.kartwheel.layout


import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
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

    fun setIfNotEmpty(editText: EditText, value: String?) {
        if (!TextUtils.isEmpty(value)) {
            editText.setText(value)
        }
    }

    // Draw stripes
    private val line = Path()
    private val paint = Paint()
    private var strokeWidth: Float = 0f

    init {
        paint.style = Paint.Style.STROKE
    }

    fun getCandyCaneBackgroundDrawable(context: Context, width: Float, height: Float, backgroundColor: Int, stripeColor: Int = 0): Drawable {
        // Init the stroke width if it isn't already init'd
        if (strokeWidth == 0f) {
            strokeWidth = ViewUtil.dpToPx(context, 120).toFloat()
            paint.strokeWidth = strokeWidth / 2
        }

        paint.color = context.resources.getColor(stripeColor)

        val bmp = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        canvas.drawColor(context.resources.getColor(backgroundColor))
        canvas.translate(-width / 2, height / 10)
        canvas.rotate(-30f)
        var step = 0f
        while (step / 2 < height) {
            canvas.translate(0f, strokeWidth)
            canvas.drawLine(-width, 0f, 2 * width, 0f, paint)
            step += 2 * strokeWidth
        }
        return BitmapDrawable(context.resources, bmp)
    }


}

fun View.runOnPreDraw(runnable: (view: View) -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            runnable.invoke(this@runOnPreDraw)
            return true
        }
    })
}

fun View.runOnDraw(runnable: (view: View) -> Unit) {
    viewTreeObserver.addOnDrawListener(object : ViewTreeObserver.OnDrawListener {
        override fun onDraw() {
            viewTreeObserver.removeOnDrawListener(this)
            runnable.invoke(this@runOnDraw)
        }
    })
}

fun View.runOnGlobalLayout(runnable: (view: View) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            runnable.invoke(this@runOnGlobalLayout)
        }
    })
}

fun View.setCandyCaneBackground(backgroundColor: Int, stripeColor: Int) {
    runOnGlobalLayout {
        background = ViewUtil.getCandyCaneBackgroundDrawable(it.context, it.measuredWidth.toFloat(), it.measuredHeight.toFloat(), backgroundColor, stripeColor)
    }
}
