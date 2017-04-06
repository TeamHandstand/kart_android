package us.handstand.kartwheel.network

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import us.handstand.kartwheel.BuildConfig

class SafeCallback(private val relay: API.APICallback<JsonObject>) : Callback<JsonObject> {

    override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
        try {
            if (response?.code() == 200) {
                relay.onSuccess(response.body() ?: JsonObject())
            } else {
                val errorString = (response?.body()?.asString ?: response?.errorBody()?.string())
                val errorJson: JsonObject? = parser.parse(errorString).asJsonObject
                relay.onFailure(response?.code() ?: 400, errorJson?.get("error")?.asString ?: defaultError)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            relay.onFailure(400, e.message ?: defaultError)
        }
    }

    override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
        if (BuildConfig.DEBUG) {
            t?.printStackTrace()
        }
        relay.onFailure(400, t?.message ?: defaultError)
    }

    private companion object {
        val defaultError = "Something went wrong"
        val parser = JsonParser()
    }
}

