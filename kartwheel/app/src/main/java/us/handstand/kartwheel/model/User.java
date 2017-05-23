package us.handstand.kartwheel.model;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.Date;

import us.handstand.kartwheel.model.columnadapter.ColumnAdapters;
import us.handstand.kartwheel.util.DateFormatter;

import static android.text.TextUtils.isEmpty;
import static us.handstand.kartwheel.model.Util.putIfNotAbsent;

@AutoValue
public abstract class User implements UserModel {
    public static final Factory<User> FACTORY = new Factory<>(new Creator<User>() {
        @Override
        public User create(@NonNull String id, @Nullable String authToken, @Nullable Date birth, @Nullable String buddyUrl, @Nullable String cell, @Nullable String charmanderOrSquirtle, @Nullable String email, @Nullable String eventId, @Nullable Long facetimeCount, @Nullable String firstName, @Nullable String imageUrl, @Nullable String lastName, @Nullable String miniGameId, @Nullable String nickName, @Nullable String pancakeOrWaffle, @Nullable String pushDeviceToken, @Nullable Boolean pushEnabled, @Nullable String raceId, @Nullable String referralType, @Nullable String teamId, @Nullable Double totalAntiMiles, @Nullable Double totalDistanceMiles, @Nullable Date updatedAt) {
            return new AutoValue_User(id, authToken, birth, buddyUrl, cell, charmanderOrSquirtle, email, eventId, facetimeCount, firstName, imageUrl, lastName, miniGameId, nickName, pancakeOrWaffle, pushDeviceToken, pushEnabled, raceId, referralType, teamId, totalAntiMiles, totalDistanceMiles, updatedAt);
        }
    }, ColumnAdapters.DATE_LONG, ColumnAdapters.DATE_LONG);

    public void insert(BriteDatabase db) {
        if (db != null) {
            db.insert(TABLE_NAME, getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public void insertWithRaceId(BriteDatabase db, String raceId) {
        if (db != null) {
            ContentValues cv = new ContentValues();
            cv.put(RACEID, raceId);
            db.insert(TABLE_NAME, getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
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
        putIfNotAbsent(cv, ID, id());
        putIfNotAbsent(cv, AUTHTOKEN, authToken());
        putIfNotAbsent(cv, BIRTH, ColumnAdapters.dateToLong(birth()));
        putIfNotAbsent(cv, BUDDYURL, buddyUrl());
        putIfNotAbsent(cv, CELL, cell());
        putIfNotAbsent(cv, CHARMANDERORSQUIRTLE, charmanderOrSquirtle());
        putIfNotAbsent(cv, EMAIL, email());
        putIfNotAbsent(cv, EVENTID, eventId());
        putIfNotAbsent(cv, FACETIMECOUNT, facetimeCount());
        putIfNotAbsent(cv, FIRSTNAME, firstName());
        putIfNotAbsent(cv, IMAGEURL, imageUrl());
        putIfNotAbsent(cv, LASTNAME, lastName());
        putIfNotAbsent(cv, MINIGAMEID, miniGameId());
        putIfNotAbsent(cv, NICKNAME, nickName());
        putIfNotAbsent(cv, PANCAKEORWAFFLE, pancakeOrWaffle());
        putIfNotAbsent(cv, PUSHDEVICETOKEN, pushDeviceToken());
        putIfNotAbsent(cv, PUSHENABLED, pushEnabled());
        putIfNotAbsent(cv, RACEID, raceId());
        putIfNotAbsent(cv, REFERRALTYPE, referralType());
        putIfNotAbsent(cv, TEAMID, teamId());
        putIfNotAbsent(cv, TOTALANTIMILES, totalAntiMiles());
        putIfNotAbsent(cv, TOTALDISTANCEMILES, totalDistanceMiles());
        putIfNotAbsent(cv, UPDATEDAT, ColumnAdapters.dateToLong(updatedAt()));
        return cv;
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
                null,
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
                null,
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

    public static User emptyUser() {
        return User.FACTORY.creator.create("", "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    // Needed by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<User> typeAdapter(Gson gson) {
        return new AutoValue_User.GsonTypeAdapter(gson);
    }
}
