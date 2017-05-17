package us.handstand.kartwheel.model.columnadapter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.sqldelight.ColumnAdapter;

import java.util.Date;

import us.handstand.kartwheel.model.Course;
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

    @Nullable
    public static byte[] courseToBlob(Course course) {
        return course == null ? null : API.INSTANCE.getGson().toJson(course).getBytes();
    }

    @Nullable
    public static Course blobToCourse(byte[] blob) {
        return blob == null ? null : API.INSTANCE.getGson().fromJson(new String(blob), Course.class);
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
}
