package us.handstand.kartwheel.test.activity


import android.app.Instrumentation
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.runner.RunWith
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.layout.TOSScrollView
import us.handstand.kartwheel.mocks.MockAPI
import us.handstand.kartwheel.mocks.matches
import us.handstand.kartwheel.mocks.toJson
import us.handstand.kartwheel.test.AndroidTestKartWheel
import us.handstand.kartwheel.test.inject.provider.ControllerProviderWithIdlingResources

@RunWith(AndroidJUnit4::class)
class TicketActivityTest {
    @Rule @JvmField
    val testRule = IntentsTestRule(TicketActivity::class.java)
    lateinit var mockApi: MockAPI
    var needBackPress = false

    @Before
    fun setUp() {
        ControllerProviderWithIdlingResources.registerIdlingResources()
        mockApi = AndroidTestKartWheel.api
        mockApi.server.setDispatcher(QueueDispatcher())
    }

    @After
    fun tearDown() {
        KartWheel.logout(false)
        ControllerProviderWithIdlingResources.unregisterIdlingResources()
        if (needBackPress) {
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressBack()
        }
    }

    @Test
    fun show_codeEntry_whenFinishedReadingTOS() {
        (testRule.activity.findViewById(R.id.tosScrollView) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())
        onView(withId(R.id.codeEditText)).check(matches(isDisplayed()))
    }

    // Best case scenario
    @Test
    fun fillOutUserInfo_whenEnteredCode() {
        // Setup mock server
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getTeam().toJson()))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getEvent(false).toJson()))

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tosScrollView) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.codeEditText)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        // Click on waffles
        onView(withId(R.id.leftImage)).perform(click())
        onView(withId(R.id.button)).perform(click())

        // Click on squirtle
        onView(withId(R.id.rightImage)).perform(click())
        onView(withId(R.id.button)).perform(click())

        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getUser(1, false, false).toJson()))
        // Enter information
        onView(withId(R.id.firstName)).perform(replaceText("Matthew"))
        onView(withId(R.id.lastName)).perform(replaceText("Ott"))
        onView(withId(R.id.email)).perform(replaceText("matthew.w.ott@gmail.com"))
        onView(withId(R.id.cell)).perform(replaceText("4083064285"))
        onView(withId(R.id.birth)).perform(replaceText("07251989"))
        onView(withId(R.id.nickname)).perform(replaceText("Matty Otter"))
        onView(withId(R.id.button)).perform(click())

        onView(withId(R.id.title)).check(matches(withText(R.string.onboarding_started_title)))
    }

    @Test
    fun doNotAdvance_whenIncorrectSignUpEntered() {
        // Setup mock server
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getTeam().toJson()))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getEvent(false).toJson()))

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tosScrollView) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.codeEditText)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        // Click on waffles
        onView(withId(R.id.leftImage)).perform(click())
        onView(withId(R.id.button)).perform(click())

        // Click on squirtle
        onView(withId(R.id.rightImage)).perform(click())
        onView(withId(R.id.button)).perform(click())

        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getUser(1, false, false).toJson()))
        // For every field we fill out, make sure that we don't advance until we fill out the page correctly
        onView(withId(R.id.firstName)).perform(replaceText("Matthew"))
        onView(withId(R.id.button)).perform(click())

        onView(withId(R.id.lastName)).perform(replaceText("Ott"))
        onView(withId(R.id.button)).perform(click())

        onView(withId(R.id.email)).perform(replaceText("matthew.w.ott@gmail.com"))
        onView(withId(R.id.button)).perform(click())

        onView(withId(R.id.cell)).perform(replaceText("40830642"))
        onView(withId(R.id.button)).perform(click())

        onView(withId(R.id.birth)).perform(replaceText("7725"))
        onView(withId(R.id.button)).perform(click())

        onView(withId(R.id.nickname)).perform(replaceText("Matty Otter"))
        onView(withId(R.id.button)).perform(click())

        // We should have to re-enter the birth date since it was invalid
        onView(withId(R.id.birth)).perform(replaceText("07251989"))
        onView(withId(R.id.button)).perform(click())


        // Also validate phone number
        onView(withId(R.id.cell)).perform(replaceText("4083064285"))
        onView(withId(R.id.button)).perform(click())

        onView(withId(R.id.title)).check(matches(withText(R.string.onboarding_started_title)))
    }

    @Test
    fun open_emailApp_whenAlreadyClaimed_contactUsButtonClicked() {
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(Instrumentation.ActivityResult(0, Intent()))
        // Setup mock server to return "already claimed" response
        mockApi.server.enqueue(MockResponse().setResponseCode(409).setBody("{}"))

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tosScrollView) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.codeEditText)).perform(replaceText(MockAPI.code2))
        onView(withId(R.id.button)).perform(click())

        // Email support
        onView(withId(R.id.additionalButton)).perform(click())
        intended(allOf(hasAction(Intent.ACTION_CHOOSER),
                hasExtra(`is`(Intent.EXTRA_INTENT),
                        allOf(hasAction(Intent.ACTION_SENDTO),
                                hasExtra(Intent.EXTRA_EMAIL, arrayOf(testRule.activity.resources.getString(R.string.support_email))),
                                hasExtra(Intent.EXTRA_SUBJECT, testRule.activity.resources.getString(R.string.contact_us_subject_line)),
                                hasExtra(Intent.EXTRA_TEXT, testRule.activity.resources.getString(R.string.contact_us_body, MockAPI.code2))))))
    }

    @Test
    fun shareNonClaimedUserTicket() {
        // Setup mock server
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getTeam(signUpUser1 = true, onboardedUser1 = true).toJson()))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getEvent(false).toJson()))

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tosScrollView) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.codeEditText)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        // Share user 2
        onView(allOf(withParent(withId(R.id.playerTwo)), withId(R.id.forfeit_or_share))).perform(click())
        intended(allOf(hasAction(Intent.ACTION_CHOOSER),
                hasExtra(`is`(Intent.EXTRA_INTENT),
                        allOf(hasAction(Intent.ACTION_SEND),
                                hasExtra(Intent.EXTRA_TEXT, MockAPI.code2)))))
        needBackPress = true
    }

    @Test
    fun showGameInfo_ifOnboarded_andEverythingFilledOut_onPreGameday() {
        // Setup mock server
        mockApi.server.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                if (request.path.matches(MockAPI.userPattern)) {
                    return MockResponse().setBody(MockAPI.getUser(1, true, false).toJson())
                } else if (request.path.matches(MockAPI.teamPattern)) {
                    return MockResponse().setBody(MockAPI.getTeam(signUpUser1 = true, onboardedUser1 = true).toJson())
                } else if (request.path.matches(MockAPI.eventPattern)) {
                    return MockResponse().setBody(MockAPI.getEvent(false).toJson())
                } else {
                    Log.e("Unknown path", request.path)
                }
                return MockResponse().setHttp2ErrorCode(404)
            }
        })

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tosScrollView) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.codeEditText)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        onView(allOf(withParent(withId(R.id.playerOne)), withId(R.id.player_name))).check(matches(withText(MockAPI.firstName1 + " " + MockAPI.lastName1)))
        onView(allOf(withParent(withId(R.id.playerTwo)), withId(R.id.player_name))).check(matches(withText(startsWith("UNCLAIMED"))))
    }

    @Test
    fun forfeitUser_thenNevermind() {
        // Setup mock server
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getTeam(signUpUser1 = true, onboardedUser1 = true).toJson()))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getEvent(false).toJson()))

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tosScrollView) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.codeEditText)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        onView(allOf(withParent(withId(R.id.playerOne)), withId(R.id.forfeit_or_share))).perform(click())
        onView(withText(R.string.keep_ticket)).perform(click())

        onView(allOf(withParent(withId(R.id.playerOne)), withId(R.id.player_name))).check(matches(withText(MockAPI.firstName1 + " " + MockAPI.lastName1)))
    }

    @Test
    fun actuallyForfeitUser_butDoNotSave() {
        // Setup mock server
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getTeam(signUpUser1 = true, onboardedUser1 = true).toJson()))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getEvent(false).toJson()))
        mockApi.server.enqueue(MockResponse().setBody("{}"))

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tosScrollView) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.codeEditText)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        onView(allOf(withParent(withId(R.id.playerOne)), withId(R.id.forfeit_or_share))).perform(click())
        onView(withText(R.string.forfeit_ticket)).perform(click())

        // Should return to the code entry screen
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getTeam(signUpUser1 = true, onboardedUser1 = true).toJson()))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getEvent(false).toJson()))
        onView(withId(R.id.codeEditText)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        onView(allOf(withParent(withId(R.id.playerOne)), withId(R.id.player_name))).check(matches(withText(MockAPI.firstName1 + " " + MockAPI.lastName1)))
    }

    @Test
    fun showRaceList_ifOnboarded_andEverythingFilledOut_onGameday() {
        // Setup mock server
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

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tosScrollView) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.codeEditText)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        // Check that there are three races queued
        onView(allOf(withId(R.id.raceName), withText("#1 - Race race-1"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.raceName), withText("#2 - Race race-2"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.raceName), withText("#3 - Race race-3"))).check(matches(isDisplayed()))
    }

    @Test
    fun prePopulateFields_thatUserHas() {
        // Setup mock server
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getTeam().toJson()))
        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getEvent(false).toJson()))

        // Scroll TOS
        (testRule.activity.findViewById(R.id.tosScrollView) as TOSScrollView).listener!!.invoke()
        onView(withId(R.id.button)).perform(click())

        // Enter the code
        onView(withId(R.id.codeEditText)).perform(replaceText(MockAPI.code1))
        onView(withId(R.id.button)).perform(click())

        // Click on waffles
        onView(withId(R.id.leftImage)).perform(click())
        onView(withId(R.id.button)).perform(click())

        // Click on squirtle
        onView(withId(R.id.rightImage)).perform(click())
        onView(withId(R.id.button)).perform(click())

        mockApi.server.enqueue(MockResponse().setBody(MockAPI.getUser(1, false, false).toJson()))
        // Enter information
        onView(withId(R.id.firstName)).perform(replaceText("Matthew"))
        onView(withId(R.id.lastName)).perform(replaceText("Ott"))
        onView(withId(R.id.email)).perform(replaceText("matthew.w.ott@gmail.com"))
        onView(withId(R.id.cell)).perform(replaceText("4083064285"))
        onView(withId(R.id.birth)).perform(replaceText("07251989"))
        onView(withId(R.id.nickname)).perform(replaceText("Matty Otter"))
        onView(withId(R.id.button)).perform(click())

        onView(withId(R.id.title)).check(matches(withText(R.string.onboarding_started_title)))
    }
}

