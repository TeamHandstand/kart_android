package us.handstand.kartwheel.model;


import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Team implements TeamModel {
    public static final TeamModel.Factory<Team> FACTORY = new TeamModel.Factory<>((_id, bronzeCount, eventId, goldCount, silverCount, ribbonCount, ranking, updatedAt) -> new AutoValue_Team(_id, bronzeCount, eventId, goldCount, silverCount, ribbonCount, ranking, updatedAt));
    public static final RowMapper<Team> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Team> typeAdapter(Gson gson) {
        return new AutoValue_Team.GsonTypeAdapter(gson);
    }
}
