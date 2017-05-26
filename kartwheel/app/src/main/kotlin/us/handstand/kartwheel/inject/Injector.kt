package us.handstand.kartwheel.inject

import dagger.Component
import us.handstand.kartwheel.fragment.race.RaceListFragment
import us.handstand.kartwheel.fragment.ticket.GameInfoFragment
import us.handstand.kartwheel.inject.provider.ApplicationProvider
import us.handstand.kartwheel.inject.provider.ControllerProvider


@Component(modules = arrayOf(ApplicationProvider::class, ControllerProvider::class))
interface Injector {
    fun inject(fragment: GameInfoFragment)
    fun inject(fragment: RaceListFragment)
}