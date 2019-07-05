package com.example.calendar.repository.db.convert

import androidx.room.TypeConverter
import org.threeten.bp.Duration


class DurationConverter {
    @TypeConverter
    fun toDuration(l : Long?): Duration? {
        if (l == null) {return null }
        return Duration.ofMinutes(l)
    }

    @TypeConverter
    fun fromDuration(d: Duration?) : Long? {
        return d?.toMinutes()
    }

}