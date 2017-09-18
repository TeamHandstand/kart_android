package us.handstand.kartwheel.model


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.squareup.sqlbrite2.BriteDatabase
import us.handstand.kartwheel.model.TicketModel.Creator
import us.handstand.kartwheel.model.Util.putIfNotAbsent

@AutoValue
abstract class Ticket : TicketModel {

    fun insert(db: BriteDatabase?) {
        if (db != null) {
            val cv = ContentValues()
            putIfNotAbsent(cv, TicketModel.ID, id())
            putIfNotAbsent(cv, TicketModel.CLAIMEDAT, claimedAt()?.time)
            putIfNotAbsent(cv, TicketModel.CODE, code())
            putIfNotAbsent(cv, TicketModel.EVENTID, eventId())
            putIfNotAbsent(cv, TicketModel.FORFEITEDAT, forfeitedAt()?.time)
            putIfNotAbsent(cv, TicketModel.PAYMENTID, paymentId())
            putIfNotAbsent(cv, TicketModel.PLAYERID, playerId())
            putIfNotAbsent(cv, TicketModel.PRICETIERID, priceTierId())
            putIfNotAbsent(cv, TicketModel.PURCHASEDAT, purchasedAt()?.time)
            putIfNotAbsent(cv, TicketModel.PURCHASERID, purchaserId())
            putIfNotAbsent(cv, TicketModel.TEAMID, teamId())
            putIfNotAbsent(cv, TicketModel.UPDATEDAT, updatedAt()?.time)
            db.insert(TicketModel.TABLE_NAME, cv, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    val isClaimed: Boolean
        get() = claimedAt() != null && forfeitedAt() == null

    companion object : Creator<Ticket> by Creator(::AutoValue_Ticket) {
        val FACTORY = TicketModel.Factory<Ticket>(Creator<Ticket> { id, code, claimedAt, eventId, forfeitedAt, paymentId, playerId, priceTierId, purchasedAt, purchaserId, teamId, updatedAt -> create(id, code, claimedAt, eventId, forfeitedAt, paymentId, playerId, priceTierId, purchasedAt, purchaserId, teamId, updatedAt) }, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG)
        // Required by Gson
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<Ticket> {
            return AutoValue_Ticket.GsonTypeAdapter(gson)
        }
    }
}
