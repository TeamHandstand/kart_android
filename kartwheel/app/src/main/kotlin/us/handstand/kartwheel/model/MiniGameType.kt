package us.handstand.kartwheel.model


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite2.BriteDatabase
import us.handstand.kartwheel.model.MiniGameTypeModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent

@AutoValue
abstract class MiniGameType : MiniGameTypeModel {

    fun insert(db: BriteDatabase?) {
        db?.insert(MiniGameTypeModel.TABLE_NAME, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private val contentValues: ContentValues
        get() {
            val cv = ContentValues()
            putIfNotAbsent(cv, MiniGameTypeModel.ID, id())
            putIfNotAbsent(cv, MiniGameTypeModel.ALLOWEDACTIVE, allowedActive())
            putIfNotAbsent(cv, MiniGameTypeModel.ALLOWEDINACTIVE, allowedInactive())
            putIfNotAbsent(cv, MiniGameTypeModel.IMAGEURL, imageUrl())
            putIfNotAbsent(cv, MiniGameTypeModel.MEETUPINSTRUCTIONS, meetupInstructions())
            putIfNotAbsent(cv, MiniGameTypeModel.MINIMUMPLAYERS, minimumPlayers())
            putIfNotAbsent(cv, MiniGameTypeModel.NAME, name())
            putIfNotAbsent(cv, MiniGameTypeModel.TIMEESTIMATE, timeEstimate())
            return cv
        }

    companion object : Creator<MiniGameType> by Creator(::AutoValue_MiniGameType) {
        val FACTORY = MiniGameTypeModel.Factory<MiniGameType>(Creator<MiniGameType> { id, allowedActive, allowedInactive, imageUrl, meetupInstructions, minimumPlayers, name, timeEstimate -> create(id, allowedActive, allowedInactive, imageUrl, meetupInstructions, minimumPlayers, name, timeEstimate) })
        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<MiniGameType> {
            return AutoValue_MiniGameType.GsonTypeAdapter(gson)
        }
    }
}
