package us.handstand.kartwheel.activity


import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.TOSScrollView
import us.handstand.kartwheel.mocks.MockAPI
import us.handstand.kartwheel.mocks.toJson

@RunWith(AndroidJUnit4::class)
class TicketActivityTest {
    @Rule @JvmField
    val testRule = ActivityTestRule(TicketActivity::class.java)

    @After
    fun tearDown() {
        KartWheel.logout()
    }

    @Test
    fun show_codeEntry_whenFinishedReadingTOS() {
        (testRule.activity.findViewById(R.id.tos_scroll_view) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())
        onView(withId(R.id.code_edit_text)).check(matches(isDisplayed()))
    }

    // Best case scenario
    @Test
    fun show_criticalInfo_whenEnteredCode() {
        // Setup mock server
        val mockApi = MockAPI()
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.team.toJson()))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getEvent(false).toJson()))

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tos_scroll_view) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.code_edit_text)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        // Click on waffles
        onView(withId(R.id.left_image)).perform(click())
        onView(withId(R.id.button)).perform(click())

        // Click on squirtle
        onView(withId(R.id.right_image)).perform(click())
        onView(withId(R.id.button)).perform(click())

        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getUser(1, false).toJson()))
        // Enter information
        onView(withId(R.id.first_name)).perform(replaceText("Matthew"))
        onView(withId(R.id.last_name)).perform(replaceText("Ott"))
        onView(withId(R.id.email)).perform(replaceText("matthew.w.ott@gmail.com"))
        onView(withId(R.id.cell)).perform(replaceText("4083064285"))
        onView(withId(R.id.birth)).perform(replaceText("07251989"))
        onView(withId(R.id.nickname)).perform(replaceText("Matty Otter"))
        onView(withId(R.id.button)).perform(click())

        onView(withId(R.id.title)).check(matches(withText(R.string.onboarding_started_title)))
    }

    // Show Already Claimed
    @Test
    fun show_alreadyClaimed_whenEnteredCode() {
        // Setup mock server to return "already claimed" response
        val mockApi = MockAPI()
        mockApi.server.enqueue(MockResponse().setResponseCode(409).setBody("{}"))

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tos_scroll_view) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.code_edit_text)).perform(replaceText("bob"))
        onView(withId(R.id.button)).perform(click())

        // Already claimed is shown
        onView(withId(R.id.alreadyClaimedLink)).check(matches(isDisplayed()))

        // Return to code entry
        onView(withId(R.id.button)).perform(click())
        onView(withId(R.id.code_edit_text)).check(matches(isDisplayed()))
    }

    @Ignore
    @Test
    fun open_emailApp_whenAlreadyClaimed_contactUsButtonClicked() {
        // TODO: test opening email application
    }

    @Test
    fun showGameInfo_ifOnboarded_andEverythingFilledOut_onPreGameday() {
        // Setup mock server
        val mockApi = MockAPI()
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.team.toJson()))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getEvent(false).toJson()))

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tos_scroll_view) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.code_edit_text)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        // Click on waffles
        onView(withId(R.id.left_image)).perform(click())
        onView(withId(R.id.button)).perform(click())

        // Click on squirtle
        onView(withId(R.id.right_image)).perform(click())
        onView(withId(R.id.button)).perform(click())

        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getUser(1, true).toJson()))
        // Enter information
        onView(withId(R.id.first_name)).perform(replaceText("Matthew"))
        onView(withId(R.id.last_name)).perform(replaceText("Ott"))
        onView(withId(R.id.email)).perform(replaceText("matthew.w.ott@gmail.com"))
        onView(withId(R.id.cell)).perform(replaceText("4083064285"))
        onView(withId(R.id.birth)).perform(replaceText("07251989"))
        onView(withId(R.id.nickname)).perform(replaceText("Matty Otter"))
        onView(withId(R.id.button)).perform(click())

        // TODO: Register idling resources on the GameInfoController
        // TODO: Check the actual names on player 1 and player 2
        onView(withId(R.id.playerOne)).check(matches(isDisplayed()))
        onView(withId(R.id.playerTwo)).check(matches(isDisplayed()))
    }

    @Test
    fun showRaceList_ifOnboarded_andEverythingFilledOut_onGameday() {
        // Setup mock server
        val mockApi = MockAPI()
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.team.toJson()))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getEvent(true).toJson()))

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tos_scroll_view) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.code_edit_text)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        // Click on pancakes
        onView(withId(R.id.right_image)).perform(click())
        onView(withId(R.id.button)).perform(click())

        // Click on charmander
        onView(withId(R.id.left_image)).perform(click())
        onView(withId(R.id.button)).perform(click())

        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getUser(1, true).toJson()))
        // Enter information
        onView(withId(R.id.first_name)).perform(replaceText("Matthew"))
        onView(withId(R.id.last_name)).perform(replaceText("Ott"))
        onView(withId(R.id.email)).perform(replaceText("matthew.w.ott@gmail.com"))
        onView(withId(R.id.cell)).perform(replaceText("4083064285"))
        onView(withId(R.id.birth)).perform(replaceText("07251989"))
        onView(withId(R.id.nickname)).perform(replaceText("Matty Otter"))
        onView(withId(R.id.button)).perform(click())

        // TODO: Register idling resources on the RaceListController
        onView(withId(R.id.playerOne)).check(matches(isDisplayed()))
    }
}

