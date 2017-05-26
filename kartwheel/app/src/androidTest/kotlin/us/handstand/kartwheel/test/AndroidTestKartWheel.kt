package us.handstand.kartwheel.test

import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.inject.DaggerInjector
import us.handstand.kartwheel.test.inject.provider.ControllerProviderWithIdlingResources


class AndroidTestKartWheel : KartWheel() {
    override fun onCreate() {
        super.onCreate()
        injector = DaggerInjector.builder().controllerProvider(ControllerProviderWithIdlingResources()).build()
    }
}