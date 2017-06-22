package us.handstand.kartwheel.test

import android.os.StrictMode
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.inject.DaggerInjector
import us.handstand.kartwheel.inject.provider.ApiProvider
import us.handstand.kartwheel.mocks.MockAPI
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.test.inject.provider.ControllerProviderWithIdlingResources
import us.handstand.kartwheel.test.inject.provider.MockCloudStorageProvider

class AndroidTestKartWheel : KartWheel() {

    override fun onCreate() {
        super.onCreate()
        // Temporarily set the thread policy so that we can start the mock server on app creation
        val threadPolicy = StrictMode.getThreadPolicy()
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build());
        val api = MockAPI(Database.get())
        injector = DaggerInjector.builder()
                .controllerProvider(ControllerProviderWithIdlingResources())
                .cloudStorageProvider(MockCloudStorageProvider())
                .apiProvider(ApiProvider(api.server.url("/").uri().toString()))
                .build()
        injector.inject(API)
        StrictMode.setThreadPolicy(threadPolicy)
    }
}