package us.handstand.kartwheel.model;


import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class User implements UserModel {
    public static final Factory<User> FACTORY = new Factory<>((_id, authToken, birth, cell, charmanderOrSquirtle, email, eventId, facetimeCount, firstName, furbyOrTamagachi, imageUrl, lastName, miniGameId, nickName, pancakeOrWaffle, pushDeviceToken, raceId, referralType, totalAntiMiles, totalDistanceMiles, updatedAt) -> new AutoValue_User(_id, authToken, birth, cell, charmanderOrSquirtle, email, eventId, facetimeCount, firstName, furbyOrTamagachi, imageUrl, lastName, miniGameId, nickName, pancakeOrWaffle, pushDeviceToken, raceId, referralType, totalAntiMiles, totalDistanceMiles, updatedAt));
    public static final RowMapper<User> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    // Needed by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<User> typeAdapter(Gson gson) {
        return new AutoValue_User.GsonTypeAdapter(gson);
    }
}
