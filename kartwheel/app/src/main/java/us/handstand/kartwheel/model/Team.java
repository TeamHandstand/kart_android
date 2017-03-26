package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.ColumnAdapter;
import com.squareup.sqldelight.RowMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class Team implements TeamModel {
    public static final TeamModel.Factory<Team> FACTORY = new TeamModel.Factory<>(AutoValue_Team::new, new <Ticket>ListTicketColumnAdapter(), new <User>ListTicketColumnAdapter());
    public static final RowMapper<Team> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    public void insert() {
        ContentValues teamCV = new ContentValues();
        teamCV.put(Team.ID, id());
        teamCV.put(Team.BRONZECOUNT, bronzeCount());
        teamCV.put(Team.EVENTID, eventId());
        teamCV.put(Team.GOLDCOUNT, goldCount());
        teamCV.put(Team.NAME, name());
        teamCV.put(Team.RANKING, ranking());
        teamCV.put(Team.RIBBONCOUNT, ribbonCount());
        teamCV.put(Team.SILVERCOUNT, silverCount());
        teamCV.put(Team.SLUG, slug());
        teamCV.put(Team.UPDATEDAT, updatedAt());

        Database.get().insert(TABLE_NAME, teamCV);
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Team> typeAdapter(Gson gson) {
        return new AutoValue_Team.GsonTypeAdapter(gson);
    }

    private static class ListTicketColumnAdapter<T> implements ColumnAdapter<List<T>, byte[]> {
        @NonNull
        @Override
        public List<T> decode(byte[] databaseValue) {
            List<T> tickets = Collections.emptyList();
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(databaseValue);
                ObjectInputStream is = new ObjectInputStream(in);
                tickets = (List<T>) is.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tickets;
        }

        @Override
        public byte[] encode(@NonNull List<T> value) {
            byte[] bytes = new byte[0];
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(out);
                os.writeObject(value);
                bytes = out.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bytes;
        }
    }
}
