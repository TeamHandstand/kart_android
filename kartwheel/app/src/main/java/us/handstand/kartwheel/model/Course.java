package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Course implements CourseModel {
    public static final CourseModel.Factory<Course> FACTORY = new CourseModel.Factory<>(new Creator<Course>() {
        @Override
        public Course create(@NonNull String id, @Nullable Double distance, @Nullable Long maxRegistrants, @Nullable String name, @Nullable Double startLat, @Nullable Double startLong, @Nullable String updatedAt) {
            return new AutoValue_Course(id, distance, maxRegistrants, name, startLat, startLong, updatedAt);
        }
    });
    public static final RowMapper<Course> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    public void insert(@Nullable BriteDatabase db) {
        if (db == null) {
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(ID, id());
        cv.put(DISTANCE, distance());
        cv.put(MAXREGISTRANTS, maxRegistrants());
        cv.put(NAME, name());
        cv.put(STARTLAT, startLat());
        cv.put(STARTLONG, startLong());
        cv.put(STARTLONG, startLong());
        cv.put(UPDATEDAT, updatedAt());
        db.insert(TABLE_NAME, cv);
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Course> typeAdapter(Gson gson) {
        return new AutoValue_Course.GsonTypeAdapter(gson);
    }
}
