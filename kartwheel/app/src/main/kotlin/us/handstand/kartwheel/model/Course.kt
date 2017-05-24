package us.handstand.kartwheel.model


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite.BriteDatabase
import us.handstand.kartwheel.model.CourseModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent
import us.handstand.kartwheel.model.columnadapter.ColumnAdapters
import java.util.*

@AutoValue
abstract class Course : CourseModel {

    data class CourseBounds(val lowLat: Double, val lowLong: Double, val highLat: Double, val highLong: Double) {
        val centerLat: Double = (lowLat + highLat) / 2
        val centerLong: Double = (lowLong + highLong) / 2
    }

    fun findCorners(): CourseBounds {
        var first = true
        var lowLat = 0.0
        var lowLong = 0.0
        var highLat = 0.0
        var highLong = 0.0
        Collections.sort(vertices()!!)
        for (point in vertices()!!) {
            if (first) {
                lowLat = point.latitude()
                highLat = point.latitude()
                lowLong = point.longitude()
                highLong = point.longitude()
            }
            if (point.latitude() < lowLat) {
                lowLat = point.latitude()
            }
            if (point.latitude() > highLat) {
                highLat = point.latitude()
            }

            if (point.longitude() < lowLong) {
                lowLong = point.longitude()
            }
            if (point.longitude() > highLong) {
                highLong = point.longitude()
            }
            first = false
        }
        return CourseBounds(lowLat, lowLong, highLat, highLong)
    }

    fun insert(db: BriteDatabase?) {
        if (db != null) {
            val cv = ContentValues()
            putIfNotAbsent(cv, CourseModel.ID, id())
            putIfNotAbsent(cv, CourseModel.CREATEDAT, createdAt())
            putIfNotAbsent(cv, CourseModel.DELETEDAT, deletedAt())
            putIfNotAbsent(cv, CourseModel.DISTANCE, distance())
            putIfNotAbsent(cv, CourseModel.MAXREGISTRANTS, maxRegistrants())
            putIfNotAbsent(cv, CourseModel.NAME, name())
            putIfNotAbsent(cv, CourseModel.STARTLAT, startLat())
            putIfNotAbsent(cv, CourseModel.STARTLONG, startLong())
            putIfNotAbsent(cv, CourseModel.UPDATEDAT, updatedAt())
            db.insert(CourseModel.TABLE_NAME, cv, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    companion object : Creator<Course> by Creator(::AutoValue_Course) {
        val EMPTY_COURSE: Course = AutoValue_Course("", "", "", 0.0, 0L, "", 0.0, 0.0, "", null)

        val FACTORY = CourseModel.Factory<Course>(CourseModel.Creator<Course> { id, createdAt, deletedAt, distance, maxRegistrants, name, startLat, startLong, updatedAt, vertices -> create(id, createdAt, deletedAt, distance, maxRegistrants, name, startLat, startLong, updatedAt, vertices) }, ColumnAdapters.LIST_POINT_BLOB)

        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<Course> {
            return AutoValue_Course.GsonTypeAdapter(gson)
        }
    }
}
