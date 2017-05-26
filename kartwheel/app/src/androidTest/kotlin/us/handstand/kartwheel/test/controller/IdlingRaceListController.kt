package us.handstand.kartwheel.test.controller

import android.support.test.espresso.idling.CountingIdlingResource
import us.handstand.kartwheel.controller.RaceListController
import us.handstand.kartwheel.model.Race

class IdlingRaceListController(val idlingResource: CountingIdlingResource) : RaceListController() {
    override fun subscribe(raceListListener: RaceListListener) {
        // We're expecting 3 back
        idlingResource.increment()
        idlingResource.increment()
        idlingResource.increment()
        super.subscribe(raceListListener)
    }

    override fun onRacesUpdated(races: List<Race>) {
        super.onRacesUpdated(races)
        for (race in races) {
            idlingResource.decrement()
        }
    }
}
