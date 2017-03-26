package us.handstand.kartwheel.model;


import android.content.ContentValues;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class User implements UserModel {
    public static final Factory<User> FACTORY = new Factory<>(AutoValue_User::new);
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

        Database.get().insert(TABLE_NAME, cv);
    }

    // Needed by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<User> typeAdapter(Gson gson) {
        return new AutoValue_User.GsonTypeAdapter(gson);
    }
}
