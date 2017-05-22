package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqlbrite.BriteDatabase;

import static us.handstand.kartwheel.model.Util.putIfNotAbsent;

@AutoValue
public abstract class MiniGameType implements MiniGameTypeModel {
    public static final MiniGameTypeModel.Factory<MiniGameType> FACTORY = new MiniGameTypeModel.Factory<>(new Creator<MiniGameType>() {
        @Override
        public MiniGameType create(@NonNull String id, @Nullable Boolean allowedActive, @Nullable Boolean allowedInactive, @NonNull String imageUrl, @NonNull String meetupInstructions, @Nullable Long minimumPlayers, @NonNull String name, @NonNull String timeEstimate) {
            return new AutoValue_MiniGameType(id, allowedActive, allowedInactive, imageUrl, meetupInstructions, minimumPlayers, name, timeEstimate);
        }
    });

    public void insert(BriteDatabase db) {
        if (db != null) {
            db.insert(TABLE_NAME, getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    private ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        putIfNotAbsent(cv, ID, id());
        putIfNotAbsent(cv, ALLOWEDACTIVE, allowedActive());
        putIfNotAbsent(cv, ALLOWEDINACTIVE, allowedInactive());
        putIfNotAbsent(cv, IMAGEURL, imageUrl());
        putIfNotAbsent(cv, MEETUPINSTRUCTIONS, meetupInstructions());
        putIfNotAbsent(cv, MINIMUMPLAYERS, minimumPlayers());
        putIfNotAbsent(cv, NAME, name());
        putIfNotAbsent(cv, TIMEESTIMATE, timeEstimate());
        return cv;
    }

    // Required by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<MiniGameType> typeAdapter(Gson gson) {
        return new AutoValue_MiniGameType.GsonTypeAdapter(gson);
    }
}
