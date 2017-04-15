package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Ticket implements TicketModel {
    public static final Factory<Ticket> FACTORY = new Factory<>(new Creator<Ticket>() {
        @Override
        public Ticket create(@NonNull String id, @Nullable String code, @Nullable String claimedAt,
                             @Nullable String eventId, @Nullable String forfeitedAt, @Nullable String paymentId,
                             @Nullable String playerId, @Nullable String priceTierId, @Nullable String purchasedAt,
                             @Nullable String purchaserId, @Nullable String teamId, @Nullable String updatedAt) {
            return new AutoValue_Ticket(id, code, claimedAt, eventId, forfeitedAt, paymentId, playerId, priceTierId, purchasedAt, purchaserId, teamId, updatedAt);
        }
    });
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

        Database.Companion.get().insert(TABLE_NAME, cv);
    }

    public boolean isClaimed() {
        return !TextUtils.isEmpty(claimedAt());
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Ticket> typeAdapter(Gson gson) {
        return new AutoValue_Ticket.GsonTypeAdapter(gson);
    }
}
