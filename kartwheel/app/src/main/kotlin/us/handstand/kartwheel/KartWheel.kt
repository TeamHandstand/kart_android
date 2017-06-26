package us.handstand.kartwheel


import android.preference.PreferenceManager
import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import us.handstand.kartwheel.inject.DaggerInjector
import us.handstand.kartwheel.inject.Injector
import us.handstand.kartwheel.inject.provider.ApiProvider
import us.handstand.kartwheel.inject.provider.BottomSheetCallbackProvider
import us.handstand.kartwheel.inject.provider.CloudStorageProvider
import us.handstand.kartwheel.inject.provider.ControllerProvider
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.network.API

open class KartWheel : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Storage.initialize(PreferenceManager.getDefaultSharedPreferences(this))
        Database.initialize(this)
        API.db = Database.get()
        Fabric.with(this, Crashlytics())
        injector = DaggerInjector.builder()
                .controllerProvider(ControllerProvider())
                .cloudStorageProvider(CloudStorageProvider())
                .apiProvider(ApiProvider(BuildConfig.SERVER))
                .bottomSheetCallbackProvider(BottomSheetCallbackProvider())
                .build()

        injector.inject(API)
    }

    companion object {
        lateinit var injector: Injector
        fun logout() {
            Storage.clear()
            Database.clear(Database.get())
        }
    }
}
