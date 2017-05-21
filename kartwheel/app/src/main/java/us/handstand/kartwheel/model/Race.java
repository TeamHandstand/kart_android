package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.Date;
import java.util.List;

import us.handstand.kartwheel.model.columnadapter.ColumnAdapters;

import static us.handstand.kartwheel.model.Util.putIfNotAbsent;

@AutoValue
public abstract class Race implements RaceModel, Comparable<Race> {
    public static final RaceModel.Factory<Race> FACTORY = new RaceModel.Factory<>(new Creator<Race>() {
        @Override
        public Race create(@NonNull String id, @Nullable Course course, @Nullable String courseId, @Nullable Date deletedAt, @Nullable String eventId, @Nullable Date endTime, @Nullable String funQuestion, @Nullable String name, @Nullable Long openSpots, @Nullable Long raceOrder, @Nullable List<String> registrantIds, @Nullable List<String> registrantImageUrls, @Nullable String replayUrl, @Nullable String shortAnswer1, @Nullable String shortAnswer2, @Nullable String slug, @Nullable Date startTime, @Nullable Long totalLaps, @Nullable Date updatedAt, @Nullable String videoUrl) {
            return new AutoValue_Race(id, course, courseId, deletedAt, eventId, endTime, funQuestion, name, openSpots, raceOrder, registrantIds, registrantImageUrls, replayUrl, shortAnswer1, shortAnswer2, slug, startTime, totalLaps, updatedAt, videoUrl);
        }
    }, ColumnAdapters.COURSE_BLOB, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG, ColumnAdapters.LIST_BLOB, ColumnAdapters.LIST_BLOB, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG);

    public static final int ALLOWABLE_SECONDS_BEFORE_START_TIME_TO_REGISTER = 5;
    public static final int LOW_REGISTRANTS_NUMBER = 5;
    public static final int FINISHED = 0;
    public static final int REGISTERED = 1;
    public static final int REGISTRATION_CLOSED = 2;
    public static final int RACE_IS_FULL = 3;
    public static final int HAS_OPEN_SPOTS = 4;
    public static final String DEFAULT_RACE_NAME = "Racey McRacerson";

    @Override
    public int compareTo(@NonNull Race o) {
        Long myRaceOrder = raceOrder();
        Long theirRaceOrder = o.raceOrder();
        if (myRaceOrder == null) {
            return 1;
        } else if (theirRaceOrder == null) {
            return -1;
        } else {
            return myRaceOrder.compareTo(theirRaceOrder);
        }
    }

    @IntDef({FINISHED, REGISTERED, REGISTRATION_CLOSED, RACE_IS_FULL, HAS_OPEN_SPOTS})
    public @interface RaceStatus {
    }

    public Race() {

    }

    public boolean alreadyStarted() {
        Date startTime = startTime();
        return startTime == null || startTime.getTime() - System.currentTimeMillis() < 0;
    }

    @RaceStatus
    public int getRaceStatus() {
        final Long openSpots = openSpots();
        if (endTime() != null) {
            return FINISHED;
            // User is registered for this race
        } else if (registrantIds() != null && registrantIds().contains(Storage.Companion.getUserId())) {
            return REGISTERED;
            // Registration is closed
        } else if (getTimeUntilRace() < Race.ALLOWABLE_SECONDS_BEFORE_START_TIME_TO_REGISTER) {
            return REGISTRATION_CLOSED;
            // Registration is full
        } else if ((openSpots == null ? 0 : openSpots) == 0L) {
            return RACE_IS_FULL;
            // Not registered for the race
        } else {
            return HAS_OPEN_SPOTS;
        }
    }

    @NonNull
    public Long getTimeUntilRace() {
        final Date startTime = startTime();
        final long startTimeMs = startTime == null ? 0L : startTime.getTime();
        final long currentTimeMs = System.currentTimeMillis();
        return startTimeMs - currentTimeMs;
    }

    public boolean hasLowRegistrantCount() {
        final Long openSpots = openSpots();
        return (openSpots == null ? 0L : openSpots) > LOW_REGISTRANTS_NUMBER;
    }

    public void insert(@Nullable BriteDatabase db, Course course) {
        if (db != null) {
            ContentValues cv = getContentValues();
            cv.put(COURSE, ColumnAdapters.courseToBlob(course));
            db.insert(TABLE_NAME, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public void insert(@Nullable BriteDatabase db) {
        if (db != null) {
            db.insert(TABLE_NAME, getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    private ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        putIfNotAbsent(cv, ID, id());
        putIfNotAbsent(cv, COURSE, ColumnAdapters.courseToBlob(course()));
        putIfNotAbsent(cv, COURSEID, courseId());
        putIfNotAbsent(cv, DELETEDAT, ColumnAdapters.dateToLong(deletedAt()));
        putIfNotAbsent(cv, EVENTID, eventId());
        putIfNotAbsent(cv, ENDTIME, ColumnAdapters.dateToLong(endTime()));
        putIfNotAbsent(cv, FUNQUESTION, funQuestion());
        putIfNotAbsent(cv, NAME, name());
        putIfNotAbsent(cv, OPENSPOTS, openSpots());
        putIfNotAbsent(cv, RACEORDER, raceOrder());
        putIfNotAbsent(cv, REGISTRANTIDS, ColumnAdapters.listToBlob(registrantIds()));
        putIfNotAbsent(cv, REPLAYURL, replayUrl());
        putIfNotAbsent(cv, SHORTANSWER1, shortAnswer1());
        putIfNotAbsent(cv, SHORTANSWER2, shortAnswer2());
        putIfNotAbsent(cv, SLUG, slug());
        putIfNotAbsent(cv, STARTTIME, ColumnAdapters.dateToLong(startTime()));
        putIfNotAbsent(cv, TOTALLAPS, totalLaps());
        putIfNotAbsent(cv, UPDATEDAT, ColumnAdapters.dateToLong(updatedAt()));
        putIfNotAbsent(cv, VIDEOURL, videoUrl());
        return cv;
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Race> typeAdapter(Gson gson) {
        return new AutoValue_Race.GsonTypeAdapter(gson);
    }
}
