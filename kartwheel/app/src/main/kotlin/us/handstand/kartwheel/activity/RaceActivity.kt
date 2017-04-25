package us.handstand.kartwheel.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.RaceController
import us.handstand.kartwheel.model.Storage

class RaceActivity : AppCompatActivity() {

    private val raceController = RaceController(KartWheel.db, Storage.eventId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race)
    }
}
