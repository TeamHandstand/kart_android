package us.handstand.kartwheel.model

import android.content.ContentValues
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import us.handstand.kartwheel.model.RaceLogEventModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent


@AutoValue
abstract class RaceLogEvent : RaceLogEventModel, Insertable {

    override fun tableName(): String {
        return RaceLogEventModel.TABLE_NAME
    }

    override val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, RaceLogEventModel.ID, id())
            putIfNotAbsent(cv, RaceLogEventModel.ATTACKERID, attackerId())
            putIfNotAbsent(cv, RaceLogEventModel.CHALLENGESLUG, challengeSlug())
            putIfNotAbsent(cv, RaceLogEventModel.DISPLAYABLE, displayable())
            putIfNotAbsent(cv, RaceLogEventModel.DISTANCE, distance())
            putIfNotAbsent(cv, RaceLogEventModel.EVENTTYPE, eventType())
            putIfNotAbsent(cv, RaceLogEventModel.ITEMNAME, itemName())
            putIfNotAbsent(cv, RaceLogEventModel.ITEMID, itemId())
            putIfNotAbsent(cv, RaceLogEventModel.ITEMSLUG, itemSlug())
            putIfNotAbsent(cv, RaceLogEventModel.LAPNUMBER, lapNumber())
            putIfNotAbsent(cv, RaceLogEventModel.POINTID, pointId())
            putIfNotAbsent(cv, RaceLogEventModel.RACEID, raceId())
            putIfNotAbsent(cv, RaceLogEventModel.RANKING, ranking())
            putIfNotAbsent(cv, RaceLogEventModel.RELATIVETIME, relativeTime())
            putIfNotAbsent(cv, RaceLogEventModel.RESPONSE, response())
            putIfNotAbsent(cv, RaceLogEventModel.TARGETID, targetId())
            putIfNotAbsent(cv, RaceLogEventModel.TIME, time().time)
            putIfNotAbsent(cv, RaceLogEventModel.TOTALTIME, totalTime()?.time)
            putIfNotAbsent(cv, RaceLogEventModel.USERID, userId())
            return cv
        }

    companion object : Creator<RaceLogEvent> by Creator(::AutoValue_RaceLogEvent) {
        val FACTORY = RaceLogEventModel.Factory<RaceLogEvent>(Creator { id, attackerId, challengeSlug, displayable, distance, eventType, itemName, itemId, itemSlug, lapNumber, pointId, raceId, ranking, relativeTime, response, targetId, time, totalTime, userId -> create(id, attackerId, challengeSlug, displayable, distance, eventType, itemName, itemId, itemSlug, lapNumber, pointId, raceId, ranking, relativeTime, response, targetId, time, totalTime, userId) }, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG)
        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<RaceLogEvent> {
            return AutoValue_RaceLogEvent.GsonTypeAdapter(gson)
        }
    }
}