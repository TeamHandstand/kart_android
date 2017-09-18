package us.handstand.kartwheel.model


import android.content.ContentValues
import android.text.TextUtils.isEmpty
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite2.BriteDatabase
import us.handstand.kartwheel.model.UserModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent
import us.handstand.kartwheel.util.DateFormatter

@AutoValue
abstract class User : UserModel, Insertable {

    fun updateRace(db: BriteDatabase?, raceId: String? = null) {
        if (db != null) {
            val cv = contentValues
            cv.put(UserModel.RACEID, raceId ?: "")
            insertOrUpdate(db, cv)
        }
    }

    override fun tableName(): String {
        return UserModel.TABLE_NAME
    }

    override val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, UserModel.ID, id())
            putIfNotAbsent(cv, UserModel.AUTHTOKEN, authToken())
            putIfNotAbsent(cv, UserModel.BIRTH, birth()?.time)
            putIfNotAbsent(cv, UserModel.BUDDYURL, buddyUrl())
            putIfNotAbsent(cv, UserModel.CELL, cell())
            putIfNotAbsent(cv, UserModel.CHARMANDERORSQUIRTLE, charmanderOrSquirtle())
            putIfNotAbsent(cv, UserModel.EMAIL, email())
            putIfNotAbsent(cv, UserModel.EVENTID, eventId())
            putIfNotAbsent(cv, UserModel.FIRSTNAME, firstName())
            putIfNotAbsent(cv, UserModel.FURBYORTAMAGACHI, furbyOrTamagachi())
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
            return cv
        }

    fun hasAllInformation(): Boolean {
        return !isEmpty(cell()) && !isEmpty(email())
                && !isEmpty(firstName()) && !isEmpty(lastName()) && !isEmpty(nickName())
    }

    fun wasOnboarded(): Boolean {
        return !isEmpty(buddyUrl()) && !isEmpty(imageUrl())
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
                furbyOrTamagachi(),
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
                totalDistanceMiles() // total distance miles
        )
    }

    companion object : Creator<User> by Creator(::AutoValue_User) {

        fun emptyUser(): User {
            return create("", "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
        }

        val FACTORY = UserModel.Factory<User>(Creator<User> { id, authToken, birth, buddyUrl, cell, charmanderOrSquirtle, email, eventId, firstName, furbyOrTamagachi, imageUrl, lastName, miniGameId, nickName, pancakeOrWaffle, pushDeviceToken, pushEnabled, raceId, referralType, teamId, totalAntiMiles, totalDistanceMiles -> create(id, authToken, birth, buddyUrl, cell, charmanderOrSquirtle, email, eventId, firstName, furbyOrTamagachi, imageUrl, lastName, miniGameId, nickName, pancakeOrWaffle, pushDeviceToken, pushEnabled, raceId, referralType, teamId, totalAntiMiles, totalDistanceMiles) }, ColumnAdapters.DATE_LONG)

        fun updateRaceId(db: BriteDatabase?, id: String?, raceId: String?) {
            if (id == null) {
                return
            }
            val userStatement = User.FACTORY.select_for_id(id)
            db?.query(userStatement.statement, *userStatement.args)?.use {
                if (it.moveToFirst()) {
                    User.FACTORY.select_for_idMapper().map(it)
                            .updateRace(db, raceId)
                }
            }
        }

        // Needed by Gson
        @JvmStatic
        fun typeAdapterFactory(gson: Gson): TypeAdapter<User> {
            return AutoValue_User.GsonTypeAdapter(gson)
        }
    }
}
