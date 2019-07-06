package com.example.calendar.repository.server.convert

import com.example.calendar.helpers.fromLongUTC
import com.example.calendar.helpers.toLongUTC
import com.google.gson.*
import org.threeten.bp.ZonedDateTime
import java.lang.reflect.Type


class ZonedDateTimeJsonConvert : JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    override fun serialize(src: ZonedDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return src?.let { return JsonPrimitive(toLongUTC(it)) }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ZonedDateTime? {
        return json?.let { return fromLongUTC(it.asLong) }
    }
}