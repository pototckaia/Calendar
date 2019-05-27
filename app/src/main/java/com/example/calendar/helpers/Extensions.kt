package com.example.calendar.helpers

import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.dmfs.rfc5545.DateTime
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.util.*


fun ViewGroup.inflate(
    @LayoutRes layoutRes: Int,
    attachToRoot: Boolean = false
): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}


fun max(d1: ZonedDateTime, d2: ZonedDateTime): ZonedDateTime {
    return if (d1.isAfter(d2)) d1 else d2
}

fun min(d1: ZonedDateTime, d2: ZonedDateTime): ZonedDateTime {
    return if (d1.isBefore(d2)) d1 else d2
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

fun withYearMonthDay(
    d: ZonedDateTime,
    year: Int, monthOfYear: Int, dayOfMonth: Int
): ZonedDateTime {
    return d.withYear(year)
        .withMonth(monthOfYear)
        .withDayOfMonth(dayOfMonth)
}

fun withYearMonthDayTruncate(
    d: ZonedDateTime,
    year: Int, monthOfYear: Int, dayOfMonth: Int
): ZonedDateTime {
    return withYearMonthDay(d, year, monthOfYear, dayOfMonth)
        .truncatedTo(ChronoUnit.DAYS)
}

fun withHourMinuteTruncate(
    d: ZonedDateTime,
    hour: Int, minute: Int
) : ZonedDateTime {
    return d.withHour(hour)
        .withMinute(minute)
        .truncatedTo(ChronoUnit.MINUTES)
}

fun isSameDay(d1: ZonedDateTime, d2: ZonedDateTime): Boolean {
    return d1.truncatedTo(ChronoUnit.DAYS) == d2.truncatedTo(ChronoUnit.DAYS)
}

fun lessDay(d1: ZonedDateTime, d2: ZonedDateTime): Boolean {
    return d1.truncatedTo(ChronoUnit.DAYS) < d2.truncatedTo(ChronoUnit.DAYS)
}

fun moreDay(d1: ZonedDateTime, d2: ZonedDateTime): Boolean {
    return d1.truncatedTo(ChronoUnit.DAYS) > d2.truncatedTo(ChronoUnit.DAYS)
}

//"HH:mm"
fun getStringDiff(
    d1: ZonedDateTime, d2: ZonedDateTime,
    pattern: String, sep: String = "-"
): String {
    val f = DateTimeFormatter.ofPattern(pattern)
    return "${d1.format(f)} $sep ${d2.format(f)}"
}

fun getStringDayDiff(
    d1: ZonedDateTime, d2: ZonedDateTime,
    pD: String = "dd", pM: String = "MMMM", pY: String = "yyyy"
): String {
    val fmtYear = DateTimeFormatter.ofPattern(pY)
    val fmtMonth = DateTimeFormatter.ofPattern("$pM $pY")
    val fmtDay = DateTimeFormatter.ofPattern("$pD $pM $pY")


    if (d1.year != d2.year) {
        return getStringDiff(d1, d2, "$pD $pM $pY")
    }

    if (d1.month != d2.month) {
        return getStringDiff(d1, d2, "$pD $pM ") + d1.format(fmtYear)
    }

    if (d1.dayOfMonth != d2.dayOfMonth) {
        return getStringDiff(d1, d2, "$pD ") + d1.format(fmtMonth)
    }

    return d1.format(fmtDay)

}