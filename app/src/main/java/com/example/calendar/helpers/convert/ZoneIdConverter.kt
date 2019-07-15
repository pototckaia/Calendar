package com.example.calendar.helpers.convert

import androidx.room.TypeConverter
import org.threeten.bp.ZoneId


class ZoneIdConverter {
    @TypeConverter
    fun toZoneId(l : String?): ZoneId? {
        if (l == null) {return null }
        return ZoneId.of(l)
    }

    @TypeConverter
    fun fromZoneId(d: ZoneId?) : String? {
        return d?.toString()
    }

}