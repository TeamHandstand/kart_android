package us.handstand.kartwheel.activity

import android.support.v4.app.Fragment
import android.os.Bundle
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.RaceController
import us.handstand.kartwheel.controller.RaceController.Companion.NONE
import us.handstand.kartwheel.controller.RaceController.Companion.RACE_SIGN_UP
import us.handstand.kartwheel.controller.RaceController.Companion.RACE_MAP
import us.handstand.kartwheel.controller.RaceController.Companion.FINISHED
import us.handstand.kartwheel.controller.RaceListener
import us.handstand.kartwheel.fragment.race.RaceSignUpFragment


class RaceActivity : LocationAwareActivity(), RaceListener {
    companion object {
        private fun getFragmentForStep(step: Long): Fragment? {
            var fragment: Fragment? = null
            when (step) {
                NONE -> { /* NO - OP */ }
                RACE_SIGN_UP -> fragment = (RaceSignUpFragment() as Fragment)
                RACE_MAP -> { fragment = (RaceSignUpFragment() as Fragment) }
                FINISHED -> { /* NO - OP */ }
            }
            return fragment
        }
    }

    private var currentFragment: Fragment? = null

    private val controller = RaceController(this)

    //region - Life Cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_race_sign_up)

        controller.transition(NONE, RACE_SIGN_UP)
    }

    //endregion

    //region - Private

    //endregion

    //region - RaceController.RaceListener

    override fun showNextStep(previous: Long, next: Long) {
        val nextFragment = getFragmentForStep(next)

        if (nextFragment == null) {
            if (currentFragment != null) {
                supportFragmentManager.beginTransaction().remove(currentFragment).commit()
            }
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, nextFragment).commit()
        }

        currentFragment = nextFragment
    }

    //endregion
}