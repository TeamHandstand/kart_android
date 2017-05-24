package us.handstand.kartwheel.model


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite.BriteDatabase
import us.handstand.kartwheel.model.UserRaceInfoModel.Creator

import us.handstand.kartwheel.model.Util.putIfNotAbsent

@AutoValue
abstract class UserRaceInfo : UserRaceInfoModel {

    fun insert(db: BriteDatabase?) {
        db?.insert(UserRaceInfoModel.TABLE_NAME, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun delete(db: BriteDatabase?) {
        db?.delete(UserRaceInfoModel.TABLE_NAME, UserRaceInfoModel.ID + "=?", id())
    }

    private val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, UserRaceInfoModel.ID, id())
            putIfNotAbsent(cv, UserRaceInfoModel.CHALLENGEID, challengeId())
            putIfNotAbsent(cv, UserRaceInfoModel.COMPLETIONPERCENT, completionPercent())
            putIfNotAbsent(cv, UserRaceInfoModel.COURSEID, courseId())
            putIfNotAbsent(cv, UserRaceInfoModel.CURRENTLAP, currentLap())
            putIfNotAbsent(cv, UserRaceInfoModel.ENDTIME, endTime())
            putIfNotAbsent(cv, UserRaceInfoModel.FUNANSWERDISPLAYTEXT, funAnswerDisplayText())
            putIfNotAbsent(cv, UserRaceInfoModel.ITEMID, itemId())
            putIfNotAbsent(cv, UserRaceInfoModel.LATITUDE, latitude())
            putIfNotAbsent(cv, UserRaceInfoModel.LONGITUDE, longitude())
            putIfNotAbsent(cv, UserRaceInfoModel.RACEID, raceId())
            putIfNotAbsent(cv, UserRaceInfoModel.STATE, state())
            putIfNotAbsent(cv, UserRaceInfoModel.TARGETED, targeted())
            putIfNotAbsent(cv, UserRaceInfoModel.TARGETEDBY, targetedBy())
            putIfNotAbsent(cv, UserRaceInfoModel.TOTALANTIMILES, totalAntiMiles())
            putIfNotAbsent(cv, UserRaceInfoModel.TOTALMILEAGE, totalMileage())
            putIfNotAbsent(cv, UserRaceInfoModel.TOTALTIME, totalTime())
            putIfNotAbsent(cv, UserRaceInfoModel.USERID, userId())
            putIfNotAbsent(cv, UserRaceInfoModel.UPDATEDAT, updatedAt())
            return cv
        }

    companion object : Creator<UserRaceInfo> by Creator(::AutoValue_UserRaceInfo) {
        val FACTORY = UserRaceInfoModel.Factory<UserRaceInfo>(Creator<UserRaceInfo> { id, challengeId, completionPercent, courseId, currentLap, endTime, funAnswerDisplayText, itemId, latitude, longitude, raceId, ranking, removedAt, state, targeted, targetedBy, totalAntiMiles, totalMileage, totalTime, userId, updatedAt -> create(id, challengeId, completionPercent, courseId, currentLap, endTime, funAnswerDisplayText, itemId, latitude, longitude, raceId, ranking, removedAt, state, targeted, targetedBy, totalAntiMiles, totalMileage, totalTime, userId, updatedAt) })
        // Needed by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<UserRaceInfo> {
            return AutoValue_UserRaceInfo.GsonTypeAdapter(gson)
        }
    }
}
