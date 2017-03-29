package us.handstand.kartwheel.network;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

interface KartWheelService {

    @POST("tickets/claim")
    Call<JsonObject> claimTicket(@Body JsonObject ticketCode);

    @GET("events/{eventId}/races")
    Call<JsonObject> getRaces(@Header("auth-id") String userId, @Path("eventId") String eventId);

    @PUT("events/{eventId}/users/{userId}")
    Call<JsonObject> updateUser(@Header("auth-id") @Path("userId") String userId, @Path("eventId") String eventId, @Body JsonObject user);
}
