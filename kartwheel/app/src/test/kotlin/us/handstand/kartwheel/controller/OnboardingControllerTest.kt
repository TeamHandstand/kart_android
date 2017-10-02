package us.handstand.kartwheel.controller

import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import us.handstand.kartwheel.controller.OnboardingController.Companion.BUDDY_EXPLANATION
import us.handstand.kartwheel.controller.OnboardingController.Companion.FINISHED
import us.handstand.kartwheel.controller.OnboardingController.Companion.NONE
import us.handstand.kartwheel.controller.OnboardingController.Companion.PICK_BUDDY
import us.handstand.kartwheel.controller.OnboardingController.Companion.POINT_SYSTEM
import us.handstand.kartwheel.controller.OnboardingController.Companion.SELFIE
import us.handstand.kartwheel.controller.OnboardingController.Companion.STARTED
import us.handstand.kartwheel.controller.OnboardingController.Companion.VIDEO
import us.handstand.kartwheel.mocks.MockDBContext
import us.handstand.kartwheel.model.Storage

class OnboardingControllerTest : OnboardingStepCompletionListener {
    private val onboardingController = OnboardingController(this)
    private val items = listOf(STARTED, SELFIE, PICK_BUDDY, BUDDY_EXPLANATION, POINT_SYSTEM, VIDEO, FINISHED)
    private var nextStep: Long = NONE

    @Before
    fun setUp() {
        Storage.initialize(MockDBContext().sharedPrefs)
    }

    @After
    fun tearDown() {
        Storage.clear()
    }

    @Test
    fun startedTransitions() {
        testTransitions(STARTED, { it == SELFIE })
    }

    @Test
    fun selfieTransitions() {
        testTransitions(SELFIE, { it == PICK_BUDDY })
    }

    @Test
    fun pickBuddyTransitions() {
        testTransitions(PICK_BUDDY, { it == BUDDY_EXPLANATION })
    }

    @Test
    fun buddyExplanationTransitions() {
        testTransitions(BUDDY_EXPLANATION, { it == POINT_SYSTEM })
    }

    @Test
    fun pointSystemTransitions() {
        testTransitions(POINT_SYSTEM, { it == VIDEO })
    }

    @Test
    fun videoTransitions() {
        testTransitions(VIDEO, { it == FINISHED })
    }

    private fun testTransitions(from: Long, checkItemForAllowableTransition: (Long) -> Boolean) {
        for (item in items) {
            if (checkItemForAllowableTransition(item)) {
                onboardingController.transition(from, item)
                assertThat(nextStep, CoreMatchers.`is`(item))
                continue
            } else {
                try {
                    onboardingController.transition(from, item)
                } catch(e: Exception) {
                    assertThat(e.message, CoreMatchers.`is`("Invalid transition from $from to $item"))
                    continue
                }
                fail("Did not throw exception for $item")
            }
        }
    }

    override fun showNextStep(previous: Long, next: Long) {
        nextStep = next
    }

    override fun showDialog(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onOnboardingFragmentStateChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
