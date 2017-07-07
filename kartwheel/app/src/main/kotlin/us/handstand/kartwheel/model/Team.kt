package us.handstand.kartwheel.model


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite.BriteDatabase
import us.handstand.kartwheel.model.TeamModel.Creator

@AutoValue
abstract class Team : TeamModel {

    fun insert(db: BriteDatabase?) {
        if (db != null) {
            val teamCV = ContentValues()
            teamCV.put(TeamModel.ID, id())
            teamCV.put(TeamModel.BRONZECOUNT, bronzeCount())
            teamCV.put(TeamModel.EVENTID, eventId())
            teamCV.put(TeamModel.GOLDCOUNT, goldCount())
            teamCV.put(TeamModel.NAME, name())
            teamCV.put(TeamModel.RANKING, ranking())
            teamCV.put(TeamModel.RIBBONCOUNT, ribbonCount())
            teamCV.put(TeamModel.SILVERCOUNT, silverCount())
            teamCV.put(TeamModel.SLUG, slug())
            teamCV.put(TeamModel.UPDATEDAT, updatedAt())

            db.insert(TeamModel.TABLE_NAME, teamCV, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    companion object : Creator<Team> by Creator(::AutoValue_Team) {
        val FACTORY = TeamModel.Factory<Team>(Creator<Team> { id, bronzeCount, eventId, goldCount, name, ribbonCount, ranking, silverCount, slug, tickets, updatedAt, users -> create(id, bronzeCount, eventId, goldCount, name, ribbonCount, ranking, silverCount, slug, tickets, updatedAt, users) }, ColumnAdapters.ListTicketColumnAdapter(), ColumnAdapters.ListTicketColumnAdapter())
        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<Team> {
            return AutoValue_Team.GsonTypeAdapter(gson)
        }
    }
}
