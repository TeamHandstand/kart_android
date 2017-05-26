package us.handstand.kartwheel.inject

import dagger.Component
import us.handstand.kartwheel.fragment.ticket.GameInfoFragment
import us.handstand.kartwheel.inject.provider.ApplicationProvider
import us.handstand.kartwheel.inject.provider.GameInfoProvider


@Component(modules = arrayOf(ApplicationProvider::class, GameInfoProvider::class))
interface Injector {
    fun inject(fragment: GameInfoFragment)
}