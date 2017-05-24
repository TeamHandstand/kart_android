package us.handstand.kartwheel.model


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils.isEmpty
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite.BriteDatabase
import us.handstand.kartwheel.model.UserModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent
import us.handstand.kartwheel.model.columnadapter.ColumnAdapters
import us.handstand.kartwheel.util.DateFormatter

@AutoValue
abstract class User : UserModel {

    fun insert(db: BriteDatabase?) {
        db?.insert(UserModel.TABLE_NAME, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun insertWithRaceId(db: BriteDatabase?, raceId: String) {
        if (db != null) {
            val cv = ContentValues()
            cv.put(UserModel.RACEID, raceId)
            db.insert(UserModel.TABLE_NAME, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    fun update(db: BriteDatabase?) {
        if (db != null) {
            val cv = contentValues
            cv.remove(UserModel.ID)
            db.update(UserModel.TABLE_NAME, cv, UserModel.ID + " = ?", id())
        }
    }

    private val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, UserModel.ID, id())
            putIfNotAbsent(cv, UserModel.AUTHTOKEN, authToken())
            putIfNotAbsent(cv, UserModel.BIRTH, ColumnAdapters.dateToLong(birth()))
            putIfNotAbsent(cv, UserModel.BUDDYURL, buddyUrl())
            putIfNotAbsent(cv, UserModel.CELL, cell())
            putIfNotAbsent(cv, UserModel.CHARMANDERORSQUIRTLE, charmanderOrSquirtle())
            putIfNotAbsent(cv, UserModel.EMAIL, email())
            putIfNotAbsent(cv, UserModel.EVENTID, eventId())
            putIfNotAbsent(cv, UserModel.FIRSTNAME, firstName())
            putIfNotAbsent(cv, UserModel.IMAGEURL, imageUrl())
            putIfNotAbsent(cv, UserModel.LASTNAME, lastName())
            putIfNotAbsent(cv, UserModel.MINIGAMEID, miniGameId())
            putIfNotAbsent(cv, UserModel.NICKNAME, nickName())
            putIfNotAbsent(cv, UserModel.PANCAKEORWAFFLE, pancakeOrWaffle())
            putIfNotAbsent(cv, UserModel.PUSHDEVICETOKEN, pushDeviceToken())
            putIfNotAbsent(cv, UserModel.PUSHENABLED, pushEnabled())
            putIfNotAbsent(cv, UserModel.RACEID, raceId())
            putIfNotAbsent(cv, UserModel.REFERRALTYPE, referralType())
            putIfNotAbsent(cv, UserModel.TEAMID, teamId())
            putIfNotAbsent(cv, UserModel.TOTALANTIMILES, totalAntiMiles())
            putIfNotAbsent(cv, UserModel.TOTALDISTANCEMILES, totalDistanceMiles())
            putIfNotAbsent(cv, UserModel.UPDATEDAT, ColumnAdapters.dateToLong(updatedAt()))
            return cv
        }

    fun hasCriticalInfo(): Boolean {
        return !isEmpty(charmanderOrSquirtle()) && !isEmpty(pancakeOrWaffle())
    }

    fun hasAllInformation(): Boolean {
        return hasCriticalInfo() && !isEmpty(cell()) && !isEmpty(email())
                && !isEmpty(firstName()) && !isEmpty(lastName()) && !isEmpty(nickName())
    }

    fun wasOnboarded(): Boolean {
        return !isEmpty(buddyUrl()) && !isEmpty(imageUrl())
    }

    fun construct(charmanderOrSquirtle: String, pancakeOrWaffle: String): User {
        return create(id(),
                authToken(), // authToken
                birth(),
                buddyUrl(),
                cell(),
                charmanderOrSquirtle,
                email(),
                eventId(),
                firstName(),
                imageUrl(), // imageUrl
                lastName(),
                miniGameId(), // miniGameId
                nickName(),
                pancakeOrWaffle,
                pushDeviceToken(), // device token
                pushEnabled(), // push enabled
                raceId(), // race id
                referralType(), // referral type
                teamId(), // team id
                totalAntiMiles(), // total anti miles
                totalDistanceMiles(), // total distance miles
                updatedAt() // updated at
        )
    }

    fun construct(birth: String, cell: String, email: String, firstName: String, lastName: String, nickname: String): User {
        return create(id(),
                authToken(), // authToken
                DateFormatter[birth],
                buddyUrl(),
                cell,
                charmanderOrSquirtle(),
                email,
                eventId(),
                firstName,
                imageUrl(), // imageUrl
                lastName,
                miniGameId(), // miniGameId
                nickname,
                pancakeOrWaffle(),
                pushDeviceToken(), // device token
                pushEnabled(), // push enabled
                raceId(), // race id
                referralType(), // referral type
                teamId(), // team id
                totalAntiMiles(), // total anti miles
                totalDistanceMiles(), // total distance miles
                updatedAt() // updated at
        )
    }

    companion object : Creator<User> by Creator(::AutoValue_User) {

        fun emptyUser(): User {
            return create("", "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
        }

        val FACTORY = UserModel.Factory<User>(Creator<User> { id, authToken, birth, buddyUrl, cell, charmanderOrSquirtle, email, eventId, firstName, imageUrl, lastName, miniGameId, nickName, pancakeOrWaffle, pushDeviceToken, pushEnabled, raceId, referralType, teamId, totalAntiMiles, totalDistanceMiles, updatedAt -> create(id, authToken, birth, buddyUrl, cell, charmanderOrSquirtle, email, eventId, firstName, imageUrl, lastName, miniGameId, nickName, pancakeOrWaffle, pushDeviceToken, pushEnabled, raceId, referralType, teamId, totalAntiMiles, totalDistanceMiles, updatedAt) }, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG)

        // Needed by Gson
        @JvmStatic
        fun typeAdapterFactory(gson: Gson): TypeAdapter<User> {
            return AutoValue_User.GsonTypeAdapter(gson)
        }
    }
}
