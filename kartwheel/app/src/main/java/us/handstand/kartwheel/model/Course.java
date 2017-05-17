package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqlbrite.BriteDatabase;

import static us.handstand.kartwheel.model.Util.putIfNotAbsent;

@AutoValue
public abstract class Course implements CourseModel {

    public static final CourseModel.Factory<Course> FACTORY = new CourseModel.Factory<>(new Creator<Course>() {
        @Override
        public Course create(@NonNull String id, @Nullable String createdAt, @Nullable String deletedAt, @Nullable Double distance, @Nullable Long maxRegistrants, @Nullable String name, @Nullable Double startLat, @Nullable Double startLong, @Nullable String updatedAt) {
            return new AutoValue_Course(id, createdAt, deletedAt, distance, maxRegistrants, name, startLat, startLong, updatedAt);
        }
    });

    public void insert(@Nullable BriteDatabase db) {
        if (db != null) {
            ContentValues cv = new ContentValues();
            putIfNotAbsent(cv, ID, id());
            putIfNotAbsent(cv, CREATEDAT, createdAt());
            putIfNotAbsent(cv, DELETEDAT, deletedAt());
            putIfNotAbsent(cv, DISTANCE, distance());
            putIfNotAbsent(cv, MAXREGISTRANTS, maxRegistrants());
            putIfNotAbsent(cv, NAME, name());
            putIfNotAbsent(cv, STARTLAT, startLat());
            putIfNotAbsent(cv, STARTLONG, startLong());
            putIfNotAbsent(cv, UPDATEDAT, updatedAt());
            db.insert(TABLE_NAME, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Course> typeAdapter(Gson gson) {
        return new AutoValue_Course.GsonTypeAdapter(gson);
    }
}
