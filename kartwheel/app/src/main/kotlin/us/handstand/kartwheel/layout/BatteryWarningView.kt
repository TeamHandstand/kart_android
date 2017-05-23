package us.handstand.kartwheel.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import us.handstand.kartwheel.R


class BatteryWarningView : LinearLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var batteryPercentage: TextView
    var batteryDescription: TextView

    init {
        View.inflate(context, R.layout.view_battery_warning, this)
        batteryPercentage = ViewUtil.findView(this, R.id.batteryPercentage)
        batteryDescription = ViewUtil.findView(this, R.id.batteryDescription)
        setBackgroundResource(R.drawable.background_white_transparent)
        if (isInEditMode) {
            setBatteryPercentage(50)
        }
    }

    fun setBatteryPercentage(level: Int) {
        if (level < 20) {
            batteryDescription.text = String(Character.toChars(0x1F6A8)) + "Charge up! You need 20% to run a race!"
        } else if (level < 50) {
            batteryDescription.text = String(Character.toChars(0x26A0)) + "You should have 20% to run a race!"
        } else {
            batteryDescription.text = "Meet at the " + String(Character.toChars(0x1F3C1)) + " at race time!"
        }
        batteryPercentage.text = level.toString() + "%"
    }

}