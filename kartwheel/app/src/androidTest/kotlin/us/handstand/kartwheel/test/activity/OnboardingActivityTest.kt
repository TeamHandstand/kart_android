package us.handstand.kartwheel.test.activity

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.Visibility.INVISIBLE
import android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.Until
import okhttp3.mockwebserver.MockResponse
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
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
import us.handstand.kartwheel.layout.recyclerview.viewholder.RegistrantAvatarVH
import us.handstand.kartwheel.mocks.MockAPI
import us.handstand.kartwheel.mocks.MockStorageProvider
import us.handstand.kartwheel.mocks.toJson
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.test.AndroidTestKartWheel
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
        MockStorageProvider.transferObserver.bytesTransferred = 0L
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

        // Try advancing without taking a photo
        onView(withId(R.id.button)).perform(click())
        assert(!MockStorageProvider.uploading)
        checkOnboardingState(SELFIE)

        // Take the photo
        onView(withId(R.id.image)).perform(click())
        takePhotoWithNativeCamera()

        // Make sure it isn't uploading
        assert(!MockStorageProvider.uploading)
        assertThat(MockStorageProvider.transferObserver.bytesTransferred, `is`(0L))

        // Press button for upload
        onView(withId(R.id.button)).perform(click())

        // Still on the SELFIE fragment, but clicking the button/selfie pic does nothing, since we're not done uploading
        assert(MockStorageProvider.uploading)
        onView(withId(R.id.button)).check(matches(not(isEnabled())))
        onView(withId(R.id.image)).check(matches(not(isEnabled())))
        checkOnboardingState(SELFIE)

        // We've now completed the transfer. The UI should change to PICK_BUDDY
        MockStorageProvider.transferObserver.bytesTransferred = 100L
        onView(withId(R.id.button)).check(matches(isEnabled()))
        onView(withId(R.id.image)).check(matches(isEnabled()))
        assert(!MockStorageProvider.uploading)
        checkOnboardingState(PICK_BUDDY)
    }

    @Test
    fun skipImageUpload_ifUserAlreadyHasImageUrl() {
        checkOnboardingState(STARTED)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(SELFIE)

        // Try advancing without taking a photo; should succeed
        Storage.userImageUrl = "https://www.skipimageuploading.com"
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(PICK_BUDDY)
    }

    @Test
    fun pickBuddy() {
        checkOnboardingState(STARTED)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(SELFIE)
        // User already has image; we can skip this work
        Storage.userImageUrl = "https://www.skipimageuploading.com"

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(PICK_BUDDY)

        // Try advancing without a buddy
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(PICK_BUDDY)

        // Select buddy
        onView(withId(R.id.image)).perform(click())
        onView(withId(R.id.bottomSheet)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.bottomSheet)).perform(RecyclerViewActions.actionOnItemAtPosition<RegistrantAvatarVH>(0, click()))

        // Buddy list should disappear
        onView(withId(R.id.bottomSheet)).check(matches(not(isCompletelyDisplayed())))

        // Now upload the buddy and move onto the next step

        // We need the server to return a User object with the buddyUrl
        AndroidTestKartWheel.api.server.enqueue(MockResponse().setBody(MockAPI.getUser(0, true, true).toJson()))
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(BUDDY_EXPLANATION)
    }

    @Test
    fun skipBuddyUpload_ifUserAlreadyHasBuddyUrl() {
        // User already has image and buddy
        Storage.userImageUrl = "https://www.skipimageuploading.com"
        Storage.userBuddyUrl = "https://www.skipbuddyuploading.com"

        checkOnboardingState(STARTED)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(SELFIE)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(PICK_BUDDY)

        // Should advance to the buddy explanation screen, since we already have one
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(BUDDY_EXPLANATION)
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