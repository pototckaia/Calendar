package com.example.calendar.repository.server.convert

import com.google.gson.*
import org.threeten.bp.ZoneId
import java.lang.reflect.Type


class ZoneIdJsonConvert: JsonSerializer<ZoneId>, JsonDeserializer<ZoneId> {

    override fun serialize(src: ZoneId?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return src?.let { return JsonPrimitive(it.toString()) }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ZoneId? {
        return json?.let { return ZoneId.of(it.asString) }
    }
}