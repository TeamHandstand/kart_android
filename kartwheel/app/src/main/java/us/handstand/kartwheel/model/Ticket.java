package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqldelight.RowMapper;

import static android.text.TextUtils.isEmpty;
import static us.handstand.kartwheel.model.Util.putIfNotAbsent;

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

    public void insert(@Nullable BriteDatabase db) {
        if (db != null) {
            ContentValues cv = new ContentValues();
            putIfNotAbsent(cv, ID, id());
            putIfNotAbsent(cv, CLAIMEDAT, claimedAt());
            putIfNotAbsent(cv, CODE, code());
            putIfNotAbsent(cv, EVENTID, eventId());
            putIfNotAbsent(cv, FORFEITEDAT, forfeitedAt());
            putIfNotAbsent(cv, PAYMENTID, paymentId());
            putIfNotAbsent(cv, PLAYERID, playerId());
            putIfNotAbsent(cv, PRICETIERID, priceTierId());
            putIfNotAbsent(cv, PURCHASEDAT, purchasedAt());
            putIfNotAbsent(cv, PURCHASERID, purchaserId());
            putIfNotAbsent(cv, TEAMID, teamId());
            putIfNotAbsent(cv, UPDATEDAT, updatedAt());
            db.insert(TABLE_NAME, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public boolean isClaimed() {
        return !isEmpty(claimedAt()) && !isEmpty(forfeitedAt());
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Ticket> typeAdapter(Gson gson) {
        return new AutoValue_Ticket.GsonTypeAdapter(gson);
    }
}
