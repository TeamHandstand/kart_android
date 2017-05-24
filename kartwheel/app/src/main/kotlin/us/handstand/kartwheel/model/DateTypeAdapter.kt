package us.handstand.kartwheel.model


import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

import java.lang.reflect.Type
import java.util.Date

import us.handstand.kartwheel.util.DateFormatter

class DateTypeAdapter : JsonDeserializer<Date>, JsonSerializer<Date> {
    private val gson = Gson()

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date {
        return DateFormatter[json.asString]!!
    }

    override fun serialize(src: Date?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return if (src == null) JsonObject() else gson.toJsonTree(DateFormatter.getString(src))
    }
}
