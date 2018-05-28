package us.handstand.kartwheel.controller

import android.support.annotation.LongDef
import android.text.TextUtils.isEmpty
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.Storage

interface OnboardingStepCompletionListener {
    fun showNextStep(@OnboardingController.Companion.OnboardingStep previous: Long, @OnboardingController.Companion.OnboardingStep next: Long)
    fun showDialog(message: String)
    fun onOnboardingFragmentStateChanged()
}

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

        @LongDef(STARTED, SELFIE, PICK_BUDDY, BUDDY_EXPLANATION, POINT_SYSTEM, VIDEO, FINISHED, ERROR, NONE)
        annotation class OnboardingStep

        fun getTitleStringResIdForStep(@OnboardingStep step: Long): Int {
            when (step) {
                STARTED -> return R.string.onboarding_started_title
                SELFIE -> return R.string.onboarding_selfie_title
                PICK_BUDDY -> return R.string.onboarding_pick_buddy_title
                BUDDY_EXPLANATION -> return R.string.onboarding_buddy_explanation_title
                POINT_SYSTEM -> return R.string.onboarding_points_title
                VIDEO -> return R.string.onboarding_video_title
            }
            return -1
        }

        fun getDescriptionStringResIdForStep(@OnboardingStep step: Long): Int {
            when (step) {
                STARTED -> return R.string.onboarding_started_description
                SELFIE -> return R.string.onboarding_selfie_description
                PICK_BUDDY -> return R.string.onboarding_pick_buddy_description
                BUDDY_EXPLANATION -> return R.string.onboarding_buddy_explanation_description
                POINT_SYSTEM -> return R.string.onboarding_points_description
                VIDEO -> return R.string.onboarding_video_description
            }
            return -1
        }

        fun getButtonStringResIdForStep(@OnboardingStep step: Long): Int {
            when (step) {
                STARTED -> return R.string.onboarding_started_button
                SELFIE -> return R.string.onboarding_selfie_button
                PICK_BUDDY -> return R.string.onboarding_pick_buddy_button
                BUDDY_EXPLANATION -> return R.string.onboarding_buddy_explanation_button
                POINT_SYSTEM -> return R.string.onboarding_points_button
                VIDEO -> return R.string.onboarding_video_button
            }
            return -1
        }

        fun getImageResIdForStep(@OnboardingStep step: Long): Int {
            when (step) {
                SELFIE -> return R.drawable.onboarding_camera
                PICK_BUDDY -> return R.drawable.onboarding_buddy_placeholder
                VIDEO -> return R.drawable.onboarding_play_button
            }
            return -1
        }
    }

    fun transition(@OnboardingStep from: Long, @OnboardingStep to: Long) {
        if (from != NONE && to != ERROR) {
            validateTransition(from, to)
        }
        listener.showNextStep(from, to)
    }

    fun onStepCompleted(@OnboardingStep type: Long) {
        when (type) {
            STARTED -> transition(type, SELFIE)
            SELFIE -> transition(type, PICK_BUDDY)
            PICK_BUDDY -> if (!isEmpty(Storage.userBuddyUrl)) transition(type, BUDDY_EXPLANATION)
            BUDDY_EXPLANATION -> transition(type, POINT_SYSTEM)
            POINT_SYSTEM -> transition(type, VIDEO)
            VIDEO -> transition(type, FINISHED)
        }
    }

    private fun validateTransition(@OnboardingStep from: Long, @OnboardingStep to: Long) {
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
