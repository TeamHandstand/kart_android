package us.handstand.kartwheel.model

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite.BriteDatabase
import us.handstand.kartwheel.model.RaceEventModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent


@AutoValue
abstract class RaceEvent : RaceEventModel, Insertable {

    override fun tableName(): String {
        return RaceEventModel.TABLE_NAME
    }

    override val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, RaceEventModel.ID, id())
            putIfNotAbsent(cv, RaceEventModel.MESSAGE, message())
            putIfNotAbsent(cv, RaceEventModel.SOUNDNAME, soundName())
            putIfNotAbsent(cv, RaceEventModel.TYPE, type())
            putIfNotAbsent(cv, RaceEventModel.VIBRATE, vibrate())
            return cv
        }

    companion object : Creator<RaceEvent> by Creator(::AutoValue_RaceEvent) {
        val FACTORY = RaceEventModel.Factory<RaceEvent>(Creator { id, message, soundName, type, vibrate -> create(id, message, soundName, type, vibrate) })
        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<RaceEvent> {
            return AutoValue_RaceEvent.GsonTypeAdapter(gson)
        }
    }
}