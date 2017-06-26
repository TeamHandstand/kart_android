package us.handstand.kartwheel


import android.preference.PreferenceManager
import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import us.handstand.kartwheel.inject.DaggerInjector
import us.handstand.kartwheel.inject.Injector
import us.handstand.kartwheel.inject.provider.*
import us.handstand.kartwheel.layout.Font
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.ThreadManager

open class KartWheel : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Storage.initialize(PreferenceManager.getDefaultSharedPreferences(this))
        Database.initialize(this)
        API.db = Database.get()
        Fabric.with(this, Crashlytics())
        injector = DaggerInjector.builder()
                .applicationProvider(ApplicationProvider(this))
                .controllerProvider(ControllerProvider())
                .cloudStorageProvider(CloudStorageProvider())
                .apiProvider(ApiProvider(BuildConfig.SERVER))
                .bottomSheetCallbackProvider(BottomSheetCallbackProvider())
                .build()

        injector.inject(API)
        injector.inject(Font)
    }

    companion object {
        lateinit var injector: Injector
        fun logout(submitOnExecutor: Boolean = true) {
            if (submitOnExecutor) {
                ThreadManager.databaseExecutor.submit {
                    Storage.clear()
                    Database.clear(Database.get())
                }
            } else {
                Storage.clear()
                Database.clear(Database.get())
            }
        }
    }
}
