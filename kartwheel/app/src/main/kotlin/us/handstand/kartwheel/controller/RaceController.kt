package us.handstand.kartwheel.controller

import android.support.annotation.IntDef


interface RaceListener {
    fun showNextStep(@RaceController.Companion.RaceStep previous: Long, @RaceController.Companion.RaceStep next: Long)
}

class RaceController(var listener: RaceListener) {
    companion object {
        const val NONE = -1L
        const val RACE_SIGN_UP = 0L
        const val RACE_MAP = 1L
        const val FINISHED = 2L

        @IntDef(RACE_SIGN_UP, RACE_MAP)
        annotation class RaceStep
    }

    fun transition(@RaceStep from: Long, @OnboardingController.Companion.OnboardingStep to: Long) {
        validateTransition(from, to)

        listener.showNextStep(from, to)
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

}
