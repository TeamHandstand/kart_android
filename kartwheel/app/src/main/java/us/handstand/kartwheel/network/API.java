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
                            /*
                           "id":"d7c3b1aa-cb22-4447-9ba6-77d361102dfb"
                           ,"name":"team-2"
                           ,"slug":null
                           ,"goldCount":0
                           ,"silverCount":0
                           ,"bronzeCount":0
                           ,"ribbonCount":0
                           ,"eventId":"6bcf26fb-daac-4d9a-8000-8448cc5fb530"
                           ,"users":[{"id":"f879f6ad-9f1c-4bb6-a3d4-7416645f2a4b","firstName":"Gil","lastName":"Levy","teamId":"d7c3b1aa-cb22-4447-9ba6-77d361102dfb","nickName":"gilly","cell":null,"email":"gil@handstandwith.us","referralType":null,"imageUrl":null,"pushEnabled":false,"facetimeCount":0,"pushDeviceToken":null,"pancakeOrWaffle":null,"charmanderOrSquirtle":null}
                                    ,{"id":"83fd76d5-ef5f-45c7-bb6f-417c7e1f979f","firstName":"Kyler","lastName":"Evitt","teamId":"d7c3b1aa-cb22-4447-9ba6-77d361102dfb","nickName":"kylery","cell":null,"email":"kyler@handstandwith.us","referralType":null,"imageUrl":null,"pushEnabled":false,"facetimeCount":0,"pushDeviceToken":null,"pancakeOrWaffle":null,"charmanderOrSquirtle":null}]
                          ,"tickets":[{"id":"f3a8d34f-5b03-40d9-8794-7988ebab09b8","code":"👽🎮🙈","claimedAt":"2017-03-26T09:09:29.968Z","playerId":"83fd76d5-ef5f-45c7-bb6f-417c7e1f979f","teamId":"d7c3b1aa-cb22-4447-9ba6-77d361102dfb","priceTierId":null,"paymentId":null,"forfeitedAt":null,"purchasedAt":null}
                                     ,{"id":"f763c3ac-4ff3-4c64-a77a-95a54f1b5a1b","code":"🎨🙊💩","claimedAt":"2017-03-26T09:11:31.008Z","playerId":"f879f6ad-9f1c-4bb6-a3d4-7416645f2a4b","teamId":"d7c3b1aa-cb22-4447-9ba6-77d361102dfb","priceTierId":null,"paymentId":null,"forfeitedAt":null,"purchasedAt":null}]}}
                             */

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
