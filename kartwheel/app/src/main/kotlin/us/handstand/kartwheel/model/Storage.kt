package us.handstand.kartwheel.model


import android.content.SharedPreferences
import android.support.annotation.StringDef
import us.handstand.kartwheel.controller.OnboardingController
import us.handstand.kartwheel.controller.TicketController

class Storage private constructor(val prefs: SharedPreferences) {

    companion object {
        private const val USER_ID = "user_id"
        private const val USER_IMAGE_URL = "user_image_url"
        private const val TEAM_ID = "team_id"
        private const val EVENT_ID = "event_id"
        private const val TICKET_ID = "ticket_id"
        private const val EMOJI_CODE = "emoji_code"
        private const val SHOW_RACES = "show_races"
        private const val LAST_TICKET_STEP = "last_ticket_step"
        private const val LAST_ONBOARDING_STEP = "last_onboarding_step"
        private const val SELFIE_URI = "selfi_uri"

        @StringDef(USER_ID, EMOJI_CODE, EVENT_ID, TEAM_ID, TICKET_ID, SHOW_RACES, USER_IMAGE_URL, LAST_TICKET_STEP, SELFIE_URI)
        private annotation class KEYS

        private lateinit var instance: Storage

        fun initialize(prefs: SharedPreferences) {
            instance = Storage(prefs)
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
                return instance.prefs.getBoolean(SHOW_RACES, false)
            }
            set(value) {
                instance.prefs.edit().putBoolean(SHOW_RACES, value).apply()
            }
        var lastTicketState: Long
            get() {
                return instance.prefs.getLong(LAST_TICKET_STEP, TicketController.Companion.TOS)
            }
            set(value) {
                instance.prefs.edit().putLong(LAST_TICKET_STEP, value).apply()
            }
        var lastOnboardingState: Long
            get() {
                return instance.prefs.getLong(LAST_ONBOARDING_STEP, OnboardingController.Companion.STARTED)
            }
            set(value) {
                instance.prefs.edit().putLong(LAST_ONBOARDING_STEP, value).apply()
            }
        var selfieUri: String
            get() {
                return get(SELFIE_URI)
            }
            set(value) {
                return set(SELFIE_URI, value)
            }

        private fun get(@KEYS key: String): String {
            return instance.prefs.getString(key, "")
        }

        private operator fun set(@KEYS key: String, value: String) {
            instance.prefs.edit().putString(key, value).apply()
        }

        fun clear() {
            userId = ""
            teamId = ""
            ticketId = ""
            showRaces = false
            eventId = ""
            code = ""
            userImageUrl = ""
            selfieUri = ""
            lastTicketState = TicketController.Companion.TOS
            lastOnboardingState = OnboardingController.Companion.STARTED
        }
    }
}
