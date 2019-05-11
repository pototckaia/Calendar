package com.example.calendar.data

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime


class ZoneDateTimeConverter {
//    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    @TypeConverter
    fun toZoneDateTime(date: Long?): ZonedDateTime? {
        return date?.let {
            return ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneOffset.UTC)
        }
    }

    @TypeConverter
    fun fromZoneDateTime(date: ZonedDateTime?): Long? {
        val utc = date?.withZoneSameInstant(ZoneOffset.UTC)
        return utc?.toInstant()?.toEpochMilli()
    }
}
