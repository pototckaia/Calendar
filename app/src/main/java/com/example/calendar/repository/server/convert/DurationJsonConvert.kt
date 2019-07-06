package com.example.calendar.repository.server.convert

import com.google.gson.*
import org.threeten.bp.Duration
import org.threeten.bp.temporal.ChronoUnit
import java.lang.reflect.Type


class DurationJsonConvert: JsonSerializer<Duration>, JsonDeserializer<Duration> {

    override fun serialize(src: Duration?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return src?.let { return JsonPrimitive(it.toMillis()) }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Duration? {
        return json?.let { return Duration.ofMillis(it.asLong) }
    }
}