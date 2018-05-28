package us.handstand.kartwheel.test.fragment

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.LoggedInActivity
import us.handstand.kartwheel.mocks.MockAPI
import us.handstand.kartwheel.mocks.matches
import us.handstand.kartwheel.mocks.toJson
import us.handstand.kartwheel.model.Race
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.test.AndroidTestKartWheel
import us.handstand.kartwheel.test.inject.provider.ControllerProviderWithIdlingResources
import us.handstand.kartwheel.test.util.withBackground


@RunWith(AndroidJUnit4::class)
class RaceListFragmentTest {
    @Rule @JvmField
    val testRule = IntentsTestRule(LoggedInActivity::class.java, true, false)
    lateinit var mockApi: MockAPI
    val races = mutableListOf<Race>()

    @Before
    fun setUp() {
        ControllerProviderWithIdlingResources.registerIdlingResources()
        Storage.userId = MockAPI.userId1
        Storage.eventId = MockAPI.eventId
        mockApi = AndroidTestKartWheel.api
        // Setup mock server
        mockApi.server.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                if (request.path.matches(MockAPI.coursesPattern)) {
                    return MockResponse().setBody(MockAPI.courses.toJson("courses"))
                } else if (request.path.matches(MockAPI.racesPattern)) {
                    return MockResponse().setBody(races.toJson("races"))
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
    }

    @After
    fun tearDown() {
        KartWheel.logout(false)
        ControllerProviderWithIdlingResources.unregisterIdlingResources()
    }

    @Test
    fun checkColorsOnRaces() {
        races.clear()
        races.add(MockAPI.getRace(1L, "race-1", MockAPI.hellmanCourse))
        races.add(MockAPI.getRace(2L, "race-2", MockAPI.hellmanCourse))
        races.add(MockAPI.getRace(3L, "race-3", MockAPI.hellmanCourse))

        testRule.launchActivity(Intent(Intent.ACTION_MAIN))

        // Check that there are three races queued
        checkItem("#1 - Race race-1", "FINISHED", R.color.red)
        checkItem("#2 - Race race-2", "REGISTRATION CLOSED", R.color.red)
        checkItem("#3 - Race race-3", "3 SPOTS LEFT", R.color.green)

    }

    private fun checkItem(raceName: String, status: String, bgColor: Int) {
        onView(allOf(
                withChild(allOf(withId(R.id.raceName), withText(raceName))),
                withChild(allOf(withId(R.id.spotsLeft), withText(status), withBackground(bgColor)))
        )).check(matches(isDisplayed()))
    }
}