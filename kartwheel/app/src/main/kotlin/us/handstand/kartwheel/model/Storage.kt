package us.handstand.kartwheel.model


import android.content.SharedPreferences
import android.support.annotation.StringDef
import us.handstand.kartwheel.controller.OnboardingController
import us.handstand.kartwheel.controller.TicketController

class Storage private constructor(val prefs: SharedPreferences) {

    companion object {
        private const val BUDDY_URI = "buddy_uri"
        private const val EMOJI_CODE = "emoji_code"
        private const val EVENT_ID = "event_id"
        private const val LAST_TICKET_STEP = "last_ticket_step"
        private const val LAST_ONBOARDING_STEP = "last_onboarding_step"
        private const val SELFIE_URI = "selfie_uri"
        private const val SELFIE_TRANSFER_ID = "selfie_transfer_id"
        private const val SHOW_RACES = "show_races"
        private const val FCM_TOKEN = "fcm_token"
        private const val TEAM_ID = "team_id"
        private const val TICKET_ID = "ticket_id"
        private const val RACE_ID = "race_id"
        private const val USER_ID = "user_id"
        private const val USER_BUDDY_URL = "user_buddy_url"
        private const val USER_IMAGE_URL = "user_image_url"

        @StringDef(BUDDY_URI, USER_ID, EMOJI_CODE, EVENT_ID, TEAM_ID, TICKET_ID, SHOW_RACES, USER_IMAGE_URL, USER_BUDDY_URL, LAST_TICKET_STEP, SELFIE_URI, SELFIE_TRANSFER_ID, FCM_TOKEN, RACE_ID)
        private annotation class KEYS

        private lateinit var instance: Storage

        fun initialize(prefs: SharedPreferences) {
            instance = Storage(prefs)
        }

        var userId: String
            get() {
                return getString(USER_ID)
            }
            set(value) {
                set(USER_ID, value)
            }
        var teamId: String
            get() {
                return getString(TEAM_ID)
            }
            set(value) {
                set(TEAM_ID, value)
            }
        var eventId: String
            get() {
                return getString(EVENT_ID)
            }
            set(value) {
                set(EVENT_ID, value)
            }
        var raceId: String
            get() {
                return getString(RACE_ID)
            }
            set(value) {
                set(RACE_ID, value)
            }
        var code: String
            get() {
                return getString(EMOJI_CODE)
            }
            set(value) {
                set(EMOJI_CODE, value)
            }
        var ticketId: String
            get() {
                return getString(TICKET_ID)
            }
            set(value) {
                set(TICKET_ID, value)
            }
        var userImageUrl: String
            get() {
                return getString(USER_IMAGE_URL)
            }
            set(value) {
                set(USER_IMAGE_URL, value)
            }
        var userBuddyUrl: String
            get() {
                return getString(USER_BUDDY_URL)
            }
            set(value) {
                set(USER_BUDDY_URL, value)
            }
        var showRaces: Boolean
            get() {
                return getBoolean(SHOW_RACES)
            }
            set(value) {
                set(SHOW_RACES, value)
            }
        var lastTicketState: Long
            get() {
                return getLong(LAST_TICKET_STEP, TicketController.TOS)
            }
            set(value) {
                set(LAST_TICKET_STEP, value)
            }
        var lastOnboardingState: Long
            get() {
                return getLong(LAST_ONBOARDING_STEP, OnboardingController.STARTED)
            }
            set(value) {
                set(LAST_ONBOARDING_STEP, value)
            }
        var selfieUri: String
            get() {
                return getString(SELFIE_URI)
            }
            set(value) {
                return set(SELFIE_URI, value)
            }
        var selectedBuddyUrl: String
            get() {
                return getString(BUDDY_URI)
            }
            set(value) {
                return set(BUDDY_URI, value)
            }
        var selfieTransferId: Int
            get() {
                return getInt(SELFIE_TRANSFER_ID)
            }
            set(value) {
                return set(SELFIE_TRANSFER_ID, value)
            }
        val pubNubChannelGroup: String
            get() {
                return "user-group-$userId"
            }
        var fcmToken: String
            get() {
                return getString(FCM_TOKEN)
            }
            set(value) {
                return set(FCM_TOKEN, value)
            }


        private fun getString(@KEYS key: String, default: String = ""): String {
            return instance.prefs.getString(key, default)
        }

        private fun getLong(@KEYS key: String, default: Long): Long {
            return instance.prefs.getLong(key, default)
        }

        private fun getInt(@KEYS key: String, default: Int = -1): Int {
            return instance.prefs.getInt(key, default)
        }

        private fun getBoolean(@KEYS key: String, default: Boolean = false): Boolean {
            return instance.prefs.getBoolean(key, default)
        }

        private operator fun set(@KEYS key: String, value: String) {
            instance.prefs.edit().putString(key, value).apply()
        }

        private operator fun set(@KEYS key: String, value: Int) {
            instance.prefs.edit().putInt(key, value).apply()
        }

        private operator fun set(@KEYS key: String, value: Long) {
            instance.prefs.edit().putLong(key, value).apply()
        }

        private operator fun set(@KEYS key: String, value: Boolean) {
            instance.prefs.edit().putBoolean(key, value).apply()
        }

        fun clear() {
            selectedBuddyUrl = ""
            code = ""
            eventId = ""
            raceId = ""
            lastTicketState = TicketController.Companion.TOS
            lastOnboardingState = OnboardingController.Companion.STARTED
            selfieTransferId = -1
            selfieUri = ""
            showRaces = false
            teamId = ""
            ticketId = ""
            userId = ""
            userBuddyUrl = ""
            userImageUrl = ""
        }
    }
}
