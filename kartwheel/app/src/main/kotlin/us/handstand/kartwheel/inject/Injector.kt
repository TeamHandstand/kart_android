package us.handstand.kartwheel.inject

import dagger.Component
import us.handstand.kartwheel.fragment.race.RaceListFragment
import us.handstand.kartwheel.fragment.ticket.GameInfoFragment
import us.handstand.kartwheel.inject.provider.ApiProvider
import us.handstand.kartwheel.inject.provider.ApplicationProvider
import us.handstand.kartwheel.inject.provider.CloudStorageProvider
import us.handstand.kartwheel.inject.provider.ControllerProvider
import us.handstand.kartwheel.network.API


@Component(modules = arrayOf(ApplicationProvider::class, ControllerProvider::class, CloudStorageProvider::class, ApiProvider::class))
interface Injector {
    fun inject(fragment: GameInfoFragment)
    fun inject(fragment: RaceListFragment)
    fun inject(api: API)
}