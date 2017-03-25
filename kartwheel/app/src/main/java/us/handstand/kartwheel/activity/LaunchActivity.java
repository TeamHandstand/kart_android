package us.handstand.kartwheel.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import us.handstand.kartwheel.KartWheel;
import us.handstand.kartwheel.R;

public class LaunchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        if (KartWheel.getUser() == null) {
            startActivity(TicketActivity.getStartIntent(this));
            finish();
        } else {
            // TODO: Start actual application
        }
    }
}
