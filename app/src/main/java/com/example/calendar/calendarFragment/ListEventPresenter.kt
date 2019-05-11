package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.oldEvent.EventRepository
import com.example.calendar.data.oldEvent.EventTable
import com.example.calendar.helpers.*
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*


@InjectViewState
class ListEventPresenter(
    private val eventRepository: EventRepository
) :
    BaseMvpSubscribe<ListEventView>() {

    private val start = getCalendarWithDefaultTimeZone()
    private val end = getCalendarWithDefaultTimeZone()
    private val events = ArrayList<EventTable>()

    constructor(e: EventRepository, startTime: Long, endTime: Long) : this(e) {
        start.timeInMillis = startTime
        end.timeInMillis = endTime
        loadEvents()
    }

    // todo not work on two click ???
    fun onDateSelected(date: Calendar) {
        start.setYearMonthDay(date)
        end.setYearMonthDay(date)
        roundHourAndMinute()
        loadEvents()
    }

    fun getId(pos: Int): String {
        if (pos >= events.size) {
            // todo hardcore
            throw IllegalArgumentException("Position greater list size ${events.size}")
        }
        return events[pos].id
    }


    private fun roundHourAndMinute() {
        start.setHourOfDayAndMinute(0, 0)
        end.setHourOfDayAndMinute(0, 0)
        end.add(Calendar.DAY_OF_MONTH, 1)
    }

    private fun loadEvents() {
        val subscription = eventRepository.fromTo(start, end)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { repositories ->
                    onLoadingSuccess(repositories)
                },
                { error ->
                    onLoadingFailed(error)
                });
        unsubscribeOnDestroy(subscription)
    }

    private fun onLoadingFailed(error: Throwable) {
        viewState.showError(error.toString());
    }

    private fun onLoadingSuccess(rep: List<EventTable>) {
        events.clear()
        events.addAll(rep)
        viewState.setEvents(rep)
    }
}

