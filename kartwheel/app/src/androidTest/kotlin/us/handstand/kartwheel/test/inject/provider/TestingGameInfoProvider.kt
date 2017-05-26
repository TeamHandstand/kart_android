package us.handstand.kartwheel.test.inject.provider

import android.support.test.espresso.Espresso
import android.support.test.espresso.idling.CountingIdlingResource
import us.handstand.kartwheel.inject.provider.GameInfoProvider
import us.handstand.kartwheel.test.controller.IdlingGameInfoController

@dagger.Module
class TestingGameInfoProvider : GameInfoProvider() {
    companion object {
        val idlingResource = CountingIdlingResource(IdlingGameInfoController::class.java.name)
        fun registerIdlingResources() {
            Espresso.registerIdlingResources(TestingGameInfoProvider.Companion.idlingResource)
        }

        fun unregisterIdlingResources() {
            Espresso.unregisterIdlingResources(TestingGameInfoProvider.Companion.idlingResource)
            while (!TestingGameInfoProvider.Companion.idlingResource.isIdleNow) {
                TestingGameInfoProvider.Companion.idlingResource.decrement()
            }
        }
    }

    @dagger.Provides override fun controller() = IdlingGameInfoController(idlingResource)
}


