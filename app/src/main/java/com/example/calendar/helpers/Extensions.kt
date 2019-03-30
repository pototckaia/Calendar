package com.example.calendar.helpers

import android.provider.CalendarContract
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.text.SimpleDateFormat
import java.util.*

fun ViewGroup.inflate(
    @LayoutRes layoutRes: Int,
    attachToRoot: Boolean = false
): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun getCalendarWithUTF() : Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

fun getCalendarWithDefaultTimeZone() : Calendar = Calendar.getInstance(TimeZone.getDefault());

fun Calendar.cloneWithDefaultTimeZone(): Calendar {
    val c = this.clone() as Calendar
    c.timeZone = TimeZone.getDefault()
    return c
}