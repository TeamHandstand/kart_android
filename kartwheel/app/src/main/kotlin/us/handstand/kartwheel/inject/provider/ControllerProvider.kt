package us.handstand.kartwheel.inject.provider


import android.content.Context
import dagger.Module
import dagger.Provides
import us.handstand.kartwheel.controller.GameInfoController
import us.handstand.kartwheel.controller.RaceListController
import us.handstand.kartwheel.controller.SelfieUploadController

@Module(includes = arrayOf(ApplicationProvider::class))
open class ControllerProvider {
    @Provides open fun gameInfoController(): GameInfoController = GameInfoController()
    @Provides open fun raceListController(): RaceListController = RaceListController()
    @Provides open fun selfieUploadController(context: Context): SelfieUploadController = SelfieUploadController(context)
}
