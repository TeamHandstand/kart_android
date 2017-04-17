package us.handstand.kartwheel

import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Test
import us.handstand.kartwheel.controller.TicketController
import us.handstand.kartwheel.controller.TicketController.Companion.ALREADY_CLAIMED
import us.handstand.kartwheel.controller.TicketController.Companion.CODE_ENTRY
import us.handstand.kartwheel.controller.TicketController.Companion.CRITICAL_INFO
import us.handstand.kartwheel.controller.TicketController.Companion.FORFEIT
import us.handstand.kartwheel.controller.TicketController.Companion.GAME_INFO
import us.handstand.kartwheel.controller.TicketController.Companion.NONE
import us.handstand.kartwheel.controller.TicketController.Companion.TOS
import us.handstand.kartwheel.controller.TicketController.Companion.WELCOME

class TicketControllerTest : TicketController.Companion.TicketStepCompletionListener {
    val ticketController = TicketController(this)

    var nextStep: Int = NONE

    val items = listOf(TOS, CODE_ENTRY, CRITICAL_INFO, WELCOME, ALREADY_CLAIMED, FORFEIT, GAME_INFO)

    @Test
    fun tosTransitions() {
        testTransitions(TOS, { it == CODE_ENTRY })
    }

    @Test
    fun codeEntryTransitions() {
        testTransitions(CODE_ENTRY, { it == CRITICAL_INFO || it == GAME_INFO || it == ALREADY_CLAIMED })
    }

    @Test
    fun criticalInfoTransitions() {
        testTransitions(CRITICAL_INFO, { it == WELCOME })
    }

    @Test
    fun welcomeTransitions() {
        testTransitions(WELCOME, { it == GAME_INFO })
    }

    @Test
    fun gameInfoTransitions() {
        testTransitions(GAME_INFO, { it == FORFEIT })
    }

    @Test
    fun forfeitTransitions() {
        testTransitions(FORFEIT, { it == CODE_ENTRY || it == GAME_INFO })
    }

    private fun testTransitions(from: Int, checkItemForAllowableTransition: (Int) -> Boolean) {
        for (item in items) {
            if (checkItemForAllowableTransition(item)) {
                ticketController.transition(from, item)
                assertThat(nextStep, CoreMatchers.`is`(item))
                continue
            } else {
                try {
                    ticketController.transition(from, item)
                } catch(e: Exception) {
                    assertThat(e.message, CoreMatchers.`is`("Invalid transition from $from to $item"))
                    continue
                }
                fail("Did not throw exception for $item")
            }
        }
    }

    override fun showNextStep(previous: Int, next: Int) {
        nextStep = next
    }

    override fun showDialog(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTicketFragmentStateChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
