package us.handstand.kartwheel.controller

import android.support.annotation.IntDef
import android.support.v4.app.Fragment
import us.handstand.kartwheel.fragment.race.RaceMapFragment
import us.handstand.kartwheel.fragment.race.RaceSignUpFragment
import us.handstand.kartwheel.fragment.race.RaceSignUpListener
import us.handstand.kartwheel.location.UserLocation


interface RaceListener {
    fun showNextFragment(nextFragment: Fragment)
    fun getLocation(): UserLocation
    fun finishFlow()
}

class RaceController(var listener: RaceListener) : RaceSignUpListener {
    private companion object {
        const val NONE = -1L
        const val RACE_SIGN_UP = 0L
        const val RACE_MAP = 1L
        const val FINISHED = 2L

        @IntDef(RACE_SIGN_UP, RACE_MAP)
        annotation class RaceStep
    }

    //region - Public

    fun start() {
        transition(NONE, RACE_SIGN_UP)
    }

    //endregion

    //region - Private

    private fun transition(@RaceStep from: Long, @OnboardingController.Companion.OnboardingStep to: Long) {
        validateTransition(from, to)

        when (to) {
            NONE -> { /* NO - OP */ }
            RACE_SIGN_UP, RACE_MAP -> {
                listener.showNextFragment(getFragmentForStep(to)!!)
            }
            FINISHED -> {
                listener.finishFlow()
            }
        }
    }

    private fun validateTransition(@RaceStep from: Long, @RaceStep to: Long) {
        when (from) {
            NONE -> if (to == RACE_SIGN_UP) return
            RACE_SIGN_UP -> if (to == RACE_MAP) return
            RACE_MAP -> if (to == FINISHED) return
            FINISHED -> { /* NO - OP */ }
        }

        throw IllegalStateException("Invalid transition from $from to $to")
    }

    private fun getFragmentForStep(step: Long): Fragment? {
        var fragment: Fragment? = null
        when (step) {
            NONE -> { /* NO - OP */ }
            RACE_SIGN_UP -> fragment = (RaceSignUpFragment.newInstance(this))
            RACE_MAP -> { fragment = (RaceMapFragment()) }
            FINISHED -> { /* NO - OP */ }
        }
        return fragment
    }

    //endregion

    //region - RaceFragmentInterface

    override fun getLocation(): UserLocation {
        return listener.getLocation()
    }

    //region - RaceSignUpListener

    override fun onJoinRace() {
        transition(RACE_SIGN_UP, RACE_MAP)
    }

    //endregion
}
