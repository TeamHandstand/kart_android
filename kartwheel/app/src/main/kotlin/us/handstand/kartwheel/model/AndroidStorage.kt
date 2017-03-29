package us.handstand.kartwheel.model


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.annotation.StringDef

class AndroidStorage private constructor(context: Context) {
    private val context: Context

    init {
        this.context = context.applicationContext
    }

    private val prefs: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        const val USER_ID = "user_id"
        const val TEAM_ID = "team_id"
        const val EVENT_ID = "event_id"
        const val EMOJI_CODE = "emoji_code"

        @StringDef(USER_ID, EMOJI_CODE, EVENT_ID, TEAM_ID)
        annotation class KEYS

        private var instance: AndroidStorage? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = AndroidStorage(context)
            }
        }

        fun get(@KEYS key: String): String {
            return instance!!.prefs.getString(key, null)
        }

        operator fun set(@KEYS key: String, value: String) {
            instance!!.prefs.edit().putString(key, value).apply()
        }
    }
}
