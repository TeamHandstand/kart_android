package us.handstand.kartwheel.test

import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.inject.DaggerInjector
import us.handstand.kartwheel.inject.provider.ApiProvider
import us.handstand.kartwheel.mocks.MockAPI
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.test.inject.provider.ControllerProviderWithIdlingResources
import us.handstand.kartwheel.test.inject.provider.MockCloudStorageProvider

class AndroidTestKartWheel : KartWheel() {
    val api = MockAPI(Database.get())

    override fun onCreate() {
        super.onCreate()
        injector = DaggerInjector.builder()
                .controllerProvider(ControllerProviderWithIdlingResources())
                .cloudStorageProvider(MockCloudStorageProvider())
                .apiProvider(ApiProvider(api.server.url("/").uri().toString()))
                .build()
        injector.inject(API)
    }
}