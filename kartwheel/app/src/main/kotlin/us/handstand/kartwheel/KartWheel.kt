package us.handstand.kartwheel


import android.app.Application
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import us.handstand.kartwheel.model.AndroidStorage
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class KartWheel : Application(), Interceptor {
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(this).build()

    override fun onCreate() {
        super.onCreate()
        AndroidStorage.initialize(this)
        Database.initialize(this)
        API.initialize(okHttpClient, "http://10.0.0.173:3000")

        submitWork(Runnable {
            val getUserStatement = User.FACTORY.select_all(AndroidStorage.get(AndroidStorage.USER_ID))
            Database.get().query(getUserStatement.statement, *getUserStatement.args).use { cursor ->
                if (cursor.moveToFirst()) {
                    user = User.SELECT_ALL_MAPPER.map(cursor)
                }
            }
        })
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.header("Content-Type", "application/json")
        requestBuilder.header("Accept", "application/json")
        return chain.proceed(requestBuilder.build())
    }

    companion object {
        private val es = Executors.newCachedThreadPool()
        var user: User? = null
            private set

        fun submitWork(runnable: Runnable): Future<*> {
            return es.submit(runnable)
        }
    }
}
