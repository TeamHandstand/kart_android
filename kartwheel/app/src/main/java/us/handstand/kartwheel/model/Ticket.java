package us.handstand.kartwheel.model;


import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Ticket implements TicketModel {
    public static final Factory<Ticket> FACTORY = new Factory<>(AutoValue_Ticket::new);
    public static final RowMapper<Ticket> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Ticket> typeAdapter(Gson gson) {
        return new AutoValue_Ticket.GsonTypeAdapter(gson);
    }
}
