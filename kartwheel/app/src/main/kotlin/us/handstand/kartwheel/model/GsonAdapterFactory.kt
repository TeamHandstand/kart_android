package us.handstand.kartwheel.model


import com.google.gson.TypeAdapterFactory
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory

@GsonTypeAdapterFactory
abstract class GsonAdapterFactory : TypeAdapterFactory {
    companion object {
        // Static factory method to access the package
        // private generated implementation
        @JvmStatic
        fun create(): TypeAdapterFactory {
            return AutoValueGson_GsonAdapterFactory()
        }
    }

}