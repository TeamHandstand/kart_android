package us.handstand.kartwheel.network;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import us.handstand.kartwheel.model.AndroidStorage;
import us.handstand.kartwheel.model.Database;
import us.handstand.kartwheel.model.GsonAdapterFactory;
import us.handstand.kartwheel.model.Race;
import us.handstand.kartwheel.model.Team;
import us.handstand.kartwheel.model.Ticket;
import us.handstand.kartwheel.model.User;

public class API {
    private static final String TAG = API.class.getName();
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(GsonAdapterFactory.create())
            .create();

    public static abstract class APICallback<T> {
        public abstract void onSuccess(T response);

        public abstract void onFailure(int errorCode, String errorResponse);
    }

    private static Retrofit retrofit;
    private static KartWheelService kartWheelService;

    public static void initialize(OkHttpClient okHttpClient, String url) {
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        kartWheelService = retrofit.create(KartWheelService.class);
    }

    // TODO: Show loading animation when claiming ticket
    public static void claimTicket(String ticketCode, final APICallback<Ticket> apiCallback) {
        Database.get().delete(Ticket.TABLE_NAME, null);
        Database.get().delete(User.TABLE_NAME, null);
        Database.get().delete(Team.TABLE_NAME, null);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", ticketCode);
        kartWheelService.claimTicket(jsonObject).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    Ticket userTicket = null;
                    if (response.code() == 200) {
                        Database.get().delete(Team.TABLE_NAME, null);
                        Log.e(TAG, response.body().toString());
                        Team team = gson.fromJson(response.body().get("team"), Team.class);
                        team.insert();
                        AndroidStorage.set(AndroidStorage.EMOJI_CODE, ticketCode);
                        AndroidStorage.set(AndroidStorage.TEAM_ID, team.id());
                        AndroidStorage.set(AndroidStorage.EVENT_ID, team.eventId());

                        List<User> users = team.users() == null ? Collections.emptyList() : team.users();
                        for (User user : users) {
                            user.insert();
                        }

                        List<Ticket> tickets = team.tickets() == null ? Collections.emptyList() : team.tickets();
                        for (Ticket ticket : tickets) {
                            if (ticketCode.equals(ticket.code())) {
                                userTicket = ticket;
                                AndroidStorage.set(AndroidStorage.USER_ID, ticket.playerId());
                            }
                            ticket.insert();
                        }
                        apiCallback.onSuccess(userTicket);
                    } else {
                        apiCallback.onFailure(response.code(), response.body().get("error").getAsString());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "claimTicket#onResponse", e);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static void getRaces(String eventId, final APICallback<List<Race>> apiCallback) {
        kartWheelService.getRaces(AndroidStorage.getString(AndroidStorage.USER_ID), eventId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.code() == 200) {
                        Race[] races = gson.fromJson(response.body().get("races"), Race[].class);
                        for (Race race : races) {
                            race.insert();
                            Log.e(TAG, "getRaces#onResponse:" + gson.toJson(race));
                        }
                        apiCallback.onSuccess(Arrays.asList(races));
                    } else {
                        apiCallback.onFailure(response.code(), response.errorBody().string());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "getRaces#onResponse", e);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static void updateUser(User user, final APICallback<User> apiCallback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user", gson.toJson(user));
        kartWheelService.updateUser(AndroidStorage.getString(AndroidStorage.USER_ID), AndroidStorage.getString(AndroidStorage.EVENT_ID), jsonObject)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.code() == 200) {
                                User updatedUser = gson.fromJson(response.body().get("user"), User.class);
                                updatedUser.insert();
                                apiCallback.onSuccess(updatedUser);
                            } else {
                                apiCallback.onFailure(response.code(), response.errorBody().string());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "updateUser#onResponse", e);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }
}
