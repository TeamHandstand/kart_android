package us.handstand.kartwheel;


import android.app.Application;
import android.database.Cursor;

import com.squareup.sqldelight.SqlDelightStatement;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import us.handstand.kartwheel.model.AndroidStorage;
import us.handstand.kartwheel.model.Database;
import us.handstand.kartwheel.model.User;
import us.handstand.kartwheel.network.API;

public class KartWheel extends Application implements Interceptor {
    private static final ExecutorService es = Executors.newCachedThreadPool();
    private static User user;
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(this).build();

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidStorage.initialize(this);
        Database.initialize(this);
        API.initialize(okHttpClient, "http://10.0.0.173:3000");

        submitWork(() -> {
            SqlDelightStatement getUserStatement = User.FACTORY.select_all(AndroidStorage.getString(AndroidStorage.USER_ID));
            try (Cursor cursor = Database.get().query(getUserStatement.statement, getUserStatement.args)) {
                if (cursor.moveToFirst()) {
                    user = User.SELECT_ALL_MAPPER.map(cursor);
                }
            }
        });
    }

    public static User getUser() {
        return user;
    }

    public static Future submitWork(Runnable runnable) {
        return es.submit(runnable);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();
        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Accept", "application/json");
        return chain.proceed(requestBuilder.build());
    }
}
