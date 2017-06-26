package us.handstand.kartwheel.test.activity

import android.support.design.widget.BottomSheetBehavior
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.IdlingResource
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
import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
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
import us.handstand.kartwheel.layout.recyclerview.viewholder.RegistrantAvatarVH
import us.handstand.kartwheel.mocks.MockAPI
import us.handstand.kartwheel.mocks.MockStorageProvider
import us.handstand.kartwheel.mocks.matches
import us.handstand.kartwheel.mocks.toJson
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.test.AndroidTestKartWheel
import us.handstand.kartwheel.test.inject.provider.ControllerProviderWithIdlingResources
import java.util.regex.Pattern


@RunWith(AndroidJUnit4::class)
class OnboardingActivityTest {
    @Rule @JvmField
    val testRule = IntentsTestRule(OnboardingActivity::class.java)
    private lateinit var device: UiDevice
    private lateinit var mockApi: MockAPI

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mockApi = AndroidTestKartWheel.api
        mockApi.setupApp()
        mockApi.server.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                if (request.path.matches(MockAPI.userPattern)) {
                    return MockResponse().setBody(MockAPI.getUser(1, true, true).toJson())
                } else {
                    Log.e("Unknown path", request.path)
                }
                return MockResponse().setHttp2ErrorCode(404)
            }
        })
        ControllerProviderWithIdlingResources.registerIdlingResources()
        registerIdlingResources(
                testRule.activity.pickBuddyBehaviorCallback as IdlingResource,
                testRule.activity.videoBehaviorCallback as IdlingResource
        )
    }

    @After
    fun tearDown() {
        KartWheel.logout(false)
        MockStorageProvider.transferObserver.bytesTransferred = 0L
        ControllerProviderWithIdlingResources.unregisterIdlingResources()
        unregisterIdlingResources(
                testRule.activity.pickBuddyBehaviorCallback as IdlingResource,
                testRule.activity.videoBehaviorCallback as IdlingResource
        )
    }

    @Test
    fun takePhoto() {
        checkOnboardingState(STARTED)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(SELFIE)

        // The advance button shouldn't be visible until we've taken a photo
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(INVISIBLE)))
        assert(!MockStorageProvider.uploading)

        // Take the photo
        onView(withId(R.id.image)).perform(click())
        takePhotoWithNativeCamera()

        // Make sure it isn't uploading
        assert(!MockStorageProvider.uploading)
        assertThat(MockStorageProvider.transferObserver.bytesTransferred, `is`(0L))

        // Press button for uploadPhoto. It should now be visible
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(VISIBLE)))
        onView(withId(R.id.button)).perform(click())

        // Still on the SELFIE fragment, but clicking the button/selfie pic does nothing, since we're not done uploading
        assert(MockStorageProvider.uploading)
        onView(withId(R.id.button)).check(matches(not(isEnabled())))
        onView(withId(R.id.image)).check(matches(not(isEnabled())))
        checkOnboardingState(SELFIE)

        // We've now completed the transfer. The UI should change to PICK_BUDDY
        MockStorageProvider.transferObserver.bytesTransferred = 100L
        assert(!MockStorageProvider.uploading)

        checkOnboardingState(PICK_BUDDY)
        onView(withId(R.id.image)).check(matches(isEnabled()))
        // The advance button should be disabled though, since we don't have a buddy
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(INVISIBLE)))
    }

    @Test
    fun skipImageUpload_ifUserAlreadyHasImageUrl() {
        Storage.userImageUrl = "https://www.skipimageuploading.com"

        checkOnboardingState(STARTED)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(SELFIE)

        // Try advancing without taking a photo; should succeed
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(VISIBLE)))
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(PICK_BUDDY)
    }

    @Test
    fun enableButtons_ifUserImageUploadFailed() {
        checkOnboardingState(STARTED)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(SELFIE)

        // Take the photo
        onView(withId(R.id.image)).perform(click())
        takePhotoWithNativeCamera()

        // Press button for uploadPhoto
        onView(withId(R.id.button)).perform(click())

        // Make sure it's uploading and then make it fail
        assert(MockStorageProvider.uploading)
        MockStorageProvider.failUpload()
        // When the image fails the uploadPhoto, check that both buttons are enabled
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(VISIBLE)))
        onView(withId(R.id.button)).check(matches(isEnabled()))
        onView(withId(R.id.image)).check(matches(isEnabled()))
        checkOnboardingState(SELFIE)
    }

    @Test
    fun pickBuddy() {
        // User already has image; we can skip this work
        Storage.userImageUrl = "https://www.skipimageuploading.com"

        checkOnboardingState(STARTED)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(SELFIE)

        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(PICK_BUDDY)

        // Cannot advance without a buddy, button isn't yet visible
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(INVISIBLE)))

        // Open the buddy picker
        onView(withId(R.id.image)).perform(click())
        assertThat(testRule.activity.pickBuddyBehavior.state, `is`(BottomSheetBehavior.STATE_EXPANDED))

        // Hiding the buddy picker with the back button without selecting a buddy
        Espresso.pressBack()

        // Buddy list should disappear and the advance button should not appear
        assertThat(testRule.activity.pickBuddyBehavior.state, `is`(BottomSheetBehavior.STATE_HIDDEN))
        assertThat(Storage.selectedBuddyUrl, isEmptyOrNullString())
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(INVISIBLE)))

        // Select buddy
        onView(withId(R.id.image)).perform(click())
        onView(withId(R.id.bottomSheet)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.bottomSheet)).perform(RecyclerViewActions.actionOnItemAtPosition<RegistrantAvatarVH>(0, click()))

        // Buddy list should disappear and the advance button should appear and be enabled
        assertThat(testRule.activity.pickBuddyBehavior.state, `is`(BottomSheetBehavior.STATE_HIDDEN))
        assertThat(Storage.selectedBuddyUrl, not(isEmptyOrNullString()))
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(VISIBLE)))

        // Now uploadPhoto the buddy and move onto the next step
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(BUDDY_EXPLANATION)
    }

    @Test
    fun showVideo_whenClicked() {
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

        // Show how points are earned
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(POINT_SYSTEM)

        // Should advance to the video since there's no action for the user to perform on the explanation
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(VIDEO)

        // The advance button should be invisible until the user opens the video
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(INVISIBLE)))

        // Play the video
        onView(withId(R.id.image)).perform(click())
        assertThat(testRule.activity.videoBehavior.state, `is`(BottomSheetBehavior.STATE_EXPANDED))

        // Hide the video. Should now be able to advance
        Espresso.pressBack()

        assertThat(testRule.activity.videoBehavior.state, `is`(BottomSheetBehavior.STATE_HIDDEN))
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(VISIBLE)))
    }

    @Test
    fun advanceToRaceList_whenOnboardedOnRaceDay() {
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

        // Show how points are earned
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(POINT_SYSTEM)

        // Should advance to the video since there's no action for the user to perform on the explanation
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(VIDEO)

        // Play the video
        onView(withId(R.id.image)).perform(click())
        assertThat(testRule.activity.videoBehavior.state, `is`(BottomSheetBehavior.STATE_EXPANDED))
        // Hide the video. Should now be able to advance
        Espresso.pressBack()
        assertThat(testRule.activity.videoBehavior.state, `is`(BottomSheetBehavior.STATE_HIDDEN))
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(VISIBLE)))

        // Setup mock server to return the courses and race list
        mockApi.server.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                if (request.path.matches(MockAPI.coursesPattern)) {
                    return MockResponse().setBody(MockAPI.courses.toJson("courses"))
                } else if (request.path.matches(MockAPI.racesPattern)) {
                    return MockResponse().setBody(MockAPI.races.toJson("races"))
                } else if (request.path.matches(MockAPI.raceParticipantsPattern)) {
                    return MockResponse().setBody(MockAPI.getRaceParticipants().toJson("users"))
                } else if (request.path.matches(MockAPI.userPattern)) {
                    return MockResponse().setBody(MockAPI.getUser(1, true, true).toJson())
                } else if (request.path.matches(MockAPI.teamPattern)) {
                    return MockResponse().setBody(MockAPI.getTeam(signUpUser1 = true, onboardedUser1 = true).toJson())
                } else if (request.path.matches(MockAPI.eventPattern)) {
                    return MockResponse().setBody(MockAPI.getEvent(true).toJson())
                } else {
                    Log.e("Unknown path", request.path)
                }
                return MockResponse().setHttp2ErrorCode(404)
            }
        })
        onView(withId(R.id.button)).perform(click())

        // Check that there are three races queued
        onView(allOf(withId(R.id.raceName), withText("#1 - Race race-1"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.raceName), withText("#2 - Race race-2"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.raceName), withText("#3 - Race race-3"))).check(matches(isDisplayed()))
    }

    @Test
    fun showGameInfo_afterOnboarding_whenNotGameDay() {
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

        // Show how points are earned
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(POINT_SYSTEM)

        // Should advance to the video since there's no action for the user to perform on the explanation
        onView(withId(R.id.button)).perform(click())
        checkOnboardingState(VIDEO)

        // Play the video
        onView(withId(R.id.image)).perform(click())
        assertThat(testRule.activity.videoBehavior.state, `is`(BottomSheetBehavior.STATE_EXPANDED))
        // Hide the video. Should now be able to advance
        Espresso.pressBack()
        assertThat(testRule.activity.videoBehavior.state, `is`(BottomSheetBehavior.STATE_HIDDEN))
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(VISIBLE)))

        // Should see the GameInfoFragment
        onView(withId(R.id.button)).perform(click())
        onView(allOf(withParent(withId(R.id.playerOne)), withId(R.id.player_name))).check(matches(withText(MockAPI.firstName1 + " " + MockAPI.lastName1)))
        onView(allOf(withParent(withId(R.id.playerTwo)), withId(R.id.player_name))).check(matches(withText(startsWith("UNCLAIMED"))))
    }

    fun takePhotoWithNativeCamera() {
        device.findObject(By.res("com.android.camera:id/shutter_button").clazz("android.widget.ImageView").text(Pattern.compile("")).pkg("com.android.camera")).clickAndWait(Until.newWindow(), 500)
        device.findObject(By.res("com.android.camera:id/btn_done").clazz("android.widget.ImageView").text(Pattern.compile("")).pkg("com.android.camera")).clickAndWait(Until.newWindow(), 500)
    }

    fun checkOnboardingState(@OnboardingStep step: Long) {
        onView(withId(R.id.title)).check(matches(withText(OnboardingController.getTitleStringResIdForStep(step))))
        onView(withId(R.id.pageNumber)).check(matches(withEffectiveVisibility(if (step == STARTED) INVISIBLE else VISIBLE)))
        onView(withId(R.id.pageNumber)).check(matches(withText("$step of 5")))
        onView(withId(R.id.description)).check(matches(withText(OnboardingController.getDescriptionStringResIdForStep(step))))
        onView(withId(R.id.button)).check(matches(withText(OnboardingController.getButtonStringResIdForStep(step))))
        onView(withId(R.id.makeItRainDescription)).check(matches(withEffectiveVisibility(if (step == POINT_SYSTEM) VISIBLE else INVISIBLE)))
    }
}