package us.handstand.kartwheel


import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import us.handstand.kartwheel.controller.TicketController
import us.handstand.kartwheel.inject.DaggerInjector
import us.handstand.kartwheel.inject.Injector
import us.handstand.kartwheel.inject.provider.*
import us.handstand.kartwheel.layout.Font
import us.handstand.kartwheel.layout.GlideAppModule
import us.handstand.kartwheel.model.CompiledStatements
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.notifications.PubNubManager
import us.handstand.kartwheel.util.Audio
import us.handstand.kartwheel.util.ThreadManager

open class KartWheel : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (BuildConfig.DEBUG) {
            MultiDex.install(this)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Storage.initialize(PreferenceManager.getDefaultSharedPreferences(this))
        Database.initialize(this)
        CompiledStatements.initialize(Database.get())
        API.db = Database.get()
        Fabric.with(this, Crashlytics())
        Audio.initialize(this)
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
        fun logout(submitOnExecutor: Boolean = true, lastTicketState: Long = TicketController.TOS) {
            if (submitOnExecutor) {
                ThreadManager.databaseExecutor.submit {
                    Storage.clear()
                    Database.clear(Database.get())
                    PubNubManager.tearDown()
                    Storage.lastTicketState = lastTicketState
                }
            } else {
                Storage.clear()
                Database.clear(Database.get())
                PubNubManager.tearDown()
                Storage.lastTicketState = lastTicketState
            }
        }
    }
}
