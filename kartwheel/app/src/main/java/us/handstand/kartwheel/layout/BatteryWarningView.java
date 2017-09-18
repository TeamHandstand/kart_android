package us.handstand.kartwheel.layout;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import us.handstand.kartwheel.R;

public class BatteryWarningView extends LinearLayout {
    public BatteryWarningView(Context context) {
        super(context);
        init();
    }

    public BatteryWarningView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BatteryWarningView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private TextView batteryPercentage;
    private TextView batteryDescription;
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            setBatteryPercentage(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0));
        }
    };

    private void init() {
        View.inflate(getContext(), R.layout.view_battery_warning, this);
        batteryPercentage = findViewById(R.id.batteryPercentage);
        batteryDescription = findViewById(R.id.batteryDescription);
        if (isInEditMode()) {
            setBatteryPercentage(50);
        }
    }

    public void registerReceiver() {
        getContext().registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void unregisterReceiver() {
        getContext().unregisterReceiver(batteryInfoReceiver);
    }

    private void setBatteryPercentage(int level) {
        if (level < 20) {
            batteryDescription.setText(new String(Character.toChars(0x1F6A8)) + "Charge up! You need 20% to run a race!");
        } else if (level < 50) {
            batteryDescription.setText(new String(Character.toChars(0x26A0)) + "You should have 20% to run a race!");
        } else {
            batteryDescription.setText("Meet at the " + new String(Character.toChars(0x1F3C1)) + " at race time!");
        }
        batteryPercentage.setText(String.valueOf(level) + "%");
    }

}
