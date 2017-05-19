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
public abstract class UserRaceInfo implements UserRaceInfoModel {
    public static final Factory<UserRaceInfo> FACTORY = new Factory<>(new Creator<UserRaceInfo>() {
        @Override
        public UserRaceInfo create(@NonNull String id, @Nullable String challengeId, @Nullable Double completionPercent, @Nullable String courseId, @Nullable Long currentLap, @Nullable String endTime, @Nullable String funAnswerDisplayText, @Nullable String itemId, @Nullable Double latitude, @Nullable Double longitude, @Nullable String raceId, @Nullable Long ranking, @Nullable String removedAt, @Nullable String state, @Nullable Long targeted, @Nullable String targetedBy, @Nullable Double totalAntiMiles, @Nullable Double totalMileage, @Nullable Double totalTime, @Nullable String updatedAt) {
            return null;
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
        putIfNotAbsent(cv, CHALLENGEID, challengeId());
        putIfNotAbsent(cv, COMPLETIONPERCENT, completionPercent());
        putIfNotAbsent(cv, COURSEID, courseId());
        putIfNotAbsent(cv, CURRENTLAP, currentLap());
        putIfNotAbsent(cv, ENDTIME, endTime());
        putIfNotAbsent(cv, FUNANSWERDISPLAYTEXT, funAnswerDisplayText());
        putIfNotAbsent(cv, ITEMID, itemId());
        putIfNotAbsent(cv, LATITUDE, latitude());
        putIfNotAbsent(cv, LONGITUDE, longitude());
        putIfNotAbsent(cv, RACEID, raceId());
        putIfNotAbsent(cv, STATE, state());
        putIfNotAbsent(cv, TARGETED, targeted());
        putIfNotAbsent(cv, TARGETEDBY, targetedBy());
        putIfNotAbsent(cv, TOTALANTIMILES, totalAntiMiles());
        putIfNotAbsent(cv, TOTALMILEAGE, totalMileage());
        putIfNotAbsent(cv, TOTALTIME, totalTime());
        putIfNotAbsent(cv, UPDATEDAT, updatedAt());
        return cv;
    }

    // Needed by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<UserRaceInfo> typeAdapter(Gson gson) {
        return new AutoValue_UserRaceInfo.GsonTypeAdapter(gson);
    }
}
