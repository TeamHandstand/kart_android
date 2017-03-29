package us.handstand.kartwheel.model;


import android.content.ContentValues;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Race implements RaceModel {
    public static final RaceModel.Factory<Race> FACTORY = new RaceModel.Factory<>(AutoValue_Race::new);
    public static final RowMapper<Race> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    public void insert() {
        ContentValues cv = new ContentValues();
        cv.put(ID, id());
        cv.put(FUNQUESTION, funQuestion());
        cv.put(OPENSPOTS, openSpots());
        cv.put(SHORTANSWER1, shortAnswer1());
        cv.put(SHORTANSWER2, shortAnswer2());
        cv.put(SLUG, slug());
        cv.put(STARTTIME, startTime());
        cv.put(UPDATEDAT, updatedAt());

        Database.Companion.get().insert(TABLE_NAME, cv);
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Race> typeAdapter(Gson gson) {
        return new AutoValue_Race.GsonTypeAdapter(gson);
    }
}
