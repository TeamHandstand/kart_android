package us.handstand.kartwheel.mocks

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.DateFormatter

fun Team.toJson(): String {
    return "{\"team\":${API.gson.toJson(this)}}"
}

fun Event.toJson(): String {
    return "{\"event\":${API.gson.toJson(this)}}"
}

fun User.toJson(): String {
    return "{\"user\":${API.gson.toJson(this)}}"
}

class MockAPI {
    val server = MockWebServer()
    val context = MockDBContext()

    init {
        val okHttpClient = OkHttpClient.Builder().build()
        Storage.initialize(context.getSharedPreferences("", Context.MODE_PRIVATE))
        Database.initialize(context)
        Storage.clear()
        API.initialize(null, okHttpClient, server.url("/").uri().toString())
    }

    companion object {
        const val eventName = "Kartwheel"
        const val teamName = "Sonic The Hedgebros"
        const val teamId = "80b1736f-88f3-4f2e-b8f7-f599b2ceca20"
        const val eventId = "73945c39-f0b7-47bc-a3e2-963532d02f94"
        val birth1 = DateFormatter.get("1989-07-25 00:00:00.000")!!
        val birth2 = DateFormatter.get("1980-01-01 00:00:00.000")!!
        const val buddyUrl1 = "https://assets.handstandwith.us/web/team/sam.jpg"
        const val buddyUrl2 = "https://assets.handstandwith.us/web/team/zip.jpg"
        const val cell1 = "408-306-4285"
        const val cell2 = "555-555-5555"
        const val claimedAt1 = "2017-04-17T08:48:40.778Z"
        const val claimedAt2 = "2017-04-17T08:50:00.000Z"
        const val code1 = "matt"
        const val code2 = "bob"
        const val email1 = "matthew@ott.com"
        const val email2 = "boblob@law.com"
        const val firstName1 = "Matt"
        const val firstName2 = "Bob"
        const val imageUrl1 = "https://assets.handstandwith.us/web/team/sam.jpg"
        const val imageUrl2 = "https://assets.handstandwith.us/web/team/zip.jpg"
        const val lastName1 = "Ott"
        const val lastName2 = "Lob"
        const val nickname1 = "Matty"
        const val nickname2 = "Law"
        const val ticketId1 = "212c5182-4ee4-402b-b7ae-784ecfb4b90e"
        const val ticketId2 = "18112c1a-6ab2-48d1-aeb5-92dfaeeffa89"
        const val userId1 = "934bd187-44a1-42b6-8412-74346e92470d"
        const val userId2 = "5d07e1f3-a1ee-40bb-a0f5-0d3361f3b394"
        val eventEndTime = DateFormatter.get("2017-05-19 03:07:43.537977")!!
        val eventStartTime = DateFormatter.get("2017-05-17 03:07:43.537428")!!
        val eventUpdatedAt = DateFormatter.get("2017-05-17 03:07:43.559896")!!
        val team = Team.create(teamId, 0, eventId, 0, teamName, 0, 0, 0, null, mutableListOf(getTicket(1, true), getTicket(2, false)), null, mutableListOf(getUser(1, false), getUser(2, false)))

        fun getUser(which: Int, onboarded: Boolean): User {
            if (which == 1) {
                return User.create(userId1, null, birth1, if (onboarded) buddyUrl1 else null, cell1, null, email1, eventId, firstName1, if (onboarded) imageUrl1 else null, lastName1, null, nickname1, null, null, false, null, null, teamId, null, null, null)
            } else {
                return User.create(userId2, null, birth2, if (onboarded) buddyUrl2 else null, cell2, null, email2, eventId, firstName2, if (onboarded) imageUrl2 else null, lastName2, null, nickname2, null, null, false, null, null, teamId, null, null, null)
            }
        }

        fun getTicket(which: Int, claimed: Boolean): Ticket {
            if (which == 1) {
                return Ticket.create(ticketId1, code1, if (claimed) claimedAt1 else null, eventId, null, null, userId1, null, null, null, teamId, null)
            } else {
                return Ticket.create(ticketId2, code2, if (claimed) claimedAt2 else null, eventId, null, null, userId2, null, null, null, teamId, null)
            }
        }

        fun getEvent(canSeeRaces: Boolean): Event {
            return Event.create(eventId, eventEndTime, eventName, eventStartTime, eventUpdatedAt, canSeeRaces)
        }
    }
}
