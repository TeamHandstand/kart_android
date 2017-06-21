package us.handstand.kartwheel.inject.provider

import android.text.TextUtils.isEmpty
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import us.handstand.kartwheel.model.Storage

@Module(includes = arrayOf(CloudStorageProvider::class))
class ApiProvider(val url: String) : Interceptor {
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(this).build()

    @Provides fun okHttpClient(): OkHttpClient = okHttpClient
    @Provides fun url(): String = url

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.header("Content-Type", "application/json")
        requestBuilder.header("Accept", "application/json")
        if (!isEmpty(Storage.userId)) {
            requestBuilder.header("auth-id", Storage.userId)
        }
        return chain.proceed(requestBuilder.build())
    }
}
