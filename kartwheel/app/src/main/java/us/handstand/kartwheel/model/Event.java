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
public abstract class Event implements EventModel {

    public static final Factory<Event> FACTORY = new Factory<>(new Creator<Event>() {
        @Override
        public Event create(@NonNull String id, @NonNull Date endTime, @NonNull String name, @NonNull Date startTime, @NonNull String updatedAt, @Nullable Boolean usersCanSeeRaces) {
            return new AutoValue_Event(id, endTime, name, startTime, updatedAt, usersCanSeeRaces);
        }
    }, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG);

    public void insert(@Nullable BriteDatabase db) {
        if (db != null) {
            ContentValues cv = new ContentValues();
            putIfNotAbsent(cv, ID, id());
            putIfNotAbsent(cv, ENDTIME, endTime().getTime());
            putIfNotAbsent(cv, NAME, name());
            putIfNotAbsent(cv, STARTTIME, startTime().getTime());
            putIfNotAbsent(cv, UPDATEDAT, updatedAt());
            putIfNotAbsent(cv, USERSCANSEERACES, usersCanSeeRaces());
            db.insert(TABLE_NAME, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Event> typeAdapter(Gson gson) {
        return new AutoValue_Event.GsonTypeAdapter(gson);
    }
}
