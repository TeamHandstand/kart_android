package us.handstand.kartwheel.model


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.support.annotation.IntDef
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite.BriteDatabase
import us.handstand.kartwheel.model.RaceModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent
import us.handstand.kartwheel.model.columnadapter.ColumnAdapters
import us.handstand.kartwheel.util.DateFormatter

@AutoValue
abstract class Race : RaceModel, Comparable<Race> {

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

    // User is registered for this race
    // Registration is closed
    // Registration is full
    // Not registered for the race
    val raceStatus: Long
        @RaceStatus
        get() {
            val openSpots = openSpots()
            if (endTime().before(DateFormatter[System.currentTimeMillis()])) {
                return FINISHED
            } else if (registrantIds() != null && registrantIds()!!.contains(Storage.userId)) {
                return REGISTERED
            } else if (timeUntilRace < Race.ALLOWABLE_SECONDS_BEFORE_START_TIME_TO_REGISTER) {
                return REGISTRATION_CLOSED
            } else if (openSpots ?: 0 == 0L) {
                return RACE_IS_FULL
            } else {
                return HAS_OPEN_SPOTS
            }
        }

    val timeUntilRace: Long
        get() = startTime().time - System.currentTimeMillis()


    fun hasLowRegistrantCount(): Boolean {
        val openSpots = openSpots()
        return openSpots ?: 0L > LOW_REGISTRANTS_NUMBER
    }

    fun insert(db: BriteDatabase?, course: Course?) {
        if (db != null) {
            val cv = contentValues
            cv.put(RaceModel.COURSE, ColumnAdapters.courseToBlob(course))
            db.insert(RaceModel.TABLE_NAME, cv, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    fun insert(db: BriteDatabase?) {
        db?.insert(RaceModel.TABLE_NAME, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, RaceModel.ID, id())
            putIfNotAbsent(cv, RaceModel.COURSE, ColumnAdapters.courseToBlob(course()))
            putIfNotAbsent(cv, RaceModel.COURSEID, courseId())
            putIfNotAbsent(cv, RaceModel.DELETEDAT, deletedAt()?.time)
            putIfNotAbsent(cv, RaceModel.EVENTID, eventId())
            putIfNotAbsent(cv, RaceModel.ENDTIME, endTime().time)
            putIfNotAbsent(cv, RaceModel.FUNQUESTION, funQuestion())
            putIfNotAbsent(cv, RaceModel.NAME, name())
            putIfNotAbsent(cv, RaceModel.OPENSPOTS, openSpots())
            putIfNotAbsent(cv, RaceModel.RACEORDER, raceOrder())
            putIfNotAbsent(cv, RaceModel.REGISTRANTIDS, ColumnAdapters.listStringToBlob(registrantIds()))
            putIfNotAbsent(cv, RaceModel.REPLAYURL, replayUrl())
            putIfNotAbsent(cv, RaceModel.SHORTANSWER1, shortAnswer1())
            putIfNotAbsent(cv, RaceModel.SHORTANSWER2, shortAnswer2())
            putIfNotAbsent(cv, RaceModel.SLUG, slug())
            putIfNotAbsent(cv, RaceModel.STARTTIME, startTime().time)
            putIfNotAbsent(cv, RaceModel.TOTALLAPS, totalLaps())
            putIfNotAbsent(cv, RaceModel.UPDATEDAT, updatedAt()?.time)
            putIfNotAbsent(cv, RaceModel.VIDEOURL, videoUrl())
            return cv
        }

    companion object : Creator<Race> by Creator(::AutoValue_Race) {

        const val ALLOWABLE_SECONDS_BEFORE_START_TIME_TO_REGISTER = 5
        const val LOW_REGISTRANTS_NUMBER = 5
        const val FINISHED = 0L
        const val REGISTERED = 1L
        const val REGISTRATION_CLOSED = 2L
        const val RACE_IS_FULL = 3L
        const val HAS_OPEN_SPOTS = 4L
        const val DEFAULT_RACE_NAME = "Racey McRacerson"

        val FACTORY = RaceModel.Factory<Race>(Creator<Race> { id, course, courseId, deletedAt, eventId, endTime, funQuestion, name, openSpots, raceOrder, registrantIds, registrantImageUrls, replayUrl, shortAnswer1, shortAnswer2, slug, startTime, totalLaps, updatedAt, videoUrl -> create(id, course, courseId, deletedAt, eventId, endTime, funQuestion, name, openSpots, raceOrder, registrantIds, registrantImageUrls, replayUrl, shortAnswer1, shortAnswer2, slug, startTime, totalLaps, updatedAt, videoUrl) }, ColumnAdapters.COURSE_BLOB, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG, ColumnAdapters.LIST_STRING_BLOB, ColumnAdapters.LIST_STRING_BLOB, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG)
        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<Race> {
            return AutoValue_Race.GsonTypeAdapter(gson)
        }
    }
}
