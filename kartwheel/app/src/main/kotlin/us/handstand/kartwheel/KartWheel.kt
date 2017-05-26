package us.handstand.kartwheel


import android.app.Application
import android.preference.PreferenceManager
import android.text.TextUtils.isEmpty
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import us.handstand.kartwheel.inject.DaggerInjector
import us.handstand.kartwheel.inject.Injector
import us.handstand.kartwheel.inject.provider.ControllerProvider
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.network.API
import java.io.IOException

open class KartWheel : Application(), Interceptor {
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(this).build()

    override fun onCreate() {
        super.onCreate()
        Storage.initialize(PreferenceManager.getDefaultSharedPreferences(this))
        Database.initialize(this)
        API.initialize(Database.get(), okHttpClient, BuildConfig.SERVER)
        Fabric.with(this, Crashlytics())
        val provider = ControllerProvider()
        injector = DaggerInjector.builder().controllerProvider(provider).build()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.header("Content-Type", "application/json")
        requestBuilder.header("Accept", "application/json")
        if (!isEmpty(Storage.userId)) {
            requestBuilder.header("auth-id", Storage.userId)
        }
        return chain.proceed(requestBuilder.build())
    }

    companion object {
        lateinit var injector: Injector
        fun logout() {
            Storage.clear()
            Database.clear(Database.get())
        }
    }
}
