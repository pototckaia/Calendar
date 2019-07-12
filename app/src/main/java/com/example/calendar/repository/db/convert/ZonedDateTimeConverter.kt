package com.example.calendar.repository.db.convert

import androidx.room.TypeConverter
import com.example.calendar.helpers.fromLongUTC
import com.example.calendar.helpers.toLongUTC
import org.threeten.bp.ZonedDateTime


class ZonedDateTimeConverter {
//    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    @TypeConverter
    fun toZonedDateTime(date: Long?): ZonedDateTime? {
        return date?.let {
            return fromLongUTC(date)
        }
    }

    @TypeConverter
    fun fromZonedDateTime(date: ZonedDateTime?): Long? {
        return date?.let {
            return toLongUTC(date)
        }
    }
}
