package us.handstand.kartwheel.model;


import android.content.ContentValues;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Ticket implements TicketModel {
    public static final Factory<Ticket> FACTORY = new Factory<>(AutoValue_Ticket::new);
    public static final RowMapper<Ticket> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    public void insert() {
        ContentValues cv = new ContentValues();
        cv.put(ID, id());
        cv.put(CLAIMEDAT, claimedAt());
        cv.put(CODE, code());
        cv.put(EVENTID, eventId());
        cv.put(FORFEITEDAT, forfeitedAt());
        cv.put(PAYMENTID, paymentId());
        cv.put(PLAYERID, playerId());
        cv.put(PRICETIERID, priceTierId());
        cv.put(PURCHASEDAT, purchasedAt());
        cv.put(PURCHASERID, purchaserId());
        cv.put(TEAMID, teamId());
        cv.put(UPDATEDAT, updatedAt());

        Database.get().insert(TABLE_NAME, cv);
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Ticket> typeAdapter(Gson gson) {
        return new AutoValue_Ticket.GsonTypeAdapter(gson);
    }
}
