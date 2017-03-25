package us.handstand.kartwheel.model;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Ticket implements TicketModel {
    public static final Factory<Ticket> FACTORY = new Factory<>((_id, code, claimedAt, eventId, forfeitedAt, playerId, purchasedAt, purchaserId, updatedAt) -> new AutoValue_Ticket(_id, code, claimedAt, eventId, forfeitedAt, playerId, purchasedAt, purchaserId, updatedAt));
    public static final RowMapper<Ticket> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Ticket> typeAdapter(Gson gson) {
        return new AutoValue_Ticket.GsonTypeAdapter(gson);
    }
}
