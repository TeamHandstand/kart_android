package us.handstand.kartwheel.model

import com.squareup.sqlbrite.BriteDatabase
import com.squareup.sqldelight.SqlDelightCompiledStatement


object CompiledStatements {
    object User {
        lateinit var removeRace: UserModel.Remove_race_id
        lateinit var updateRace: UserModel.Update_race_id
    }

    object UserRaceInfo {
        lateinit var delete: UserRaceInfoModel.Delete_for_id
        lateinit var deleteAllForRace: UserRaceInfoModel.Delete_all_for_race
        lateinit var deleteForUserAndRace: UserRaceInfoModel.Delete_for_user_and_race
        lateinit var removeUserFromRace: UserRaceInfoModel.Remove_race_id_from_user
    }


    fun initialize(db: BriteDatabase) {
        val writableDatabase = db.writableDatabase
        User.removeRace = UserModel.Remove_race_id(writableDatabase)
        User.updateRace = UserModel.Update_race_id(writableDatabase)
        UserRaceInfo.delete = UserRaceInfoModel.Delete_for_id(writableDatabase)
        UserRaceInfo.deleteAllForRace = UserRaceInfoModel.Delete_all_for_race(writableDatabase)
        UserRaceInfo.deleteForUserAndRace = UserRaceInfoModel.Delete_for_user_and_race(writableDatabase)
        UserRaceInfo.removeUserFromRace = UserRaceInfoModel.Remove_race_id_from_user(writableDatabase)
    }

    /**
     * We have to execute all DB transactions on the BriteDatabase for our Subscribers to get notified
     * of any Table changes. This is just a helper method.
     */
    fun execute(db: BriteDatabase?, statement: SqlDelightCompiledStatement) {
        db?.executeUpdateDelete(statement.table, statement.program)
    }
}