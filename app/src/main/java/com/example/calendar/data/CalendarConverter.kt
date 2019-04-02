package com.example.calendar.data

import androidx.room.TypeConverter
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import java.util.*

class CalendarConverter {
    @TypeConverter
    fun toCalendar(dateLong: Long?): Calendar? {
        if (dateLong == null) {
            return null
        }
        val utf = getCalendarWithDefaultTimeZone()
        utf.timeInMillis = dateLong
        return utf
    }

    @TypeConverter
    fun fromCalendar(date: Calendar?): Long? {
        return date?.timeInMillis
    }
}