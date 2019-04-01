package com.example.calendar

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.data.EventRepository
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import com.example.calendar.helpers.getCalendarWithUTF
import com.example.calendar.helpers.wrapAsync
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
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        end.set(Calendar.HOUR_OF_DAY, 24)
        end.set(Calendar.MINUTE, 0)

        updateCurrentDate()
        loadEvents()
    }

    constructor(e: EventRepository, startTime: Long, endTime: Long) : this(e) {
        start.timeInMillis = startTime
        end.timeInMillis = endTime
    }

    private fun updateCurrentDate() {
        // convert to local
        val localStart = getCalendarWithDefaultTimeZone()
        localStart.timeInMillis = start.timeInMillis
        val localEnd = getCalendarWithDefaultTimeZone()
        localEnd.timeInMillis = end.timeInMillis
        viewState.setCurrentDate(localStart, localEnd)
    }

    private fun loadEvents() {
        val subscription = eventRepository.all
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ repositories ->
//                onLoadingFinish(isPageLoading, isRefreshing)
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

