package us.handstand.kartwheel.network;


import android.content.ContentValues;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import us.handstand.kartwheel.model.Database;
import us.handstand.kartwheel.model.GsonAdapterFactory;
import us.handstand.kartwheel.model.Team;

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

    public static void claimTicket(String ticketCode, final APICallback<Team> apiCallback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", ticketCode);
        kartWheelService.claimTicket(jsonObject).enqueue(new APICallback<Team>(apiCallback) {
            @Override
            public void onResponse(Call<Team> call, Response<Team> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            Database.get().delete(Team.TABLE_NAME, null);
                            Team team = response.body();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Team.ID, team.id());
                            Database.get().insert(Team.TABLE_NAME, contentValues);
                            return;
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
