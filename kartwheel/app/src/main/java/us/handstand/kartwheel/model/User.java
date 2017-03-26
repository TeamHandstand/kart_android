package us.handstand.kartwheel.model;


import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class User implements UserModel {
    public static final Factory<User> FACTORY = new Factory<>(AutoValue_User::new);
    public static final RowMapper<User> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

    // Needed by Gson
    @SuppressWarnings("unused")
    public static TypeAdapter<User> typeAdapter(Gson gson) {
        return new AutoValue_User.GsonTypeAdapter(gson);
    }
}
