package us.handstand.kartwheel.activity

import android.os.Bundle
import us.handstand.kartwheel.R


class RaceSignUpActivity : LocationAwareActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race_sign_up)
    }
}