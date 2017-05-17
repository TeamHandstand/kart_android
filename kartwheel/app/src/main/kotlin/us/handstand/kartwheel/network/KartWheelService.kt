package us.handstand.kartwheel.network


import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

internal interface KartWheelService {

    @POST("tickets/claim")
    fun claimTicket(@Body ticketCode: JsonObject): Call<JsonObject>

    @PUT("events/{eventId}/users/{userId}")
    fun updateUser(@Path("userId") userId: String, @Path("eventId") eventId: String, @Body user: JsonObject): Call<JsonObject>

    @POST("events/{eventId}/tickets/{ticketId}/forfeit")
    fun forfeitTicket(@Path("eventId") eventId: String, @Path("ticketId") ticketId: String): Call<JsonObject>

    @GET("events/{eventId}/races")
    fun getRaces(@Path("eventId") eventId: String): Call<JsonObject>

    @GET("events/{eventId}/courses")
    fun getCourses(@Path("eventId") eventId: String): Call<JsonObject>

    @GET("events/{eventId}")
    fun getEvent(@Path("eventId") eventId: String): Call<JsonObject>
}
