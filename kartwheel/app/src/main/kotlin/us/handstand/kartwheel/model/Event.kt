package us.handstand.kartwheel.model


import android.content.ContentValues
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import us.handstand.kartwheel.model.EventModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent

@AutoValue
abstract class Event : EventModel, Insertable {

    override fun tableName(): String {
        return EventModel.TABLE_NAME
    }

    override val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, EventModel.ID, id())
            putIfNotAbsent(cv, EventModel.ENDTIME, endTime().time)
            putIfNotAbsent(cv, EventModel.NAME, name())
            putIfNotAbsent(cv, EventModel.STARTTIME, startTime().time)
            putIfNotAbsent(cv, EventModel.UPDATEDAT, updatedAt()?.time)
            putIfNotAbsent(cv, EventModel.USERSCANSEERACES, usersCanSeeRaces())
            return cv
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
