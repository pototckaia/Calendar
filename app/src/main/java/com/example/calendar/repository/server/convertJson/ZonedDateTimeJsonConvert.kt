package com.example.calendar.repository.server.convertJson

import com.example.calendar.helpers.fromLongUTC
import com.example.calendar.helpers.toLongUTC
import com.example.calendar.repository.zonedDateTime_cn
import com.google.gson.*
import org.threeten.bp.ZonedDateTime
import java.lang.reflect.Type


class ZonedDateTimeJsonConvert : JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    override fun serialize(src: ZonedDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return src?.let { return JsonPrimitive(zonedDateTime_cn.fromZonedDateTime(it)) }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ZonedDateTime? {
        return json?.let { return zonedDateTime_cn.toZonedDateTime(it.asLong) }
    }
}