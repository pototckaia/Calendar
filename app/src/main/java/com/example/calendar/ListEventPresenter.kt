package com.example.calendar

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.data.EventRepository
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.*
import com.example.calendar.view.ListEventView
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*


@InjectViewState
class ListEventPresenter(private val eventRepository: EventRepository) :
    BaseMvpSubscribe<ListEventView>() {

    private val start = getCalendarWithUTF()
    private val end = getCalendarWithUTF()

    init {
        // default now
        start.setHourOfDayAndMinute(0, 0)
        // 24 hour
        end.setHourOfDayAndMinute(0, 0)
        end.add(Calendar.DAY_OF_MONTH, 1)
        updateCurrentDate()
        loadEvents()
    }

    constructor(e: EventRepository, startTime: Long, endTime: Long) : this(e) {
        start.timeInMillis = startTime
        end.timeInMillis = endTime
    }

    // todo not work on two click
    fun onDateSelected(local: Calendar) {
        val uft = local.cloneWitUTF()
        start.setYearMonthDay(uft)
        start.setHourOfDayAndMinute(0, 0)
        end.setYearMonthDay(uft)
        end.setHourOfDayAndMinute(0, 0)
        end.add(Calendar.DAY_OF_MONTH, 1)
        loadEvents()
    }

    private fun updateCurrentDate() {
        // convert to local
        val localStart = start.cloneWithDefaultTimeZone()
        val localEnd = end.cloneWithDefaultTimeZone()
        viewState.setCurrentDate(localStart, localEnd)
    }

    private fun loadEvents() {
        val subscription = eventRepository.fromTo(start, end)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ repositories ->
                onLoadingSuccess(repositories)
            }, { error ->
                onLoadingFailed(error)
            });
        unsubscribeOnDestroy(subscription)
    }

    private fun onLoadingFailed(error: Throwable) {
        viewState.showError(error.toString());
    }

    private fun onLoadingSuccess(rep: List<EventTable>) {
        viewState.setEvents(rep)
    }
}

