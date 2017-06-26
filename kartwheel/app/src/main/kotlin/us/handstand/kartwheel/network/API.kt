package us.handstand.kartwheel.network


import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.squareup.sqlbrite.BriteDatabase
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

    fun forfeitTicket(ticketId: String, apiCallback: APICallback<JsonElement>? = null) {
        kartWheelService!!.forfeitTicket(Storage.eventId, ticketId).enqueue(SafeCallback(apiCallback))
    }

    private fun getCourses(eventId: String, apiCallback: APICallback<Map<String, Course>>? = null) {
        kartWheelService!!.getCourses(eventId)
                .enqueue(SafeCallback(object : APICallback<JsonObject> {
                    override fun onSuccess(response: JsonObject) {
                        val courses = gson.fromJson(response.get("courses"), Array<Course>::class.java)
                        val courseMap = mutableMapOf<String, Course>()
                        val transaction = db?.newTransaction()
                        for (course in courses) {
                            course.insert(db)
                            courseMap.put(course.id(), course)
                        }
                        transaction?.markSuccessful()
                        transaction?.end()
                        apiCallback?.onSuccess(courseMap)
                    }
                }))
    }

    private fun getRaces(eventId: String, save: Boolean, apiCallback: APICallback<List<Race>>? = null) {
        kartWheelService!!.getRaces(eventId)
                .enqueue(SafeCallback(object : APICallback<JsonObject> {
                    override fun onSuccess(response: JsonObject) {
                        val races = gson.fromJson(response.get("races"), Array<Race>::class.java)
                        if (save) {
                            val transaction = db?.newTransaction()
                            for (race in races) {
                                race.insert(db)
                            }
                            transaction?.markSuccessful()
                            transaction?.end()
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
            override fun onSuccess(courseResponse: Map<String, Course>) {
                // Don't save this time
                getRaces(eventId, false, object : API.APICallback<List<Race>> {
                    override fun onSuccess(response: List<Race>) {
                        val transaction = db?.newTransaction()
                        val latch = CountDownLatch(response.size)
                        for (race in response) {
                            race.insert(db, courseResponse[race.courseId()])
                            getRaceParticipants(eventId, race.id(), APITransactionCallback(transaction, latch, true))
                        }
                    }
                })
            }
        })
    }

    fun getRaceParticipants(eventId: String, raceId: String, apiCallback: APICallback<Array<User>>? = null) {
        kartWheelService!!.getRaceParticipants(eventId, raceId).enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                val participants = gson.fromJson(response.get("users"), Array<User>::class.java)
                val participantIds = mutableListOf<String>()
                val participantImageUrls = mutableListOf<String>()
                val transaction = db?.newTransaction()
                for (user in participants) {
                    participantIds.add(user.id())
                    participantImageUrls.add(if (user.imageUrl() == null) "" else user.imageUrl()!!)
                    user.insertWithRaceId(db, raceId)
                }
                val update = RaceModel.Insert_participants(db?.writableDatabase, Race.FACTORY)
                update.bind(participantIds, participantImageUrls, raceId)
                db?.executeUpdateDelete(update.table, update.program)
                transaction?.markSuccessful()
                transaction?.end()
                apiCallback?.onSuccess(participants)
            }

            override fun onFailure(errorCode: Int, errorResponse: String) {
                apiCallback?.onFailure(errorCode, errorResponse)
            }
        }))
    }

    fun joinRace(eventId: String, raceId: String, apiCallback: APICallback<UserRaceInfo>? = null) {
        kartWheelService!!.joinRace(eventId, raceId).enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                val userRaceInfo = gson.fromJson(response.get("userRaceInfo"), UserRaceInfo::class.java)
                userRaceInfo.insert(db)
                apiCallback?.onSuccess(userRaceInfo)
            }
        }))
    }

    fun leaveRace(eventId: String, raceId: String, apiCallback: APICallback<Boolean>? = null) {
        kartWheelService!!.leaveRace(eventId, raceId).enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                val successfullyLeftRace = response.get("success").asBoolean
                // Delete the UserRaceInfo for this race
                val userRaceInfoQuery = UserRaceInfo.FACTORY.select_for_race_and_user(raceId, Storage.userId)
                db?.createQuery(UserRaceInfoModel.TABLE_NAME, userRaceInfoQuery.statement, *userRaceInfoQuery.args)
                        ?.mapToOne { UserRaceInfo.FACTORY.select_for_race_and_userMapper().map(it) }
                        ?.doOnNext { it.delete(db) }
                apiCallback?.onSuccess(successfullyLeftRace)
            }
        }))
    }

    fun getMiniGameTypes() {
        kartWheelService!!.getMiniGameTypes().enqueue(SafeCallback(object : APICallback<JsonObject> {
            override fun onSuccess(response: JsonObject) {
                val miniGameTypes = gson.fromJson(response.get("miniGameTypes"), Array<MiniGameType>::class.java)
                if (miniGameTypes == null || miniGameTypes.isEmpty()) {
                    return
                }
                val transaction = db?.newTransaction()
                for (miniGameType in miniGameTypes) {
                    miniGameType.insert(db)
                }
                transaction?.markSuccessful()
                transaction?.end()
            }
        }))
    }
}
