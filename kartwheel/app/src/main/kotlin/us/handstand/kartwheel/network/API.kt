package us.handstand.kartwheel.network


import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.squareup.sqlbrite.BriteDatabase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import us.handstand.kartwheel.model.*

object API {
    private val TAG = API::class.java.name
    val gson = GsonBuilder()
            .registerTypeAdapterFactory(GsonAdapterFactory.create())
            .create()!!
    var db: BriteDatabase? = null

    abstract class APICallback<in T : Any> {
        abstract fun onSuccess(response: T)

        abstract fun onFailure(errorCode: Int, errorResponse: String)
    }

    private var retrofit: Retrofit? = null
    private var kartWheelService: KartWheelService? = null

    fun initialize(db: BriteDatabase?, okHttpClient: OkHttpClient, url: String) {
        this.db = db
        retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
        kartWheelService = retrofit!!.create(KartWheelService::class.java)
    }

    // TODO: Show loading animation when claiming ticket
    fun claimTicket(ticketCode: String, apiCallback: APICallback<Ticket>) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("code", ticketCode)
        kartWheelService!!.claimTicket(jsonObject).enqueue(SafeCallback(object : APICallback<JsonObject>() {
            override fun onSuccess(response: JsonObject) {
                val team = gson.fromJson(response.get("team"), Team::class.java)
                team.insert(db)
                Storage.code = ticketCode
                Storage.teamId = team.id()
                Storage.eventId = team.eventId()!!

                val users = if (team.users() == null) emptyList<User>() else team.users()
                for (user in users!!) {
                    user.insert(db)
                }

                var userTicket: Ticket? = null
                val tickets = if (team.tickets() == null) emptyList<Ticket>() else team.tickets()
                for (ticket in tickets!!) {
                    if (ticketCode == ticket.code()) {
                        userTicket = ticket
                        Storage.userId = ticket.playerId()!!
                    }
                    ticket.insert(db)
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
                        updatedUser.insert(db)
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
}
