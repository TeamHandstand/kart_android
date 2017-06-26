package us.handstand.kartwheel.test.inject.provider

import android.content.Context
import android.support.test.espresso.Espresso
import android.support.test.espresso.idling.CountingIdlingResource
import dagger.Module
import dagger.Provides
import us.handstand.kartwheel.controller.SelfieUploadController
import us.handstand.kartwheel.inject.provider.ControllerProvider
import us.handstand.kartwheel.test.controller.IdlingGameInfoController
import us.handstand.kartwheel.test.controller.IdlingRaceListController
import us.handstand.kartwheel.test.controller.IdlingSelfieUploadController

@Module
class ControllerProviderWithIdlingResources : ControllerProvider() {
    companion object {
        val gameInfoIdlingResource = CountingIdlingResource(IdlingGameInfoController::class.java.name)
        val raceListIdlingResource = CountingIdlingResource(IdlingRaceListController::class.java.name)
        val selfieUploadIdlingResource = IdlingSelfieUploadController.SelfieUploadIdlingResource()

        fun registerIdlingResources() {
            Espresso.registerIdlingResources(gameInfoIdlingResource, raceListIdlingResource, selfieUploadIdlingResource)
        }

        fun unregisterIdlingResources() {
            Espresso.unregisterIdlingResources(gameInfoIdlingResource, raceListIdlingResource, selfieUploadIdlingResource)
            while (!gameInfoIdlingResource.isIdleNow) {
                gameInfoIdlingResource.decrement()
            }
            while (!raceListIdlingResource.isIdleNow) {
                raceListIdlingResource.decrement()
            }
            selfieUploadIdlingResource.unregister()
        }
    }

    @Provides override fun gameInfoController() = IdlingGameInfoController(gameInfoIdlingResource)
    @Provides override fun raceListController() = IdlingRaceListController(raceListIdlingResource)
    @Provides override fun selfieUploadController(context: Context): SelfieUploadController = IdlingSelfieUploadController(selfieUploadIdlingResource, context)
}
