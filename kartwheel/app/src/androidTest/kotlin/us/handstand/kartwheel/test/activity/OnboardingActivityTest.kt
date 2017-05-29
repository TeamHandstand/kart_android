package us.handstand.kartwheel.test.activity

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.Visibility.INVISIBLE
import android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.controller.OnboardingController
import us.handstand.kartwheel.controller.OnboardingController.Companion.BUDDY_EXPLANATION
import us.handstand.kartwheel.controller.OnboardingController.Companion.OnboardingStep
import us.handstand.kartwheel.controller.OnboardingController.Companion.PICK_BUDDY
import us.handstand.kartwheel.controller.OnboardingController.Companion.POINT_SYSTEM
import us.handstand.kartwheel.controller.OnboardingController.Companion.SELFIE
import us.handstand.kartwheel.controller.OnboardingController.Companion.STARTED
import us.handstand.kartwheel.controller.OnboardingController.Companion.VIDEO


@RunWith(AndroidJUnit4::class)
class OnboardingActivityTest {
    @Rule @JvmField
    val testRule = IntentsTestRule(OnboardingActivity::class.java)

    @After
    fun tearDown() {
        KartWheel.logout()
    }

    @Test
    fun traverseOnboarding() {
        checkOnboardingState(STARTED)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(SELFIE)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(PICK_BUDDY)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(BUDDY_EXPLANATION)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(POINT_SYSTEM)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(VIDEO)
    }

    fun checkOnboardingState(@OnboardingStep step: Long) {
        onView(withId(R.id.title)).check(matches(withText(OnboardingController.getTitleStringResIdForStep(step))))
        onView(withId(R.id.pageNumber)).check(matches(withEffectiveVisibility(if (step == STARTED) INVISIBLE else VISIBLE)))
        onView(withId(R.id.pageNumber)).check(matches(withText("${step} of 5")))
        onView(withId(R.id.description)).check(matches(withText(OnboardingController.getDescriptionStringResIdForStep(step))))
        onView(withId(R.id.button)).check(matches(withText(OnboardingController.getButtonStringResIdForStep(step))))
    }
}