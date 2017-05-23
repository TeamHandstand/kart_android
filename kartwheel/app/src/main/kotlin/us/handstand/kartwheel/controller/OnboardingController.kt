package us.handstand.kartwheel.controller

import android.support.annotation.IntDef
import android.text.TextUtils
import us.handstand.kartwheel.model.User


class OnboardingController(var listener: OnboardingStepCompletionListener) {
    companion object {
        const val ERROR = -2
        const val NONE = -1
        const val STARTED = 0
        const val SELFIE = 1
        const val PICK_BUDDY = 2
        const val BUDDY_EXPLANATION = 3
        const val POINT_SYSTEM = 4
        const val VIDEO = 5
        const val FINISHED = 6

        @IntDef(STARTED.toLong(), SELFIE.toLong(), PICK_BUDDY.toLong(), BUDDY_EXPLANATION.toLong(),
                POINT_SYSTEM.toLong(), VIDEO.toLong(), FINISHED.toLong(), ERROR.toLong(), NONE.toLong())
        annotation class FragmentType

        interface OnboardingStepCompletionListener {
            fun showNextStep(@FragmentType previous: Int, @FragmentType next: Int)
            fun showDialog(message: String)
            fun onOnboardingFragmentStateChanged()
        }
    }

    var code: String? = null
    var user: User? = null

    fun transition(@FragmentType from: Int, @FragmentType to: Int) {
        if (from != NONE && to != ERROR) {
            validateTransition(from, to)
        }
        listener.showNextStep(from, to)
    }

    fun onStepCompleted(@FragmentType type: Int) {
        when (type) {
            STARTED -> transition(type, SELFIE)

            SELFIE -> transition(type, PICK_BUDDY)

            PICK_BUDDY -> if (!TextUtils.isEmpty(user?.buddyUrl())) transition(type, BUDDY_EXPLANATION)

            BUDDY_EXPLANATION -> transition(type, POINT_SYSTEM)

            POINT_SYSTEM -> transition(type, VIDEO)

            VIDEO -> transition(type, FINISHED)
        }
    }

    private fun validateTransition(@FragmentType from: Int, @FragmentType to: Int) {
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
