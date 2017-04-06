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
                        Storage.code = ticketCode
                        Storage.teamId = team.id()
                        Storage.eventId = team.eventId()!!

                        val users = if (team.users() == null) emptyList<User>() else team.users()
                        for (user in users!!) {
                            Log.e(TAG, user.id())
                            user.insert()
                        }

                        val tickets = if (team.tickets() == null) emptyList<Ticket>() else team.tickets()
                        for (ticket in tickets!!) {
                            if (ticketCode == ticket.code()) {
                                userTicket = ticket
                                Storage.userId = userTicket.playerId()!!
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
        kartWheelService!!.getRaces(eventId).enqueue(object : Callback<JsonObject> {
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
        kartWheelService!!.updateUser(Storage.userId, Storage.eventId, jsonObject)
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

    fun forfeitTicket(ticketId: String, apiCallback: APICallback<JsonObject>) {
        kartWheelService!!.forfeitTicket(Storage.eventId, ticketId)
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>) {
                        try {
                            if (response.code() == 200) {
                                apiCallback.onSuccess(response.body())
                            } else {
                                apiCallback.onFailure(response.code(), response.errorBody().string())
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "forfeitTicket#onResponse", e)
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
    }
}
