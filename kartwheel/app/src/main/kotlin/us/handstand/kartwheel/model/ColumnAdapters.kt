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
    val DATE_LONG: ColumnAdapter<Date, Long> = object : ColumnAdapter<Date, Long> {
        override fun decode(databaseValue: Long?): Date =
                if (databaseValue != null) DateFormatter[databaseValue] ?: Date() else Date()

        override fun encode(value: Date): Long {
            @Suppress("UNNECESSARY_SAFE_CALL", "USELESS_ELVIS")
            return value?.time ?: 0L
        }
    }

    val LIST_POINT_BLOB: ColumnAdapter<List<Point>, ByteArray> = object : ColumnAdapter<List<Point>, ByteArray> {
        override fun decode(databaseValue: ByteArray): List<Point> = blobToListPoint(databaseValue)

        override fun encode(value: List<Point>): ByteArray = listPointToBlob(value)
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
                @Suppress("UNCHECKED_CAST")
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
