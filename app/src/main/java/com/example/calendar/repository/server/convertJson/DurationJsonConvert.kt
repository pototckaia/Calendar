package com.example.calendar.repository.server.convertJson

import com.example.calendar.helpers.convert.duration_cn
import com.google.gson.*
import org.threeten.bp.Duration
import java.lang.reflect.Type


class DurationJsonConvert: JsonSerializer<Duration>, JsonDeserializer<Duration> {

    override fun serialize(src: Duration?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return src?.let { return JsonPrimitive(duration_cn.fromDuration(src)) }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Duration? {
        return json?.let { return duration_cn.toDuration(it.asLong) }
    }
}