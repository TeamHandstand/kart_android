package us.handstand.kartwheel.test.activity

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.Visibility.INVISIBLE
import android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.Until
import org.junit.*
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
import java.util.regex.Pattern


@RunWith(AndroidJUnit4::class)
class OnboardingActivityTest {
    @Rule @JvmField
    val testRule = IntentsTestRule(OnboardingActivity::class.java)
    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation();
        device = UiDevice.getInstance(instrumentation);
    }

    @After
    fun tearDown() {
        KartWheel.logout()
    }

    @Ignore
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

    @Test
    fun takePhoto() {
        checkOnboardingState(STARTED)
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(SELFIE)
        onView(withId(R.id.image)).perform(click())
        takePhotoWithNativeCamera()
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(PICK_BUDDY)
    }

    fun takePhotoWithNativeCamera() {
        device.findObject(By.res("com.android.camera:id/shutter_button").clazz("android.widget.ImageView").text(Pattern.compile("")).pkg("com.android.camera")).clickAndWait(Until.newWindow(), 500)
        device.findObject(By.res("com.android.camera:id/btn_done").clazz("android.widget.ImageView").text(Pattern.compile("")).pkg("com.android.camera")).clickAndWait(Until.newWindow(), 500)
    }

    fun checkOnboardingState(@OnboardingStep step: Long) {
        onView(withId(R.id.title)).check(matches(withText(OnboardingController.getTitleStringResIdForStep(step))))
        onView(withId(R.id.pageNumber)).check(matches(withEffectiveVisibility(if (step == STARTED) INVISIBLE else VISIBLE)))
        onView(withId(R.id.pageNumber)).check(matches(withText("${step} of 5")))
        onView(withId(R.id.description)).check(matches(withText(OnboardingController.getDescriptionStringResIdForStep(step))))
        onView(withId(R.id.button)).check(matches(withText(OnboardingController.getButtonStringResIdForStep(step))))
    }
}