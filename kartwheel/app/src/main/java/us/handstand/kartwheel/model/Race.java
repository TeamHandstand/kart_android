package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Race implements RaceModel {
    public static final RaceModel.Factory<Race> FACTORY = new RaceModel.Factory<>(new Creator<Race>() {
        @Override
        public Race create(@NonNull String id, @Nullable String endTime, @Nullable String funQuestion, @Nullable Long openSpots, @Nullable Long raceOrder, @Nullable String replayUrl, @Nullable String shortAnswer1, @Nullable String shortAnswer2, @Nullable String slug, @Nullable String startTime, @Nullable String updatedAt, @Nullable String videoUrl) {
            return new AutoValue_Race(id, endTime, funQuestion, openSpots, raceOrder, replayUrl, shortAnswer1, shortAnswer2, slug, startTime, updatedAt, videoUrl);
        }
    });
    public static final RowMapper<Race> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    public void insert(@Nullable BriteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(ID, id());
        cv.put(FUNQUESTION, funQuestion());
        cv.put(OPENSPOTS, openSpots());
        cv.put(SHORTANSWER1, shortAnswer1());
        cv.put(SHORTANSWER2, shortAnswer2());
        cv.put(SLUG, slug());
        cv.put(STARTTIME, startTime());
        cv.put(UPDATEDAT, updatedAt());

        if (db != null) {
            db.insert(TABLE_NAME, cv);
        }
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<Race> typeAdapter(Gson gson) {
        return new AutoValue_Race.GsonTypeAdapter(gson);
    }
}
