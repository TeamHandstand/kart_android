package us.handstand.kartwheel.activity

import android.support.v4.app.Fragment
import android.os.Bundle
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.RaceController
import us.handstand.kartwheel.controller.RaceListener
import us.handstand.kartwheel.location.UserLocation


class RaceActivity : LocationAwareActivity(), RaceListener {
    private var currentFragment: Fragment? = null

    private val controller = RaceController(this)

    //region - Life Cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_race_sign_up)

        controller.start()
    }

    //endregion

    //region - RaceController.RaceListener

    override fun showNextFragment(nextFragment: Fragment) {
        if (currentFragment == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, nextFragment).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, nextFragment).commit()
        }

        currentFragment = nextFragment
    }

    override fun getLocation(): UserLocation = userLocation

    override fun finishFlow() {
        finish()
    }

    //endregion
}