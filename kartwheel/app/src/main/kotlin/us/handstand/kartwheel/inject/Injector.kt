package us.handstand.kartwheel.inject

import dagger.Component
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.fragment.race.RaceListFragment
import us.handstand.kartwheel.fragment.ticket.GameInfoFragment
import us.handstand.kartwheel.inject.provider.*
import us.handstand.kartwheel.network.API


@Component(modules = arrayOf(ApplicationProvider::class, ControllerProvider::class, CloudStorageProvider::class, ApiProvider::class, BottomSheetCallbackProvider::class))
interface Injector {
    fun inject(activity: OnboardingActivity)
    fun inject(api: API)
    fun inject(fragment: GameInfoFragment)
    fun inject(fragment: RaceListFragment)
}