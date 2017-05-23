package us.handstand.kartwheel.controller

import android.support.annotation.IntDef
import android.text.TextUtils
import us.handstand.kartwheel.model.User


class OnboardingController(var listener: OnboardingStepCompletionListener) {
    companion object {
        const val ERROR = -2L
        const val NONE = -1L
        const val STARTED = 0L
        const val SELFIE = 1L
        const val PICK_BUDDY = 2L
        const val BUDDY_EXPLANATION = 3L
        const val POINT_SYSTEM = 4L
        const val VIDEO = 5L
        const val FINISHED = 6L

        @IntDef(STARTED, SELFIE, PICK_BUDDY, BUDDY_EXPLANATION, POINT_SYSTEM, VIDEO, FINISHED, ERROR, NONE)
        annotation class FragmentType

        interface OnboardingStepCompletionListener {
            fun showNextStep(@FragmentType previous: Long, @FragmentType next: Long)
            fun showDialog(message: String)
            fun onOnboardingFragmentStateChanged()
        }
    }

    var code: String? = null
    var user: User? = null

    fun transition(@FragmentType from: Long, @FragmentType to: Long) {
        if (from != NONE && to != ERROR) {
            validateTransition(from, to)
        }
        listener.showNextStep(from, to)
    }

    fun onStepCompleted(@FragmentType type: Long) {
        when (type) {
            STARTED -> transition(type, SELFIE)

            SELFIE -> transition(type, PICK_BUDDY)

            PICK_BUDDY -> if (!TextUtils.isEmpty(user?.buddyUrl())) transition(type, BUDDY_EXPLANATION)

            BUDDY_EXPLANATION -> transition(type, POINT_SYSTEM)

            POINT_SYSTEM -> transition(type, VIDEO)

            VIDEO -> transition(type, FINISHED)
        }
    }

    private fun validateTransition(@FragmentType from: Long, @FragmentType to: Long) {
        when (from) {
            STARTED -> if (to == SELFIE) return
            SELFIE -> if (to == PICK_BUDDY) return
            PICK_BUDDY -> if (to == BUDDY_EXPLANATION) return
            BUDDY_EXPLANATION -> if (to == POINT_SYSTEM) return
            POINT_SYSTEM -> if (to == VIDEO) return
            VIDEO -> if (to == FINISHED) return
        }
        throw IllegalStateException("Invalid transition from $from to $to")
    }

    fun onOnboardingFragmentStateChanged() {
        listener.onOnboardingFragmentStateChanged()
    }
}
