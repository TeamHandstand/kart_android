package us.handstand.kartwheel.network


import android.location.Location
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.squareup.sqlbrite2.BriteDatabase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.network.storage.StorageProvider
import us.handstand.kartwheel.util.DateFormatter
import java.util.*
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

object API {
    val gson: Gson = GsonBuilder()
            .setDateFormat(DateFormatter.DATE_FORMAT_STRING)
            .registerTypeAdapterFactory(GsonAdapterFactory.create())
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()!!
    var db: BriteDatabase? = null
    lateinit var storageProvider: StorageProvider

    interface APICallback<in T : Any> {
        fun onSuccess(response: T)

        fun onFailure(errorCode: Int, errorResponse: String) {
            Log.e("APICallback", errorResponse)
        }
    }

    class APITransactionCallback<in T : Any>(val transaction: BriteDatabase.Transaction?, val latch: CountDownLatch, val succeedOnAny: Boolean) : APICallback<T> {
        override fun onSuccess(response: T) {
            latch.countDown()
            if (latch.count == 0L) {
                transaction?.markSuccessful()
                transaction?.end()
            }
        }

        override fun onFailure(errorCode: Int, errorResponse: String) {
            latch.countDown()
            if (latch.count == 0L) {
                if (succeedOnAny) {
                    transaction?.markSuccessful()
                }
                transaction?.end()
            }
        }
    }

    private var retrofit: Retrofit? = null
    private var kartWheelService: KartWheelService? = null

    @Suppress("unused")
    @Inject
    fun initialize(okHttpClient: OkHttpClient, url: String, storageProvider: StorageProvider) {
        this.storageProvider = storageProvider
        retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
        kartWheelService = retrofit!!.create(KartWheelService::class.java)
    }

    // TODO: Show loading animation when claiming ticket
    fun claimTicket(ticketCode: String, apiCallback: APICallback<User>) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("code", ticketCode)
        kartWheelService!!.claimTicket(jsonObject).enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                val team = gson.fromJson(response.get("team"), Team::class.java)
                team.insert(db)
                Storage.code = ticketCode
                Storage.teamId = team.id()
                Storage.eventId = team.eventId()!!

                val tickets = if (team.tickets() == null) emptyList<Ticket>() else team.tickets()
                for (ticket in tickets!!) {
                    if (ticketCode == ticket.code()) {
                        Storage.userId = ticket.playerId()!!
                        Storage.ticketId = ticket.id()
                    }
                    ticket.insert(db)
                }

                val users = if (team.users() == null) emptyList<User>() else team.users()
                var loggedInUser: User? = null
                for (user in users!!) {
                    if (Storage.userId == user.id()) {
                        loggedInUser = user
                        Storage.userImageUrl = if (user.imageUrl() == null) "" else user.imageUrl()!!
                    }
                    user.insert(db)
                }
                kartWheelService!!.getEvent(team.eventId()!!).enqueue(SafeCallback(object : APICallback<JsonObject> {
                    override fun onSuccess(response: JsonObject) {
                        val event = gson.fromJson(response.get("event"), Event::class.java)
                        event.insert(db)
                        Storage.showRaces = event.usersCanSeeRaces() ?: false
                        apiCallback.onSuccess(loggedInUser!!)
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        apiCallback.onFailure(errorCode, errorResponse)
                    }
                }))
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                apiCallback.onFailure(errorCode, errorResponse)
            }
        }))
    }

    fun updateUser(user: User, apiCallback: APICallback<User>) {
        val userJson = gson.toJsonTree(user).asJsonObject
        updateUser(userJson, apiCallback)
    }

    fun updateUser(json: JsonObject, apiCallback: APICallback<User>? = null) {
        kartWheelService!!.updateUser(Storage.userId, Storage.eventId, json)
                .enqueue(SafeCallback(object : APICallback<JsonObject> {
                    override fun onSuccess(response: JsonObject) {
                        val updatedUser = gson.fromJson(response.get("user"), User::class.java)
                        updatedUser.update(db)
                        apiCallback?.onSuccess(updatedUser)
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        apiCallback?.onFailure(errorCode, errorResponse)
                    }
                }))
    }

    fun getUser(userId: String, useExecutor: Boolean = true, blindCallback: (User?) -> Unit) {
        kartWheelService!!.getUser(userId, Storage.eventId).enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                val user = gson.fromJson(response.get("user"), User::class.java)
                user.insertOrUpdate(db)
                blindCallback.invoke(user)
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                super.onFailure(errorCode, errorResponse)
                blindCallback.invoke(null)
            }
        }, useExecutor = useExecutor))
    }

    fun forfeitTicket(ticketId: String, apiCallback: APICallback<JsonElement>? = null) {
        kartWheelService!!.forfeitTicket(Storage.eventId, ticketId).enqueue(SafeCallback(apiCallback))
    }

    private fun getCourses(eventId: String, apiCallback: APICallback<Map<String, Course>>? = null) {
        kartWheelService!!.getCourses(eventId)
                .enqueue(SafeCallback(object : APICallback<JsonObject> {
                    override fun onSuccess(response: JsonObject) {
                        val courses = gson.fromJson(response.get("courses"), Array<Course>::class.java)
                        val courseMap = mutableMapOf<String, Course>()
                        db?.newTransaction()?.use {
                            courses.forEach {
                                it.insert(db)
                                courseMap.put(it.id(), it)
                            }
                            it.markSuccessful()
                        }
                        apiCallback?.onSuccess(courseMap)
                    }
                }))
    }

    private fun getRaces(eventId: String, save: Boolean = true, apiCallback: APICallback<List<Race>>? = null) {
        kartWheelService!!.getRaces(eventId)
                .enqueue(SafeCallback(object : APICallback<JsonObject> {
                    override fun onSuccess(response: JsonObject) {
                        val races = gson.fromJson(response.get("races"), Array<Race>::class.java)
                        if (save) {
                            db?.newTransaction()?.use {
                                races?.forEach { it.insert(db) }
                                it.markSuccessful()
                            }
                        }
                        apiCallback?.onSuccess(Arrays.asList(*races))
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        apiCallback?.onFailure(errorCode, errorResponse)
                    }
                }))
    }

    fun getRacesWithCourses(eventId: String) {
        getCourses(eventId, object : API.APICallback<Map<String, Course>> {
            override fun onSuccess(response: Map<String, Course>) {
                getRaces(eventId)
            }
        })
    }

    fun getTopCourseTimes(eventId: String, courseId: String) {
        kartWheelService!!.getTopCourseTimes(eventId, courseId).enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                db?.newTransaction()?.use {
                    val userRaceInfos = gson.fromJson(response.get("userRaceInfos"), Array<UserRaceInfo>::class.java)
                    userRaceInfos?.forEach { it.insertOrUpdate(db) }
                    it.markSuccessful()
                }
            }
        }))
    }

    fun getRaceParticipants(eventId: String, raceId: String, apiCallback: APICallback<Array<User>>? = null) {
        kartWheelService!!.getRaceParticipants(eventId, raceId).enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                db?.newTransaction()?.use {
                    val participants = gson.fromJson(response.get("users"), Array<User>::class.java)
                    participants?.forEach { it.updateRace(db, raceId) }
                    apiCallback?.onSuccess(participants)
                    it.markSuccessful()
                }
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                apiCallback?.onFailure(errorCode, errorResponse)
            }
        }))
    }

    fun getUserRaceInfos(eventId: String, raceId: String) {
        kartWheelService!!.getUserRaceInfos(eventId, raceId).enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                db?.newTransaction()?.use {
                    // Delete all previous UserRaceInfos
                    CompiledStatements.UserRaceInfo.deleteAllForRace.bind(raceId)
                    CompiledStatements.execute(db, CompiledStatements.UserRaceInfo.deleteAllForRace)
                    // Remove the race ID from all Users
                    CompiledStatements.User.removeRace.bind(raceId)
                    CompiledStatements.execute(db, CompiledStatements.User.removeRace)
                    // Insert the UserRaceInfos from the server. Will also update the Users
                    val userRaceInfos = gson.fromJson(response.get("userRaceInfos"), Array<UserRaceInfo>::class.java)
                    val latch = CountDownLatch(userRaceInfos?.size ?: 0)
                    userRaceInfos?.forEach { it.update(db, raceId, latch) }
                    latch.await()
                    it.markSuccessful()
                }
            }
        }))
    }

    fun joinRace(eventId: String, raceId: String, apiCallback: APICallback<UserRaceInfo>? = null) {
        kartWheelService!!.joinRace(eventId, raceId).enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                val userRaceInfo = gson.fromJson(response.get("userRaceInfo"), UserRaceInfo::class.java)
                userRaceInfo.update(db)
                apiCallback?.onSuccess(userRaceInfo)
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                super.onFailure(errorCode, errorResponse)
                apiCallback?.onFailure(errorCode, errorResponse)
            }
        }))
    }

    fun leaveRace(eventId: String, raceId: String, apiCallback: APICallback<Boolean>? = null) {
        kartWheelService!!.leaveRace(eventId, raceId).enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                val successfullyLeftRace = response.get("success").asBoolean
                if (successfullyLeftRace) {
                    db?.newTransaction()?.use {
                        val userId = Storage.userId
                        // Remove the race id for the User
                        CompiledStatements.User.updateRace.bind(null, userId)
                        CompiledStatements.execute(db, CompiledStatements.User.updateRace)
                        // Delete the UserRaceInfo for this race
                        CompiledStatements.UserRaceInfo.deleteForUserAndRace.bind(userId, raceId)
                        CompiledStatements.execute(db, CompiledStatements.UserRaceInfo.deleteForUserAndRace)
                        it.markSuccessful()
                    }
                }
                apiCallback?.onSuccess(successfullyLeftRace)
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                super.onFailure(errorCode, errorResponse)
                apiCallback?.onFailure(errorCode, errorResponse)
            }
        }))
    }

    fun updateLocation(eventId: String, raceId: String, userRaceInfoId: String, location: Location) {
        val locationUpdate = JsonObject()
        locationUpdate.addProperty("latitude", location.latitude)
        locationUpdate.addProperty("longitude", location.longitude)
        locationUpdate.addProperty("horizontalAccuracy", location.accuracy)
        locationUpdate.addProperty("clientTime", DateFormatter.dateFormat.format(DateFormatter[location.time]))
        kartWheelService!!.updateLocation(eventId, raceId, userRaceInfoId, locationUpdate)
    }

    fun getMiniGameTypes() {
        kartWheelService!!.getMiniGameTypes().enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                val miniGameTypes = gson.fromJson(response.get("miniGameTypes"), Array<MiniGameType>::class.java)
                if (miniGameTypes == null || miniGameTypes.isEmpty()) {
                    return
                }
                db?.newTransaction()?.use {
                    miniGameTypes.forEach { it.insert(db) }
                    it.markSuccessful()
                }
            }
        }))
    }
}
