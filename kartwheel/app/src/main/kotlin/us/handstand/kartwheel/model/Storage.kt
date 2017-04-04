package us.handstand.kartwheel.model


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.annotation.StringDef

class Storage private constructor(context: Context) {
    private val context: Context = context.applicationContext

    private val prefs: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val USER_ID = "user_id"
        private const val TEAM_ID = "team_id"
        private const val EVENT_ID = "event_id"
        private const val EMOJI_CODE = "emoji_code"

        @StringDef(USER_ID, EMOJI_CODE, EVENT_ID, TEAM_ID)
        private annotation class KEYS

        private var instance: Storage? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = Storage(context)
            }
        }

        var userId: String
            get() {
                return get(USER_ID)
            }
            set(value) {
                set(USER_ID, value)
            }
        var teamId: String
            get() {
                return get(TEAM_ID)
            }
            set(value) {
                set(TEAM_ID, value)
            }
        var eventId: String
            get() {
                return get(EVENT_ID)
            }
            set(value) {
                set(EVENT_ID, value)
            }
        var code: String
            get() {
                return get(EMOJI_CODE)
            }
            set(value) {
                set(EMOJI_CODE, value)
            }

        private fun get(@KEYS key: String): String {
            return instance!!.prefs.getString(key, "")
        }

        private operator fun set(@KEYS key: String, value: String) {
            instance!!.prefs.edit().putString(key, value).apply()
        }

        fun clear() {
            userId = ""
            teamId = ""
            eventId = ""
            code = ""
        }
    }
}
