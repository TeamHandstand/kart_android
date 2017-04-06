package us.handstand.kartwheel.network


import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import us.handstand.kartwheel.model.*
import java.util.*

object API {
    private val TAG = API::class.java.name
    val gson = GsonBuilder()
            .registerTypeAdapterFactory(GsonAdapterFactory.create())
            .create()!!

    abstract class APICallback<in T : Any> {
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
        kartWheelService!!.claimTicket(jsonObject).enqueue(SafeCallback(object : APICallback<JsonObject>() {
            override fun onSuccess(response: JsonObject) {
                Database.get().delete(TeamModel.TABLE_NAME, null)
                Log.e(TAG, response.toString())
                val team = gson.fromJson(response.get("team"), Team::class.java)
                team.insert()
                Storage.code = ticketCode
                Storage.teamId = team.id()
                Storage.eventId = team.eventId()!!

                val users = if (team.users() == null) emptyList<User>() else team.users()
                for (user in users!!) {
                    Log.e(TAG, user.id())
                    user.insert()
                }

                var userTicket: Ticket? = null
                val tickets = if (team.tickets() == null) emptyList<Ticket>() else team.tickets()
                for (ticket in tickets!!) {
                    if (ticketCode == ticket.code()) {
                        userTicket = ticket
                        Storage.userId = userTicket.playerId()!!
                    }
                    ticket.insert()
                }
                apiCallback.onSuccess(userTicket!!)
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                apiCallback.onFailure(errorCode, errorResponse)
            }
        }))
    }

    fun updateUser(user: User, apiCallback: APICallback<User>) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user", gson.toJson(user))
        kartWheelService!!.updateUser(Storage.userId, Storage.eventId, jsonObject)
                .enqueue(SafeCallback(object : APICallback<JsonObject>() {
                    override fun onSuccess(response: JsonObject) {
                        val updatedUser = gson.fromJson(response.get("user"), User::class.java)
                        updatedUser.insert()
                        apiCallback.onSuccess(updatedUser)
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        apiCallback.onFailure(errorCode, errorResponse)
                    }
                }))
    }

    fun forfeitTicket(ticketId: String, apiCallback: APICallback<JsonObject>) {
        kartWheelService!!.forfeitTicket(Storage.eventId, ticketId).enqueue(SafeCallback(apiCallback))
    }

    fun getRaces(eventId: String, apiCallback: APICallback<List<Race>>) {
        kartWheelService!!.getRaces(eventId)
                .enqueue(SafeCallback(object : APICallback<JsonObject>() {
                    override fun onSuccess(response: JsonObject) {
                        val races = gson.fromJson(response.get("races"), Array<Race>::class.java)
                        for (race in races) {
                            race.insert()
                            Log.e(TAG, "getRaces#onResponse:" + gson.toJson(race))
                        }
                        apiCallback.onSuccess(Arrays.asList(*races))
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        apiCallback.onFailure(errorCode, errorResponse)
                    }
                }))
    }
}
