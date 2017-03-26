package us.handstand.kartwheel.network;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import us.handstand.kartwheel.model.Database;
import us.handstand.kartwheel.model.GsonAdapterFactory;
import us.handstand.kartwheel.model.Team;
import us.handstand.kartwheel.model.Ticket;
import us.handstand.kartwheel.model.User;

public class API {
    private static final String TAG = API.class.getName();
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(GsonAdapterFactory.create())
            .create();

    public static abstract class APICallback<T> implements Callback<T> {
        private final APICallback<T> delegate;

        public APICallback() {
            this(null);
        }

        APICallback(APICallback<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            if (delegate != null) {
                delegate.onFailure(call, t);
            }
            t.printStackTrace();
        }
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
    public static void claimTicket(String ticketCode, final APICallback<JsonObject> apiCallback) {
        Database.get().delete(Ticket.TABLE_NAME, null);
        Database.get().delete(User.TABLE_NAME, null);
        Database.get().delete(Team.TABLE_NAME, null);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", ticketCode);
        kartWheelService.claimTicket(jsonObject).enqueue(new APICallback<JsonObject>(apiCallback) {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            Database.get().delete(Team.TABLE_NAME, null);
                            Log.e(TAG, response.body().toString());
                            Team team = gson.fromJson(response.body().get("team"), Team.class);
                            team.insert();

                            List<User> users = team.users() == null ? Collections.emptyList() : team.users();
                            for (User user : users) {
                                user.insert();
                            }

                            List<Ticket> tickets = team.tickets() == null ? Collections.emptyList() : team.tickets();
                            for (Ticket ticket : tickets) {
                                ticket.insert();
                            }
                            break;
                        case 404:
                            Log.e(TAG, "Not found");
                            break;
                        case 409:
                            Log.e(TAG, "Already claimed");
                            break;
                        default:
                            Log.e(TAG, "Code: " + response.code());
                    }
                    apiCallback.onResponse(call, response);
                } catch (Exception e) {
                    Log.e(TAG, "getProtocols#onResponse", e);
                }
            }
        });
    }
}
