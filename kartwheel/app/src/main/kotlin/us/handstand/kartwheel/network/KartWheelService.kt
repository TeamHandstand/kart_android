package us.handstand.kartwheel.network


import com.google.gson.JsonObject

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface KartWheelService {

    @POST("tickets/claim")
    fun claimTicket(@Body ticketCode: JsonObject): Call<JsonObject>

    @GET("events/{eventId}/races")
    fun getRaces(@Header("auth-id") userId: String, @Path("eventId") eventId: String): Call<JsonObject>

    @PUT("events/{eventId}/users/{userId}")
    fun updateUser(@Header("auth-id") @Path("userId") userId: String, @Path("eventId") eventId: String, @Body user: JsonObject): Call<JsonObject>
}
