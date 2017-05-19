package us.handstand.kartwheel.network


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.squareup.sqlbrite.BriteDatabase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.util.DateFormatter
import java.util.*

object API {
    val gson: Gson = GsonBuilder()
            .setDateFormat(DateFormatter.DATE_FORMAT_STRING)
            .registerTypeAdapterFactory(GsonAdapterFactory.create())
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()!!
    var db: BriteDatabase? = null

    interface APICallback<in T : Any> {
        fun onSuccess(response: T)

        fun onFailure(errorCode: Int, errorResponse: String) {
            // Optional
        }
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
                    }
                    user.insert(db)
                }
                kartWheelService!!.getEvent(team.eventId()!!).enqueue(SafeCallback(object : APICallback<JsonObject> {
                    override fun onSuccess(response: JsonObject) {
                        val event = gson.fromJson(response.get("event"), Event::class.java)
                        event.insert(db)
                        Storage.showRaces = event.usersCanSeeRaces() == true
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
        kartWheelService!!.updateUser(Storage.userId, Storage.eventId, userJson)
                .enqueue(SafeCallback(object : APICallback<JsonObject> {
                    override fun onSuccess(response: JsonObject) {
                        val updatedUser = gson.fromJson(response.get("user"), User::class.java)
                        updatedUser.update(db)
                        apiCallback.onSuccess(updatedUser)
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        apiCallback.onFailure(errorCode, errorResponse)
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
                        for (course in courses) {
                            course.insert(db)
                            courseMap.put(course.id(), course)
                        }
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
                            for (race in races) {
                                race.insert(db)
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
            override fun onSuccess(courseResponse: Map<String, Course>) {
                // Subscribe to API response
                getRaces(eventId, false, object : API.APICallback<List<Race>> {
                    override fun onSuccess(response: List<Race>) {
                        for (race in response) {
                            race.insert(db, courseResponse[race.courseId()])
                        }
                    }
                })
            }
        })
    }
}
