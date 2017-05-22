package us.handstand.kartwheel.model.columnadapter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.squareup.sqldelight.ColumnAdapter;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import us.handstand.kartwheel.model.Course;
import us.handstand.kartwheel.model.Point;
import us.handstand.kartwheel.network.API;
import us.handstand.kartwheel.util.DateFormatter;

public class ColumnAdapters {
    public static final ColumnAdapter<Course, byte[]> COURSE_BLOB = new ColumnAdapter<Course, byte[]>() {
        @NonNull
        @Override
        public Course decode(byte[] databaseValue) {
            return blobToCourse(databaseValue);
        }

        @Override
        public byte[] encode(@NonNull Course value) {
            return courseToBlob(value);
        }
    };

    @NonNull
    public static byte[] courseToBlob(Course course) {
        if (course == null) {
            return new byte[0];
        } else {
            return API.INSTANCE.getGson().toJson(course).getBytes();
        }
    }

    @NonNull
    public static Course blobToCourse(byte[] blob) {
        if (blob == null || blob.length == 0) {
            return Course.EMPTY_COURSE;
        } else {
            return API.INSTANCE.getGson().fromJson(new String(blob), Course.class);
        }
    }

    public static final ColumnAdapter<Date, Long> DATE_LONG = new ColumnAdapter<Date, Long>() {
        @NonNull
        @Override
        public Date decode(Long databaseValue) {
            Date result = longToDate(databaseValue);
            return result == null ? new Date() : result;
        }

        @Override
        public Long encode(@NonNull Date value) {
            Long result = dateToLong(value);
            return result == null ? 0L : result;
        }
    };

    @Nullable
    public static Date longToDate(Long value) {
        return value == null ? null : DateFormatter.INSTANCE.get(value);
    }

    @Nullable
    public static Long dateToLong(Date date) {
        return date == null ? null : date.getTime();
    }

    public static final ColumnAdapter<List<String>, byte[]> LIST_STRING_BLOB = new ColumnAdapter<List<String>, byte[]>() {
        @NonNull
        @Override
        public List<String> decode(byte[] databaseValue) {
            return blobToListString(databaseValue);
        }

        @Override
        public byte[] encode(@NonNull List<String> value) {
            return listStringToBlob(value);
        }
    };

    private static final Type listStringType = new TypeToken<List<String>>() {
    }.getType();

    @NonNull
    public static List<String> blobToListString(byte[] blob) {
        if (blob == null || blob.length == 0) {
            return Collections.emptyList();
        } else {
            return API.INSTANCE.getGson().fromJson(new String(blob), listStringType);
        }
    }

    @NonNull
    public static byte[] listStringToBlob(List<String> list) {
        if (list == null) {
            return new byte[0];
        } else {
            return API.INSTANCE.getGson().toJson(list).getBytes();
        }
    }

    public static final ColumnAdapter<List<Point>, byte[]> LIST_POINT_BLOB = new ColumnAdapter<List<Point>, byte[]>() {
        @NonNull
        @Override
        public List<Point> decode(byte[] databaseValue) {
            return blobToListPoint(databaseValue);
        }

        @Override
        public byte[] encode(@NonNull List<Point> value) {
            return listPointToBlob(value);
        }
    };

    private static final Type listPointType = new TypeToken<List<Point>>() {
    }.getType();

    @NonNull
    public static List<Point> blobToListPoint(byte[] blob) {
        if (blob == null || blob.length == 0) {
            return Collections.emptyList();
        } else {
            return API.INSTANCE.getGson().fromJson(new String(blob), listPointType);
        }
    }

    @NonNull
    public static byte[] listPointToBlob(List<Point> list) {
        if (list == null) {
            return new byte[0];
        } else {
            return API.INSTANCE.getGson().toJson(list).getBytes();
        }
    }
}
