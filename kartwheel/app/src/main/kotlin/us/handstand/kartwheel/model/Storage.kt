package us.handstand.kartwheel.model


import android.content.SharedPreferences
import android.support.annotation.StringDef

class Storage private constructor(val prefs: SharedPreferences) {

    companion object {
        private const val USER_ID = "user_id"
        private const val USER_IMAGE_URL = "user_image_url"
        private const val TEAM_ID = "team_id"
        private const val EVENT_ID = "event_id"
        private const val TICKET_ID = "ticket_id"
        private const val EMOJI_CODE = "emoji_code"
        private const val SHOW_RACES = "show_races"

        @StringDef(USER_ID, EMOJI_CODE, EVENT_ID, TEAM_ID, TICKET_ID, SHOW_RACES, USER_IMAGE_URL)
        private annotation class KEYS

        private var instance: Storage? = null

        fun initialize(prefs: SharedPreferences) {
            if (instance == null) {
                instance = Storage(prefs)
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
        var ticketId: String
            get() {
                return get(TICKET_ID)
            }
            set(value) {
                set(TICKET_ID, value)
            }
        var userImageUrl: String
            get() {
                return get(USER_IMAGE_URL)
            }
            set(value) {
                set(USER_IMAGE_URL, value)
            }
        var showRaces: Boolean
            get() {
                return instance!!.prefs.getBoolean(SHOW_RACES, false)
            }
            set(value) {
                instance!!.prefs.edit().putBoolean(SHOW_RACES, value).apply()
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
            ticketId = ""
            showRaces = false
            eventId = ""
            code = ""
        }
    }
}
