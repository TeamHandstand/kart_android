package us.handstand.kartwheel.test.inject.provider

import android.support.test.espresso.Espresso
import android.support.test.espresso.idling.CountingIdlingResource
import us.handstand.kartwheel.inject.provider.ControllerProvider
import us.handstand.kartwheel.test.controller.IdlingGameInfoController
import us.handstand.kartwheel.test.controller.IdlingRaceListController

@dagger.Module
class ControllerProviderWithIdlingResources : ControllerProvider() {
    companion object {
        val gameInfoIdlingResource = CountingIdlingResource(IdlingGameInfoController::class.java.name)
        val raceListIdlingResource = CountingIdlingResource(IdlingRaceListController::class.java.name)
        fun registerIdlingResources() {
            Espresso.registerIdlingResources(gameInfoIdlingResource, raceListIdlingResource)
        }

        fun unregisterIdlingResources() {
            Espresso.unregisterIdlingResources(gameInfoIdlingResource, raceListIdlingResource)
            while (!gameInfoIdlingResource.isIdleNow) {
                gameInfoIdlingResource.decrement()
            }
            while (!raceListIdlingResource.isIdleNow) {
                raceListIdlingResource.decrement()
            }
        }
    }

    @dagger.Provides override fun gameInfoController() = IdlingGameInfoController(gameInfoIdlingResource)
    @dagger.Provides override fun raceListController() = IdlingRaceListController(raceListIdlingResource)
}


