package com.example.calendar.helpers

import android.provider.CalendarContract
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Flowable
import io.reactivex.Observable
import java.text.SimpleDateFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.dmfs.rfc5545.DateTime
import org.reactivestreams.Subscriber
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
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

// todo remove

fun getCalendarWithDefaultTimeZone(): Calendar =
    Calendar.getInstance(TimeZone.getDefault());

fun Calendar.cloneWithTimeZone() : Calendar {
    val c = this.clone() as Calendar
    c.timeZone = this.timeZone
    return c
}

fun Calendar.cloneWithDefaultTimeZone(): Calendar {
    val c = this.clone() as Calendar
    c.timeZone = TimeZone.getDefault()
    return c
}

fun Calendar.setYearMonthDay(c: Calendar) {
    this.set(Calendar.YEAR, c.get(Calendar.YEAR))
    this.set(Calendar.MONTH, c.get(Calendar.MONTH))
    this.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH))
}

fun Calendar.setHourOfDayAndMinute(hourOfDay: Int, minute: Int) {
    this.set(Calendar.HOUR_OF_DAY, hourOfDay)
    this.set(Calendar.MINUTE, minute)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
}

fun Calendar.eqDay(c: Calendar) : Boolean {
    return this.get(Calendar.YEAR) == c.get(Calendar.YEAR) &&
            this.get(Calendar.MONTH) == c.get(Calendar.MONTH) &&
            this.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH)
}

fun Calendar.lessDay(day: Calendar): Boolean {
    val d = day.cloneWithDefaultTimeZone()
    d.setHourOfDayAndMinute(0, 0)
    return this < d
}

fun Calendar.greaterDay(day: Calendar): Boolean {
    val d = day.cloneWithDefaultTimeZone()
    d.setHourOfDayAndMinute(24, 0)
    return this >= d
}

//"HH:mm"
fun getDiff(s: Calendar, e: Calendar, pattern: String, sep: String="-"): String {
    val fmtHour = SimpleDateFormat(pattern, Locale.getDefault())
    return "${fmtHour.format(s.time)} $sep ${fmtHour.format(e.time)}"
}

fun getDayDiff(s: Calendar, e: Calendar, pD: String="dd", pM: String="MMMM", pY: String="yyyy"): String {
    val fmtYear = SimpleDateFormat(pY, Locale.getDefault())
    val fmtMonth = SimpleDateFormat("$pM $pY", Locale.getDefault())
    val fmtDay = SimpleDateFormat("$pD $pM $pY", Locale.getDefault())

    if (s.get(Calendar.YEAR) != e.get(Calendar.YEAR)) {
       return getDiff(s, e, "$pD $pM $pY")
    } else if (s.get(Calendar.MONTH) != e.get(Calendar.MONTH)) {
        return getDiff(s, e, "$pD $pM ") + fmtYear.format(s.time)
    } else if (s.get(Calendar.DAY_OF_MONTH) != e.get(Calendar.DAY_OF_MONTH)) {
        return getDiff(s, e, "$pD ") + fmtMonth.format(s.time)
    } else {
        return fmtDay.format(s.time)
    }

}