package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.Collections;
import java.util.List;

import us.handstand.kartwheel.model.columnadapter.ColumnAdapters;

import static us.handstand.kartwheel.model.Util.putIfNotAbsent;

@AutoValue
public abstract class Course implements CourseModel {

    public static final CourseModel.Factory<Course> FACTORY = new CourseModel.Factory<>(new Creator<Course>() {
        @Override
        public Course create(@NonNull String id, @Nullable String createdAt, @Nullable String deletedAt, double distance, @Nullable Long maxRegistrants, @Nullable String name, @Nullable Double startLat, @Nullable Double startLong, @Nullable String updatedAt, @Nullable List<Point> vertices) {
            return new AutoValue_Course(id, createdAt, deletedAt, distance, maxRegistrants, name, startLat, startLong, updatedAt, vertices);
        }
    }, ColumnAdapters.LIST_POINT_BLOB);

    public static final Course EMPTY_COURSE = new AutoValue_Course("", "", "", 0.0, 0L, "", 0.0, 0.0, "", null);

    public static class CourseBounds {
        public final double lowLat;
        public final double lowLong;
        public final double highLat;
        public final double highLong;
        public final double centerLat;
        public final double centerLong;

        public CourseBounds(double lowLat, double lowLong, double highLat, double highLong) {
            this.lowLat = lowLat;
            this.lowLong = lowLong;
            this.highLat = highLat;
            this.highLong = highLong;
            this.centerLat = (lowLat + highLat) / 2;
            this.centerLong = (lowLong + highLong) / 2;
        }
    }

    public CourseBounds findCorners() {
        boolean first = true;
        double lowLat = 0;
        double lowLong = 0;
        double highLat = 0;
        double highLong = 0;
        Collections.sort(vertices());
        for (Point point : vertices()) {
            if (first) {
                lowLat = point.latitude();
                highLat = point.latitude();
                lowLong = point.longitude();
                highLong = point.longitude();
            }
            if (point.latitude() < lowLat) {
                lowLat = point.latitude();
            }
            if (point.latitude() > highLat) {
                highLat = point.latitude();
            }

            if (point.longitude() < lowLong) {
                lowLong = point.longitude();
            }
            if (point.longitude() > highLong) {
                highLong = point.longitude();
            }
            first = false;
        }
        return new CourseBounds(lowLat, lowLong, highLat, highLong);
    }

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
