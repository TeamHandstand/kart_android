package us.handstand.kartwheel.network


import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

internal interface KartWheelService {

    @POST("tickets/claim")
    fun claimTicket(@Body ticketCode: JsonObject): Call<JsonObject>

    @GET("events/{eventId}/races")
    fun getRaces(@Header("auth-id") userId: String, @Path("eventId") eventId: String): Call<JsonObject>

    @PUT("events/{eventId}/users/{userId}")
    fun updateUser(@Header("auth-id") @Path("userId") userId: String, @Path("eventId") eventId: String, @Body user: JsonObject): Call<JsonObject>

    @POST("events/{eventId}/tickets/{ticketId}/forfeit")
    fun forfeitTicket(@Header("auth-id") userId: String, @Path("eventId") eventId: String, @Path("ticketId") ticketId: String): Call<JsonObject>
}
