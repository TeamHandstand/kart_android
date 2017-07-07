package us.handstand.kartwheel.model


import android.content.ContentValues
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite.BriteDatabase
import us.handstand.kartwheel.model.UserRaceInfoModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.notifications.PubNubManager
import java.util.concurrent.CountDownLatch

@AutoValue
abstract class UserRaceInfo : UserRaceInfoModel, Insertable {
    override fun tableName(): String {
        return UserRaceInfoModel.TABLE_NAME
    }

    fun delete(db: BriteDatabase?) {
        db?.delete(UserRaceInfoModel.TABLE_NAME, UserRaceInfoModel.ID + "=?", id())
    }

    override fun update(db: BriteDatabase?, channel: String, latch: CountDownLatch?) {
        if (db != null) {
            if (deletedAt() != null) {
                // Remove UserRaceInfo
                val userRaceInfo = UserRaceInfo.remove(db, id())
                // Drop User from race
                User.updateRaceId(db, userRaceInfo?.userId(), null)
                latch?.countDown()
            } else if (userId() != null) {
                // Find the User belonging to this UserRaceInfo
                val userStatement = User.FACTORY.select_for_id(userId()!!)
                db.query(userStatement.statement, *userStatement.args).use {
                    val userFound = it.moveToFirst()
                    if (userFound) {
                        updateInfoAndUser(db, channel, latch, User.FACTORY.select_for_idMapper().map(it).id())
                    } else {
                        API.getUser(userId()!!, false) { updateInfoAndUser(db, channel, latch) }
                    }
                }
            } else {
                updateInfoAndUser(db, channel, latch)
            }
        }
    }

    private fun updateInfoAndUser(db: BriteDatabase?, channel: String? = null, latch: CountDownLatch? = null, userId: String? = null) {
        val cv = contentValues
        putIfNotAbsent(cv, UserRaceInfoModel.USERID, userId ?: userId())
        putIfNotAbsent(cv, UserRaceInfoModel.RACEID, PubNubManager.idFromChannel(channel))
        insertOrUpdate(db, cv)
        User.updateRaceId(db, userId ?: userId(), PubNubManager.idFromChannel(channel))
        latch?.countDown()
    }

    override val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, UserRaceInfoModel.ID, id())
            putIfNotAbsent(cv, UserRaceInfoModel.CHALLENGEID, challengeId())
            putIfNotAbsent(cv, UserRaceInfoModel.COMPLETIONPERCENT, completionPercent())
            putIfNotAbsent(cv, UserRaceInfoModel.COURSEID, courseId())
            putIfNotAbsent(cv, UserRaceInfoModel.CURRENTLAP, currentLap())
            putIfNotAbsent(cv, UserRaceInfoModel.ENDTIME, endTime())
            putIfNotAbsent(cv, UserRaceInfoModel.DELETEDAT, deletedAt()?.time)
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
            putIfNotAbsent(cv, UserRaceInfoModel.UPDATEDAT, updatedAt()?.time)
            return cv
        }

    companion object : Creator<UserRaceInfo> by Creator(::AutoValue_UserRaceInfo) {
        val FACTORY = UserRaceInfoModel.Factory<UserRaceInfo>(Creator<UserRaceInfo> { id, challengeId, completionPercent, courseId, currentLap, deletedAt, endTime, funAnswerDisplayText, itemId, latitude, longitude, raceId, ranking, state, targeted, targetedBy, totalAntiMiles, totalMileage, totalTime, userId, updatedAt -> create(id, challengeId, completionPercent, courseId, currentLap, deletedAt, endTime, funAnswerDisplayText, itemId, latitude, longitude, raceId, ranking, state, targeted, targetedBy, totalAntiMiles, totalMileage, totalTime, userId, updatedAt) }, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG)

        fun remove(db: BriteDatabase?, id: String): UserRaceInfo? {
            val userRaceInfoStatement = UserRaceInfo.FACTORY.select_for_id(id)
            var userRaceInfo: UserRaceInfo? = null
            db?.query(userRaceInfoStatement.statement, *userRaceInfoStatement.args)?.use {
                if (it.moveToFirst()) {
                    userRaceInfo = UserRaceInfo.FACTORY.select_for_idMapper().map(it)
                    userRaceInfo?.delete(db)
                }
            }
            return userRaceInfo
        }

        // Needed by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<UserRaceInfo> {
            return AutoValue_UserRaceInfo.GsonTypeAdapter(gson)
        }
    }
}
