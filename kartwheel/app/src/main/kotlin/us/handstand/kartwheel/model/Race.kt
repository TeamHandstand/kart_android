package us.handstand.kartwheel.model


import android.content.ContentValues
import android.support.annotation.IntDef
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import us.handstand.kartwheel.model.RaceModel.*
import us.handstand.kartwheel.model.Util.putIfNotAbsent
import us.handstand.kartwheel.util.DateFormatter
import java.util.*

@AutoValue
abstract class Race : RaceModel, Comparable<Race>, Insertable {

    override fun compareTo(other: Race): Int {
        val myRaceOrder = raceOrder()
        val theirRaceOrder = other.raceOrder()
        if (myRaceOrder == null) {
            return 1
        } else if (theirRaceOrder == null) {
            return -1
        } else {
            return myRaceOrder.compareTo(theirRaceOrder)
        }
    }

    @IntDef(FINISHED, REGISTERED, REGISTRATION_CLOSED, RACE_IS_FULL, HAS_OPEN_SPOTS)
    annotation class RaceStatus

    fun alreadyStarted(): Boolean {
        val startTime = startTime()
        return startTime == null || startTime.time - System.currentTimeMillis() < 0
    }

    val timeUntilRace: Long
        get() = if (startTime() == null) 0L else startTime()!!.time - System.currentTimeMillis()

    override fun tableName(): String {
        return TABLE_NAME
    }

    override val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, RaceModel.ID, id())
            putIfNotAbsent(cv, RaceModel.COURSEID, courseId())
            putIfNotAbsent(cv, RaceModel.DELETEDAT, deletedAt()?.time)
            putIfNotAbsent(cv, RaceModel.EVENTID, eventId())
            putIfNotAbsent(cv, RaceModel.ENDTIME, endTime()?.time)
            putIfNotAbsent(cv, RaceModel.FUNQUESTION, funQuestion())
            putIfNotAbsent(cv, RaceModel.NAME, name())
            putIfNotAbsent(cv, RaceModel.OPENSPOTS, openSpots())
            putIfNotAbsent(cv, RaceModel.RACEORDER, raceOrder())
            putIfNotAbsent(cv, RaceModel.REPLAYURL, replayUrl())
            putIfNotAbsent(cv, RaceModel.SHORTANSWER1, shortAnswer1())
            putIfNotAbsent(cv, RaceModel.SHORTANSWER2, shortAnswer2())
            putIfNotAbsent(cv, RaceModel.SLUG, slug())
            putIfNotAbsent(cv, RaceModel.STARTTIME, startTime()?.time)
            putIfNotAbsent(cv, RaceModel.TOTALLAPS, totalLaps())
            putIfNotAbsent(cv, RaceModel.UPDATEDAT, updatedAt()?.time)
            putIfNotAbsent(cv, RaceModel.VIDEOURL, videoUrl())
            return cv
        }

    companion object : Creator<Race> by Creator(::AutoValue_Race) {

        const val ALLOWABLE_SECONDS_BEFORE_START_TIME_TO_REGISTER = 5
        const val MINUTES_BEFORE_START_TIME_TO_SHOW_COUNTDOWN = 10 * DateFormatter.millisecondsPerMinute
        const val LOW_REGISTRANTS_NUMBER = 5
        const val FINISHED = 0L
        const val REGISTERED = 1L
        const val REGISTRATION_CLOSED = 2L
        const val RACE_IS_FULL = 3L
        const val HAS_OPEN_SPOTS = 4L
        const val DEFAULT_RACE_NAME = "Racey McRacerson"

        val FACTORY = RaceModel.Factory<Race>(Creator<Race> { id, courseId, deletedAt, endTime, eventId, funQuestion, name, openSpots, raceOrder, replayUrl, shortAnswer1, shortAnswer2, slug, startTime, totalLaps, updatedAt, videoUrl -> create(id, courseId, deletedAt, endTime, eventId, funQuestion, name, openSpots, raceOrder, replayUrl, shortAnswer1, shortAnswer2, slug, startTime, totalLaps, updatedAt, videoUrl) }, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG)
        val RACES_WITH_COURSE_FROM_EVENT_SELECT: RaceModel.RaceWithCourseMapper<Race, Course, RaceWithCourse> = FACTORY.select_races_with_course_from_eventMapper(::AutoValue_Race_RaceWithCourse, Course.FACTORY)
        val RACE_WITH_COURSE_SELECT: RaceModel.RaceWithCourseMapper<Race, Course, RaceWithCourse> = FACTORY.select_race_with_courseMapper(::AutoValue_Race_RaceWithCourse, Course.FACTORY)
        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<Race> = AutoValue_Race.GsonTypeAdapter(gson)
    }

    @AutoValue
    abstract class RaceWithCourse : RaceWithCourseModel<Race, Course> {
        // User is registered for this race
        // Registration is closed
        // Registration is full
        // Not registered for the race
        @RaceStatus
        fun raceStatus(userId: String, timeUntilRace: Long): Long {
            val openSpots = r().openSpots() ?: 0L
            val endTime = r().endTime() ?: Date(Long.MAX_VALUE)
            return when {
                endTime.before(DateFormatter[System.currentTimeMillis()]) -> FINISHED
                registrantIds().contains(userId) -> REGISTERED
                registrationClosed -> REGISTRATION_CLOSED
                openSpots == 0L -> RACE_IS_FULL
                else -> HAS_OPEN_SPOTS
            }
        }

        val registrationClosed: Boolean
            get() = timeUntilRace < Race.ALLOWABLE_SECONDS_BEFORE_START_TIME_TO_REGISTER

        val aboutToStart: Boolean
            get() = timeUntilRace <= Race.MINUTES_BEFORE_START_TIME_TO_SHOW_COUNTDOWN

        val timeUntilRace: Long
            get() = if (r().startTime() == null) 0L else r().startTime()!!.time - System.currentTimeMillis()


        fun hasLowRegistrantCount(): Boolean {
            val openSpots = r().openSpots()
            return openSpots ?: 0L > LOW_REGISTRANTS_NUMBER
        }
    }
}
