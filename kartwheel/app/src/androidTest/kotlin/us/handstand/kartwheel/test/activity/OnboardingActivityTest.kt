package us.handstand.kartwheel.test.activity

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity


@RunWith(AndroidJUnit4::class)
class OnboardingActivityTest {
    @Rule @JvmField
    val testRule = IntentsTestRule(OnboardingActivity::class.java)

    @Test
    fun moveToSelfiePage() {
        onView(withId(R.id.title)).check(matches(withText(R.string.onboarding_started_title)))
        onView(withId(R.id.pageNumber)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.description)).check(matches(withText(R.string.onboarding_started_description)))
        onView(withId(R.id.button)).check(matches(withText(R.string.onboarding_started_button)))

        onView(withId(R.id.button)).perform(click())

        onView(withId(R.id.title)).check(matches(withText(R.string.onboarding_selfie_title)))
        onView(withId(R.id.pageNumber)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.pageNumber)).check(matches(withText("1 of 5")))
        onView(withId(R.id.description)).check(matches(withText(R.string.onboarding_selfie_description)))
        onView(withId(R.id.button)).check(matches(withText(R.string.onboarding_selfie_button)))
    }
}