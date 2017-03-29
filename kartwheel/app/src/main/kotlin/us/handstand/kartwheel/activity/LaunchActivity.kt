package us.handstand.kartwheel.activity


import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        if (KartWheel.user == null) {
            startActivity(TicketActivity.getStartIntent(this))
            finish()
        } else {
            // TODO: Start actual application
        }
    }
}
