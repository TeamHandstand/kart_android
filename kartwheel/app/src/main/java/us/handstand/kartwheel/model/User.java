package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class User implements UserModel {
    public static final Factory<User> FACTORY = new Factory<>(new Creator<User>() {
        @Override
        public User create(@NonNull String id, @Nullable String authToken, @Nullable String birth, @Nullable String cell, @Nullable String charmanderOrSquirtle, @Nullable String email, @Nullable String eventId, @Nullable Long facetimeCount, @Nullable String firstName, @Nullable String imageUrl, @Nullable String lastName, @Nullable String miniGameId, @Nullable String nickName, @Nullable String pancakeOrWaffle, @Nullable String pushDeviceToken, @Nullable Boolean pushEnabled, @Nullable String raceId, @Nullable String referralType, @Nullable String teamId, @Nullable Double totalAntiMiles, @Nullable Double totalDistanceMiles, @Nullable String updatedAt) {
            return new AutoValue_User(id, authToken, birth, cell, charmanderOrSquirtle, email, eventId, facetimeCount, firstName, imageUrl, lastName, miniGameId, nickName, pancakeOrWaffle, pushDeviceToken, pushEnabled, raceId, referralType, teamId, totalAntiMiles, totalDistanceMiles, updatedAt);
        }
    });
    public static final RowMapper<User> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    public void insert() {
        ContentValues cv = new ContentValues();
        cv.put(ID, id());
        cv.put(AUTHTOKEN, authToken());
        cv.put(BIRTH, birth());
        cv.put(CELL, cell());
        cv.put(CHARMANDERORSQUIRTLE, charmanderOrSquirtle());
        cv.put(EMAIL, email());
        cv.put(EVENTID, eventId());
        cv.put(FACETIMECOUNT, facetimeCount());
        cv.put(FIRSTNAME, firstName());
        cv.put(IMAGEURL, imageUrl());
        cv.put(LASTNAME, lastName());
        cv.put(MINIGAMEID, miniGameId());
        cv.put(NICKNAME, nickName());
        cv.put(PANCAKEORWAFFLE, pancakeOrWaffle());
        cv.put(PUSHDEVICETOKEN, pushDeviceToken());
        cv.put(PUSHENABLED, pushEnabled());
        cv.put(RACEID, raceId());
        cv.put(REFERRALTYPE, referralType());
        cv.put(TEAMID, teamId());
        cv.put(TOTALANTIMILES, totalAntiMiles());
        cv.put(TOTALDISTANCEMILES, totalDistanceMiles());
        cv.put(UPDATEDAT, updatedAt());

        Database.Companion.get().insert(TABLE_NAME, cv);
    }

    // Needed by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<User> typeAdapter(Gson gson) {
        return new AutoValue_User.GsonTypeAdapter(gson);
    }
}
