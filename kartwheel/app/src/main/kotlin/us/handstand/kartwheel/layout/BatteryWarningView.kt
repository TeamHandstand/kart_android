package us.handstand.kartwheel.layout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import us.handstand.kartwheel.R


class BatteryWarningView : LinearLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @BindView(R.id.batteryPercentage) lateinit var batteryPercentage: TextView
    @BindView(R.id.batteryDescription) lateinit var batteryDescription: TextView
    private val batteryInfoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setBatteryPercentage(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0))
        }
    }

    init {
        View.inflate(context, R.layout.view_battery_warning, this)
        ButterKnife.bind(this)
        if (isInEditMode) {
            setBatteryPercentage(50)
        }
    }

    fun registerReceiver() = context.registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

    fun unregisterReceiver() = context.unregisterReceiver(batteryInfoReceiver)

    fun setBatteryPercentage(level: Int) {
        when {
            level < 20 -> batteryDescription.text = String(Character.toChars(0x1F6A8)) + "Charge up! You need 20% to run a race!"
            level < 50 -> batteryDescription.text = String(Character.toChars(0x26A0)) + "You should have 20% to run a race!"
            else -> batteryDescription.text = "Meet at the " + String(Character.toChars(0x1F3C1)) + " at race time!"
        }
        batteryPercentage.text = level.toString() + "%"
    }

}