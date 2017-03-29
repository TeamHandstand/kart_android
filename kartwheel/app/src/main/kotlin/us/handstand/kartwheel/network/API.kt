package us.handstand.kartwheel.network


import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import us.handstand.kartwheel.model.*
import java.util.*

object API {
    private val TAG = API::class.java.name
    val gson = GsonBuilder()
            .registerTypeAdapterFactory(GsonAdapterFactory.create())
            .create()

    abstract class APICallback<T> {
        abstract fun onSuccess(response: T)

        abstract fun onFailure(errorCode: Int, errorResponse: String)
    }

    private var retrofit: Retrofit? = null
    private var kartWheelService: KartWheelService? = null

    fun initialize(okHttpClient: OkHttpClient, url: String) {
        retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
        kartWheelService = retrofit!!.create(KartWheelService::class.java)
    }

    // TODO: Show loading animation when claiming ticket
    fun claimTicket(ticketCode: String, apiCallback: APICallback<Ticket>) {
        Database.get().delete(TicketModel.TABLE_NAME, null)
        Database.get().delete(UserModel.TABLE_NAME, null)
        Database.get().delete(TeamModel.TABLE_NAME, null)

        val jsonObject = JsonObject()
        jsonObject.addProperty("code", ticketCode)
        kartWheelService!!.claimTicket(jsonObject).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                try {
                    var userTicket: Ticket? = null
                    if (response.code() == 200) {
                        Database.get().delete(TeamModel.TABLE_NAME, null)
                        Log.e(TAG, response.body().toString())
                        val team = gson.fromJson(response.body().get("team"), Team::class.java)
                        team.insert()
                        AndroidStorage.set(AndroidStorage.EMOJI_CODE, ticketCode)
                        AndroidStorage.set(AndroidStorage.TEAM_ID, team.id())
                        AndroidStorage.set(AndroidStorage.EVENT_ID, team.eventId()!!)

                        val users = if (team.users() == null) emptyList<User>() else team.users()
                        for (user in users!!) {
                            user.insert()
                        }

                        val tickets = if (team.tickets() == null) emptyList<Ticket>() else team.tickets()
                        for (ticket in tickets!!) {
                            if (ticketCode == ticket.code()) {
                                userTicket = ticket
                                AndroidStorage.set(AndroidStorage.USER_ID, userTicket.playerId()!!)
                            }
                            ticket.insert()
                        }
                        apiCallback.onSuccess(userTicket!!)
                    } else {
                        apiCallback.onFailure(response.code(), response.body().get("error").asString)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "claimTicket#onResponse", e)
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun getRaces(eventId: String, apiCallback: APICallback<List<Race>>) {
        kartWheelService!!.getRaces(AndroidStorage.get(AndroidStorage.USER_ID), eventId).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                try {
                    if (response.code() == 200) {
                        val races = gson.fromJson(response.body().get("races"), Array<Race>::class.java)
                        for (race in races) {
                            race.insert()
                            Log.e(TAG, "getRaces#onResponse:" + gson.toJson(race))
                        }
                        apiCallback.onSuccess(Arrays.asList(*races))
                    } else {
                        apiCallback.onFailure(response.code(), response.errorBody().string())
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "getRaces#onResponse", e)
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun updateUser(user: User, apiCallback: APICallback<User>) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user", gson.toJson(user))
        kartWheelService!!.updateUser(AndroidStorage.get(AndroidStorage.USER_ID), AndroidStorage.get(AndroidStorage.EVENT_ID), jsonObject)
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        try {
                            if (response.code() == 200) {
                                val updatedUser = gson.fromJson(response.body().get("user"), User::class.java)
                                updatedUser.insert()
                                apiCallback.onSuccess(updatedUser)
                            } else {
                                apiCallback.onFailure(response.code(), response.errorBody().string())
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "updateUser#onResponse", e)
                        }

                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
    }
}
