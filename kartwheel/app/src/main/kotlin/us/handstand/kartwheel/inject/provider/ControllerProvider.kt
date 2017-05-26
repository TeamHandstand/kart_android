package us.handstand.kartwheel.inject.provider


import dagger.Module
import dagger.Provides
import us.handstand.kartwheel.controller.GameInfoController
import us.handstand.kartwheel.controller.RaceListController

@Module
open class ControllerProvider {
    @Provides open fun gameInfoController(): GameInfoController = GameInfoController()
    @Provides open fun raceListController(): RaceListController = RaceListController()
}
