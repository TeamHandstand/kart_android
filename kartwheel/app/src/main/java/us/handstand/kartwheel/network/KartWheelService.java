package us.handstand.kartwheel.network;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import us.handstand.kartwheel.model.Team;

interface KartWheelService {

    @POST("tickets/claim")
    Call<Team> claimTicket(@Body JsonObject ticketCode);
}
