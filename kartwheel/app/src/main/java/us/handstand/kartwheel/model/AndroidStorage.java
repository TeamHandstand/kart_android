package us.handstand.kartwheel.model;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringDef;

public class AndroidStorage {
    public static final String USER_ID = "user_id";
    public static final String EMOJI_CODE = "emoji_code";
    public static final String SERVER = "server";
    public static final String PORT = "port";

    @StringDef({USER_ID, EMOJI_CODE, SERVER, PORT})
    public @interface KEYS {
    }

    private static AndroidStorage instance;

    private final Context context;

    private AndroidStorage(Context context) {
        this.context = context.getApplicationContext();
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new AndroidStorage(context);
        }
    }

    public static String getString(@KEYS String key) {
        return instance.getPrefs().getString(key, null);
    }

    public static int getInt(@KEYS String key) {
        return instance.getPrefs().getInt(key, 80);
    }

    public static void set(@KEYS String key, String value) {
        instance.getPrefs().edit().putString(key, value).apply();
    }

    public static void set(@KEYS String key, int value) {
        instance.getPrefs().edit().putInt(key, value).apply();
    }

    public static String getUserId() {
        return getString(USER_ID);
    }
}
