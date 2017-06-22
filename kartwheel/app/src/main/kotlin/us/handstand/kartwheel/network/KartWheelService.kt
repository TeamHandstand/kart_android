package us.handstand.kartwheel.network


import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*
import us.handstand.kartwheel.BuildConfig

internal interface KartWheelService {

    @POST(BuildConfig.SERVER_FRAGMENT + "tickets/claim")
    fun claimTicket(@Body ticketCode: JsonObject): Call<JsonElement>

    @PUT(BuildConfig.SERVER_FRAGMENT + "events/{eventId}/users/{userId}")
    fun updateUser(@Path("userId") userId: String, @Path("eventId") eventId: String, @Body user: JsonObject): Call<JsonElement>

    @POST(BuildConfig.SERVER_FRAGMENT + "events/{eventId}/tickets/{ticketId}/forfeit")
    fun forfeitTicket(@Path("eventId") eventId: String, @Path("ticketId") ticketId: String): Call<JsonElement>

    @GET(BuildConfig.SERVER_FRAGMENT + "events/{eventId}/races")
    fun getRaces(@Path("eventId") eventId: String): Call<JsonElement>

    @GET(BuildConfig.SERVER_FRAGMENT + "events/{eventId}/courses")
    fun getCourses(@Path("eventId") eventId: String): Call<JsonElement>

    @GET(BuildConfig.SERVER_FRAGMENT + "events/{eventId}")
    fun getEvent(@Path("eventId") eventId: String): Call<JsonElement>

    @GET(BuildConfig.SERVER_FRAGMENT + "events/{eventId}/races/{raceId}/users")
    fun getRaceParticipants(@Path("eventId") eventId: String, @Path("raceId") raceId: String): Call<JsonElement>

    @POST(BuildConfig.SERVER_FRAGMENT + "events/{eventId}/races/{raceId}/join")
    fun joinRace(@Path("eventId") eventId: String, @Path("raceId") raceId: String): Call<JsonElement>

    @POST(BuildConfig.SERVER_FRAGMENT + "events/{eventId}/races/{raceId}/leave")
    fun leaveRace(@Path("eventId") eventId: String, @Path("raceId") raceId: String): Call<JsonElement>

    @GET(BuildConfig.SERVER_FRAGMENT + "mini_game_types")
    fun getMiniGameTypes(): Call<JsonElement>
}
