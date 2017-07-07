package us.handstand.kartwheel.model

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.squareup.sqlbrite.BriteDatabase
import java.util.concurrent.CountDownLatch


interface Insertable {
    fun insertOrUpdate(db: BriteDatabase?, cv: ContentValues = contentValues) {
        if (db != null) {
            cv.remove("id")
            val wasUpdated = db.update(tableName(), cv, "id = ?", id()) > 0
            if (!wasUpdated) {
                cv.put("id", id())
                db.insert(tableName(), cv, SQLiteDatabase.CONFLICT_REPLACE)
            }
        }
    }

    fun insert(db: BriteDatabase?) {
        db?.insert(tableName(), contentValues, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun update(db: BriteDatabase?, channel: String = "", latch: CountDownLatch? = null) {
        if (db != null) {
            val cv = contentValues
            cv.remove("id")
            db.update(tableName(), cv, "id = ?", id())
        }
        latch?.countDown()
    }

    fun tableName(): String
    fun id(): String

    val contentValues: ContentValues
}