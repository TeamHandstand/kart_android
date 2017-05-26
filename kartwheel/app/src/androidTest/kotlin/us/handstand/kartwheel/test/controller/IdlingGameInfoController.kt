package us.handstand.kartwheel.test.controller

import android.support.test.espresso.idling.CountingIdlingResource
import us.handstand.kartwheel.controller.GameInfoController
import us.handstand.kartwheel.model.Ticket
import us.handstand.kartwheel.model.User


class IdlingGameInfoController(val idlingResource: CountingIdlingResource) : GameInfoController() {
    init {
        // 2 per user, 1 per ticket per user
        idlingResource.increment()
        idlingResource.increment()
        idlingResource.increment()
        idlingResource.increment()
    }

    override fun onTicketFound(ticket: Ticket) {
        super.onTicketFound(ticket)
        idlingResource.decrement()

    }

    override fun onUsersFound(users: List<User>) {
        super.onUsersFound(users)
        for (user in users) {
            idlingResource.decrement()
        }
    }
}