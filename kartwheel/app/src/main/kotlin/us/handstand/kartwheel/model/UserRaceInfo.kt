package us.handstand.kartwheel.model


import android.content.ContentValues
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite2.BriteDatabase
import us.handstand.kartwheel.model.UserRaceInfoModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent
import us.handstand.kartwheel.network.API
import java.util.concurrent.CountDownLatch

@AutoValue
abstract class UserRaceInfo : UserRaceInfoModel, Insertable {
    // TODO: Map the state property to the corresponding UserState
    enum class UserState {
        DISCONNECTED, // Dropped from the race
        ATTACHED, // Standard racing state
        DETACHED, // No buddy
        INJURED // Hit
    }

    override fun tableName(): String {
        return UserRaceInfoModel.TABLE_NAME
    }

    override fun update(db: BriteDatabase?, channel: String, latch: CountDownLatch?) {
        if (db == null) {
            return
        }
        if (deletedAt() != null) {
            // TODO: Should this be done for every delete?
            db.newTransaction().use {
                // Drop User from race
                CompiledStatements.UserRaceInfo.removeUserFromRace.bind(id())
                CompiledStatements.execute(db, CompiledStatements.UserRaceInfo.removeUserFromRace)
                // Remove UserRaceInfo
                CompiledStatements.UserRaceInfo.delete.bind(id())
                CompiledStatements.execute(db, CompiledStatements.UserRaceInfo.delete)
                it.markSuccessful()
            }
            latch?.countDown()
        } else if (userId() != null) {
            // Find the User belonging to this UserRaceInfo
            val userStatement = User.FACTORY.select_for_id(userId()!!)
            db.query(userStatement.statement, *userStatement.args).use {
                // If found, just update the User and UserRaceInfo
                val userFound = it.moveToFirst()
                if (userFound) {
                    updateInfoAndUser(db, latch)
                } else {
                    // Else, get the User from the network and then update the User and UserRaceInfo
                    API.getUser(userId()!!, false) { updateInfoAndUser(db, latch) }
                }
            }
        } else {
            // This shouldn't happen, but in case it does we at least have the UserRaceInfo in the DB.
            updateInfoAndUser(db, latch)
        }
    }

    private fun updateInfoAndUser(db: BriteDatabase?, latch: CountDownLatch? = null, userId: String? = null) {
        db?.newTransaction()?.use {
            // Update the User with this raceId
            CompiledStatements.User.updateRace.bind(raceId(), userId ?: userId()!!)
            CompiledStatements.execute(db, CompiledStatements.User.updateRace)
            // Insert this UserRaceInfo into the database
            insertOrUpdate(db, contentValues)
            it.markSuccessful()
        }
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

        // Needed by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<UserRaceInfo> {
            return AutoValue_UserRaceInfo.GsonTypeAdapter(gson)
        }
    }
}
