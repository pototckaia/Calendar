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

fun getCalendarWithUTF() : Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

fun getCalendarWithDefaultTimeZone() : Calendar = Calendar.getInstance(TimeZone.getDefault());

fun Calendar.cloneWithDefaultTimeZone(): Calendar {
    val c = this.clone() as Calendar
    c.timeZone = TimeZone.getDefault()
    return c
}

fun <T> wrapAsync(observable: Observable<T>, scheduler: Scheduler = Schedulers.io()): Observable<T> {
    return observable
        .subscribeOn(scheduler)
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> wrapAsync(observable: Flowable<T>, scheduler: Scheduler = Schedulers.io()): Flowable<T> {
    return observable
        .subscribeOn(scheduler)
        .observeOn(AndroidSchedulers.mainThread())
}