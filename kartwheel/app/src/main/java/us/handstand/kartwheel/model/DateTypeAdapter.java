package us.handstand.kartwheel.model;


import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;

import us.handstand.kartwheel.util.DateFormatter;

public class DateTypeAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {
    private final Gson gson = new Gson();

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return DateFormatter.INSTANCE.get(json.getAsString());
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? new JsonObject() : gson.toJsonTree(DateFormatter.INSTANCE.getString(src));
    }
}
