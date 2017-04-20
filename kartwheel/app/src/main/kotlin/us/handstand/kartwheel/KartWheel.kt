package us.handstand.kartwheel


import android.app.Application
import android.preference.PreferenceManager
import android.text.TextUtils
import com.squareup.sqlbrite.BriteDatabase
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API
import java.io.IOException

class KartWheel : Application(), Interceptor {
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(this).build()


    override fun onCreate() {
        super.onCreate()
        Storage.initialize(PreferenceManager.getDefaultSharedPreferences(this))
        Database.initialize(this)
        API.initialize(Database.get(), okHttpClient, BuildConfig.SERVER)

        if (!TextUtils.isEmpty(Storage.userId)) {
            val query = User.FACTORY.select_all(Storage.userId)
            Database.get().createQuery(query.statement, query.statement, *query.args).subscribe({
                val cursor = it.run()
                cursor.use { cursor ->
                    if (cursor!!.moveToFirst()) {
                        user = User.SELECT_ALL_MAPPER.map(cursor)
                    }
                }
            })
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.header("Content-Type", "application/json")
        requestBuilder.header("Accept", "application/json")
        if (!TextUtils.isEmpty(Storage.userId)) {
            requestBuilder.header("auth-id", Storage.userId)
        }
        return chain.proceed(requestBuilder.build())
    }

    companion object {
        var user: User? = null
            private set

        fun logout() {
            Storage.clear()
            Database.clear(Database.get())
        }

        val db: BriteDatabase
            get() {
                return Database.get()
            }
    }
}
