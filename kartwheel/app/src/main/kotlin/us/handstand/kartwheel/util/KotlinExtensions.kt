package us.handstand.kartwheel.util

import android.database.Cursor
import com.squareup.sqldelight.RowMapper


fun <T> Cursor.mapToList(mapper: RowMapper<T>): List<T> {
    use {
        val items: List<T>
        items = ArrayList<T>(count)
        while (moveToNext()) {
            items.add(mapper.map(this))
        }
        return items
    }
}