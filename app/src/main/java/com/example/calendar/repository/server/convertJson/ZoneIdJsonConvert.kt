package com.example.calendar.repository.server.convertJson

import com.example.calendar.repository.zoneId_cn
import com.google.gson.*
import org.threeten.bp.ZoneId
import java.lang.reflect.Type


class ZoneIdJsonConvert: JsonSerializer<ZoneId>, JsonDeserializer<ZoneId> {

    override fun serialize(src: ZoneId?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return src?.let { return JsonPrimitive(zoneId_cn.fromZoneId(it)) }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ZoneId? {
        return json?.let { return zoneId_cn.toZoneId(it.asString) }
    }
}