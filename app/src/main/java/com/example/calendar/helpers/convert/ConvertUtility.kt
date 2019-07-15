package com.example.calendar.helpers.convert

import com.example.calendar.helpers.convert.DurationConverter
import com.example.calendar.helpers.convert.ZoneIdConverter
import com.example.calendar.helpers.convert.ZonedDateTimeConverter
import org.dmfs.rfc5545.DateTime
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

val duration_cn = DurationConverter()
val zonedDateTime_cn = ZonedDateTimeConverter()
val zoneId_cn = ZoneIdConverter()

// convert time

fun toTimeZone(id: ZoneId): TimeZone {
    return DateTimeUtils.toTimeZone(id)
}

fun fromDateTime(d: DateTime): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(d.timestamp), DateTimeUtils.toZoneId(d.timeZone))
}

fun toDateTime(z: ZonedDateTime, timeZone: TimeZone): DateTime {
    return DateTime(timeZone, z.toInstant().toEpochMilli())
}

fun toDateTime(z: ZonedDateTime): DateTime {
    return DateTime(toTimeZone(z.zone), z.toInstant().toEpochMilli())
}

fun fromDateTimeUTC(d: DateTime): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(d.timestamp), ZoneOffset.UTC)
}

fun toDateTimeUTC(z: ZonedDateTime): DateTime {
    return DateTime(TimeZone.getTimeZone("UTC"), z.toInstant().toEpochMilli())
}

fun toLongUTC(z: ZonedDateTime): Long {
    val utc = z.withZoneSameInstant(ZoneOffset.UTC)
    return utc.toInstant().toEpochMilli()
}

fun fromLongUTC(l: Long): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneOffset.UTC)
}

fun toStringFromZoned(z: ZonedDateTime): String {
    val f = DateTimeFormatter.ISO_ZONED_DATE_TIME
    return z.format(f)
}

fun fromStringToZoned(s: String): ZonedDateTime {
    val f = DateTimeFormatter.ISO_ZONED_DATE_TIME
    return ZonedDateTime.parse(s, f)
}

fun toCalendar(s: ZonedDateTime): Calendar {
    return DateTimeUtils.toGregorianCalendar(s)
}

fun fromCalendar(s: Calendar): ZonedDateTime {
    return DateTimeUtils.toZonedDateTime(s)
}
