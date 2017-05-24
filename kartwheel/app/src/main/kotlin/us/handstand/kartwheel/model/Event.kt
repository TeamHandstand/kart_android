package us.handstand.kartwheel.model


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite.BriteDatabase
import us.handstand.kartwheel.model.EventModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent
import us.handstand.kartwheel.model.columnadapter.ColumnAdapters

@AutoValue
abstract class Event : EventModel {

    fun insert(db: BriteDatabase?) {
        if (db != null) {
            val cv = ContentValues()
            putIfNotAbsent(cv, EventModel.ID, id())
            putIfNotAbsent(cv, EventModel.ENDTIME, endTime().time)
            putIfNotAbsent(cv, EventModel.NAME, name())
            putIfNotAbsent(cv, EventModel.STARTTIME, startTime().time)
            putIfNotAbsent(cv, EventModel.UPDATEDAT, updatedAt().time)
            putIfNotAbsent(cv, EventModel.USERSCANSEERACES, usersCanSeeRaces())
            db.insert(EventModel.TABLE_NAME, cv, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    companion object : Creator<Event> by Creator(::AutoValue_Event) {
        val FACTORY = EventModel.Factory<Event>(Creator<Event> { id, endTime, name, startTime, updatedAt, usersCanSeeRaces -> create(id, endTime, name, startTime, updatedAt, usersCanSeeRaces) }, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG)
        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<Event> {
            return AutoValue_Event.GsonTypeAdapter(gson)
        }
    }
}
