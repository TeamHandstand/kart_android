package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqldelight.ColumnAdapter;
import com.squareup.sqldelight.RowMapper;

import java.util.Date;

import us.handstand.kartwheel.util.DateFormatter;

import static android.text.TextUtils.isEmpty;

@AutoValue
public abstract class User implements UserModel {
    public static final Factory<User> FACTORY = new Factory<>(new Creator<User>() {
        @Override
        public User create(@NonNull String id, @Nullable String authToken, @Nullable Date birth, @Nullable String cell, @Nullable String charmanderOrSquirtle, @Nullable String email, @Nullable String eventId, @Nullable Long facetimeCount, @Nullable String firstName, @Nullable String imageUrl, @Nullable String lastName, @Nullable String miniGameId, @Nullable String nickName, @Nullable String pancakeOrWaffle, @Nullable String pushDeviceToken, @Nullable Boolean pushEnabled, @Nullable String raceId, @Nullable String referralType, @Nullable String teamId, @Nullable Double totalAntiMiles, @Nullable Double totalDistanceMiles, @Nullable String updatedAt) {
            return new AutoValue_User(id, authToken, birth, cell, charmanderOrSquirtle, email, eventId, facetimeCount, firstName, imageUrl, lastName, miniGameId, nickName, pancakeOrWaffle, pushDeviceToken, pushEnabled, raceId, referralType, teamId, totalAntiMiles, totalDistanceMiles, updatedAt);
        }
    }, new ColumnAdapter<Date, String>() {
        @NonNull
        @Override
        public Date decode(String databaseValue) {
            return DateFormatter.INSTANCE.get(databaseValue);
        }

        @Override
        public String encode(@Nullable Date value) {
            return value == null ? "" : DateFormatter.INSTANCE.getString(value);
        }
    });
    public static final RowMapper<User> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    public void insert(BriteDatabase db) {
        if (db != null) {
            db.insert(TABLE_NAME, getContentValues());
        }
    }

    public void update(BriteDatabase db) {
        if (db != null) {
            ContentValues cv = getContentValues();
            cv.remove(ID);
            db.update(TABLE_NAME, cv, ID + " = ?", id());
        }
    }

    private ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        putIfNotEmpty(cv, ID, id());
        putIfNotEmpty(cv, AUTHTOKEN, authToken());
        Date birth = birth();
        putIfNotEmpty(cv, BIRTH, birth == null ? "" : DateFormatter.INSTANCE.getString(birth));
        putIfNotEmpty(cv, CELL, cell());
        putIfNotEmpty(cv, CHARMANDERORSQUIRTLE, charmanderOrSquirtle());
        putIfNotEmpty(cv, EMAIL, email());
        putIfNotEmpty(cv, EVENTID, eventId());
        cv.put(FACETIMECOUNT, facetimeCount());
        putIfNotEmpty(cv, FIRSTNAME, firstName());
        putIfNotEmpty(cv, IMAGEURL, imageUrl());
        putIfNotEmpty(cv, LASTNAME, lastName());
        putIfNotEmpty(cv, MINIGAMEID, miniGameId());
        putIfNotEmpty(cv, NICKNAME, nickName());
        putIfNotEmpty(cv, PANCAKEORWAFFLE, pancakeOrWaffle());
        putIfNotEmpty(cv, PUSHDEVICETOKEN, pushDeviceToken());
        cv.put(PUSHENABLED, pushEnabled());
        putIfNotEmpty(cv, RACEID, raceId());
        putIfNotEmpty(cv, REFERRALTYPE, referralType());
        putIfNotEmpty(cv, TEAMID, teamId());
        cv.put(TOTALANTIMILES, totalAntiMiles());
        cv.put(TOTALDISTANCEMILES, totalDistanceMiles());
        putIfNotEmpty(cv, UPDATEDAT, updatedAt());
        return cv;
    }

    private void putIfNotEmpty(ContentValues cv, String key, String value) {
        if (!isEmpty(value)) {
            cv.put(key, value);
        }
    }

    public boolean hasCriticalInfo() {
        return !isEmpty(charmanderOrSquirtle()) && !isEmpty(pancakeOrWaffle());
    }

    public boolean hasAllInformation() {
        return hasCriticalInfo() && !isEmpty(cell()) && !isEmpty(email())
                && !isEmpty(firstName()) && !isEmpty(lastName()) && !isEmpty(nickName());
    }

    public User construct(String charmanderOrSquirtle, String pancakeOrWaffle) {
        return User.FACTORY.creator.create(id(),
                authToken(), // authToken
                birth(),
                cell(),
                charmanderOrSquirtle,
                email(),
                eventId(),
                facetimeCount(), // facetime count
                firstName(),
                imageUrl(), // imageUrl
                lastName(),
                miniGameId(), // miniGameId
                nickName(),
                pancakeOrWaffle,
                pushDeviceToken(), // device token
                pushEnabled(), // push enabled
                raceId(), // race id
                referralType(), // referral type
                teamId(), // team id
                totalAntiMiles(), // total anti miles
                totalDistanceMiles(), // total distance miles
                updatedAt() // updated at
        );
    }

    public User construct(String birth, String cell, String email, String firstName, String lastName, String nickname) {
        return User.FACTORY.creator.create(id(),
                authToken(), // authToken
                DateFormatter.INSTANCE.get(birth),
                cell,
                charmanderOrSquirtle(),
                email,
                eventId(),
                facetimeCount(), // facetime count
                firstName,
                imageUrl(), // imageUrl
                lastName,
                miniGameId(), // miniGameId
                nickname,
                pancakeOrWaffle(),
                pushDeviceToken(), // device token
                pushEnabled(), // push enabled
                raceId(), // race id
                referralType(), // referral type
                teamId(), // team id
                totalAntiMiles(), // total anti miles
                totalDistanceMiles(), // total distance miles
                updatedAt() // updated at
        );
    }

    // Needed by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<User> typeAdapter(Gson gson) {
        return new AutoValue_User.GsonTypeAdapter(gson);
    }
}
