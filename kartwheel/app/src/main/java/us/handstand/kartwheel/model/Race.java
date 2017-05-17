package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.Date;

import us.handstand.kartwheel.model.columnadapter.ColumnAdapters;

import static us.handstand.kartwheel.model.Util.putIfNotAbsent;

@AutoValue
public abstract class Race implements RaceModel {
    public static final RaceModel.Factory<Race> FACTORY = new RaceModel.Factory<>(new Creator<Race>() {
        @Override
        public Race create(@NonNull String id, @Nullable Course course, @Nullable String courseId, @Nullable Date deletedAt, @Nullable String eventId, @Nullable Date endTime, @Nullable String funQuestion, @Nullable String name, @Nullable Long openSpots, @Nullable Long raceOrder, @Nullable String replayUrl, @Nullable String shortAnswer1, @Nullable String shortAnswer2, @Nullable String slug, @Nullable Date startTime, @Nullable Long totalLaps, @Nullable Date updatedAt, @Nullable String videoUrl) {
            return new AutoValue_Race(id, course, courseId, deletedAt, eventId, endTime, funQuestion, name, openSpots, raceOrder, replayUrl, shortAnswer1, shortAnswer2, slug, startTime, totalLaps, updatedAt, videoUrl);
        }
    }, ColumnAdapters.COURSE_BLOB, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG);

    public void update(@Nullable BriteDatabase db, @NonNull Course course) {
        if (db != null) {
            ContentValues cv = new ContentValues();
            cv.put(COURSE, ColumnAdapters.courseToBlob(course));
            db.update(TABLE_NAME, cv, "id=?", id());
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
