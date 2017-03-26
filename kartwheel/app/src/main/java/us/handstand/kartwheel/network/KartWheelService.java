package us.handstand.kartwheel.network;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface KartWheelService {

    @POST("tickets/claim")
    Call<JsonObject> claimTicket(@Body JsonObject ticketCode);
}
