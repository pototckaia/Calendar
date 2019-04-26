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
import org.reactivestreams.Subscriber
import java.util.Calendar
import java.util.TimeZone


fun ViewGroup.inflate(
    @LayoutRes layoutRes: Int,
    attachToRoot: Boolean = false
): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun getCalendarWithDefaultTimeZone(): Calendar =
    Calendar.getInstance(TimeZone.getDefault());

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

fun Calendar.lessDay(day: Calendar) : Boolean {
    val d = day.cloneWithDefaultTimeZone()
    d.setHourOfDayAndMinute(0, 0)
    return this < d
}

fun Calendar.greaterDay(day: Calendar) : Boolean {
    val d = day.cloneWithDefaultTimeZone()
    d.setHourOfDayAndMinute(24, 0)
    return this >= d
}