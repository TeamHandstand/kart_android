package us.handstand.kartwheel.inject.provider


import dagger.Module
import dagger.Provides
import us.handstand.kartwheel.controller.GameInfoController

@Module
open class GameInfoProvider {
    @Provides open fun controller(): GameInfoController = GameInfoController()
}
