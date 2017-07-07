package us.handstand.kartwheel.model


import com.google.gson.reflect.TypeToken
import com.squareup.sqldelight.ColumnAdapter
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.DateFormatter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

object ColumnAdapters {
    val COURSE_BLOB: ColumnAdapter<Course, ByteArray> = object : ColumnAdapter<Course, ByteArray> {
        override fun decode(databaseValue: ByteArray): Course {
            return blobToCourse(databaseValue)
        }

        override fun encode(value: Course): ByteArray {
            return courseToBlob(value)
        }
    }

    fun courseToBlob(course: Course?): ByteArray {
        if (course == null) {
            return ByteArray(0)
        } else {
            return API.gson.toJson(course).toByteArray()
        }
    }

    fun blobToCourse(blob: ByteArray?): Course {
        if (blob == null || blob.isEmpty()) {
            return Course.EMPTY_COURSE
        } else {
            return API.gson.fromJson(String(blob), Course::class.java)
        }
    }

    val DATE_LONG: ColumnAdapter<Date, Long> = object : ColumnAdapter<Date, Long> {
        override fun decode(databaseValue: Long?): Date {
            return if (databaseValue != null) DateFormatter[databaseValue] ?: Date() else Date()
        }

        override fun encode(value: Date): Long {
            return value?.time ?: 0L
        }
    }

    val LIST_STRING_BLOB: ColumnAdapter<List<String>, ByteArray> = object : ColumnAdapter<List<String>, ByteArray> {
        override fun decode(databaseValue: ByteArray): List<String> {
            return blobToListString(databaseValue)
        }

        override fun encode(value: List<String>): ByteArray {
            return listStringToBlob(value)
        }
    }

    private val listStringType = object : TypeToken<List<String>>() {

    }.type

    fun blobToListString(blob: ByteArray?): List<String> {
        if (blob == null || blob.isEmpty()) {
            return emptyList()
        } else {
            return API.gson.fromJson<List<String>>(String(blob), listStringType)
        }
    }

    fun listStringToBlob(list: List<String>?): ByteArray {
        if (list == null) {
            return ByteArray(0)
        } else {
            return API.gson.toJson(list).toByteArray()
        }
    }

    val LIST_POINT_BLOB: ColumnAdapter<List<Point>, ByteArray> = object : ColumnAdapter<List<Point>, ByteArray> {
        override fun decode(databaseValue: ByteArray): List<Point> {
            return blobToListPoint(databaseValue)
        }

        override fun encode(value: List<Point>): ByteArray {
            return listPointToBlob(value)
        }
    }

    private val listPointType = object : TypeToken<List<Point>>() {

    }.type

    fun blobToListPoint(blob: ByteArray?): List<Point> {
        if (blob == null || blob.isEmpty()) {
            return emptyList()
        } else {
            return API.gson.fromJson<List<Point>>(String(blob), listPointType)
        }
    }

    fun listPointToBlob(list: List<Point>?): ByteArray {
        if (list == null) {
            return ByteArray(0)
        } else {
            return API.gson.toJson(list).toByteArray()
        }
    }

    class ListTicketColumnAdapter<T> : ColumnAdapter<List<T>, ByteArray> {
        override fun decode(databaseValue: ByteArray): List<T> {
            var tickets = emptyList<T>()
            try {
                val `in` = ByteArrayInputStream(databaseValue)
                val `is` = ObjectInputStream(`in`)
                tickets = `is`.readObject() as List<T>
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return tickets
        }

        override fun encode(value: List<T>): ByteArray {
            var bytes = ByteArray(0)
            try {
                val out = ByteArrayOutputStream()
                val os = ObjectOutputStream(out)
                os.writeObject(value)
                bytes = out.toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return bytes
        }
    }
}
