package us.handstand.kartwheel.mocks

import android.content.Context
import com.squareup.sqlbrite.BriteDatabase
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.DateFormatter
import java.util.regex.Pattern
import kotlin.reflect.KClass

fun Team.toJson(): String {
    return "{\"team\":${API.gson.toJson(this)}}"
}

fun Event.toJson(): String {
    return "{\"event\":${API.gson.toJson(this)}}"
}

fun User.toJson(): String {
    return "{\"user\":${API.gson.toJson(this)}}"
}

// TODO: Currently only works for races, courses and users
fun <T> List<T>.toJson(jsonClassName: String): String {
    return "{\"${jsonClassName}\":${API.gson.toJson(this)}}"
}

fun String.matches(pattern: Pattern): Boolean {
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

class MockAPI(val db: BriteDatabase?) {
    val server = MockWebServer()
    val context = MockDBContext()

    init {
        val okHttpClient = OkHttpClient.Builder().build()
        Storage.initialize(context.getSharedPreferences("", Context.MODE_PRIVATE))
        Database.initialize(context)
        Storage.clear()
        API.initialize(db, okHttpClient, server.url("/").uri().toString())

    }

    companion object {
        val coursesPattern = Pattern.compile("/events/[^/]+/courses")
        val racesPattern = Pattern.compile("/events/[^/]+/races")
        val raceParticipantsPattern = Pattern.compile("/events/[^/]+/races/[^/]+/users")
        val userPattern = Pattern.compile("/events/[^/]+/users/.*")
        val eventPattern = Pattern.compile("/events/.*")
        val teamPattern = Pattern.compile("/tickets/claim")
        const val eventName = "Kartwheel"
        const val teamName = "Sonic The Hedgebros"
        const val teamId = "80b1736f-88f3-4f2e-b8f7-f599b2ceca20"
        const val eventId = "73945c39-f0b7-47bc-a3e2-963532d02f94"
        val birth1 = DateFormatter["1989-07-25 00:00:00.000"]
        val birth2 = DateFormatter["1980-01-01 00:00:00.000"]
        const val buddyUrl1 = "https://assets.handstandwith.us/web/team/sam.jpg"
        const val buddyUrl2 = "https://assets.handstandwith.us/web/team/zip.jpg"
        const val cell1 = "408-306-4285"
        const val cell2 = "555-555-5555"
        val claimedAt1 = DateFormatter["2017-04-17 08:48:40.7787"]
        val claimedAt2 = DateFormatter["2017-04-17 08:50:00.0007"]
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
        val firstRaceStartTime = DateFormatter[System.currentTimeMillis()]!!
        val eventEndTime = DateFormatter["2017-05-19 03:07:43.537977"]!!
        val eventStartTime = DateFormatter["2017-05-17 03:07:43.537428"]!!
        val eventUpdatedAt = DateFormatter["2017-05-17 03:07:43.559896"]
        val team = Team.create(teamId, 0, eventId, 0, teamName, 0, 0, 0, null, mutableListOf(getTicket(1, true), getTicket(2, false)), null, mutableListOf(getUser(1, false), getUser(2, false)))
        val hellmanCourse = getHellmanCourse("course-hellman")
        val courses = listOf(hellmanCourse)
        val races = listOf(getRace(1, "race-1", hellmanCourse), getRace(2, "race-2", hellmanCourse), getRace(3, "race-3", hellmanCourse))

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

        fun getRace(order: Long, id: String, course: Course): Race {
            val startTime = DateFormatter[firstRaceStartTime.time + (order * (DateFormatter.millisecondsPerMinute * 5))]!!
            val endTime = DateFormatter[startTime.time + (order * (DateFormatter.millisecondsPerMinute * 5))]!!
            return Race.create(id, course, course.id(), null, eventId, endTime, null, "Race " + id, (Math.random() * course.maxRegistrants()!!.toDouble()).toLong(), order, null, null, null, null, null, null, startTime, (Math.random() * 3).toLong() + 1, null, null)
        }

        // TODO: Return actual list of users per race
        fun getRaceParticipants(): List<User> {
            return emptyList()
        }

        private fun getHellmanCourse(id: String): Course {
            val vertices = listOf(Point.create("point-1", true, 37.7683673194030, -122.489340137804, 1, null),
                    Point.create("point-2", true, 37.768382, -122.489512, 2, null),
                    Point.create("point-3", true, 37.768390, -122.489622, 3, null),
                    Point.create("point-4", true, 37.768438, -122.489742, 4, null),
                    Point.create("point-5", true, 37.768531, -122.489774, 5, null),
                    Point.create("point-6", true, 37.768658, -122.489713, 6, null),
                    Point.create("point-7", true, 37.7690764284113, -122.489159675429, 7, null),
                    Point.create("point-8", true, 37.7693557553345, -122.488354174534, 8, null),
                    Point.create("point-9", true, 37.7696981560791, -122.487787725518, 9, null),
                    Point.create("point-10", true, 37.7697151294330, -122.487099319810, 10, null),
                    Point.create("point-11", true, 37.7700530458593, -122.486380990708, 11, null),
                    Point.create("point-12", true, 37.7701762598359, -122.486168258006, 12, null),
                    Point.create("point-13", true, 37.770238, -122.485957, 13, null),
                    Point.create("point-14", true, 37.770058, -122.485971, 14, null),
                    Point.create("point-15", true, 37.769896, -122.485918, 15, null),
                    Point.create("point-16", true, 37.7697909856567, -122.486032303536, 16, null),
                    Point.create("point-17", true, 37.7694494650120, -122.486581318194, 17, null),
                    Point.create("point-18", true, 37.7691692998985, -122.487071324253, 18, null),
                    Point.create("point-19", true, 37.7690977184454, -122.487262096370, 19, null),
                    Point.create("point-20", true, 37.7685276652107, -122.488906458134, 20, null),
                    Point.create("point-21", true, 37.7683673194030, -122.489340137804, 21, null))
            return Course.create(id, eventUpdatedAt, null, (Math.random() * 5).toDouble(), (Math.random() * 15).toLong(), "Course " + id, 37.7683673194030, -122.489340137804, null, vertices)
        }
    }
}
