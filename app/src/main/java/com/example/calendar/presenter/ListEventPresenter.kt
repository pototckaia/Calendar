package com.example.calendar.presenter

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.EditEventFragment
import com.example.calendar.data.EventRepository
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.*
import com.example.calendar.view.ListEventView
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*


@InjectViewState
class ListEventPresenter(private val eventRepository: EventRepository) :
    BaseMvpSubscribe<ListEventView>() {

    private val start = getCalendarWithDefaultTimeZone()
    private val end = getCalendarWithDefaultTimeZone()
    private val listEvent = ArrayList<EventTable>()

    init {
        setHourAndMinute()
        updateCurrentDate()
        loadEvents()
    }

    constructor(e: EventRepository, startTime: Long, endTime: Long) : this(e) {
        start.timeInMillis = startTime
        end.timeInMillis = endTime
    }

    private fun setHourAndMinute(){
        start.setHourOfDayAndMinute(0, 0)
        end.setHourOfDayAndMinute(0, 0)
        end.add(Calendar.DAY_OF_MONTH, 1)
    }

    // todo not work on two click
    fun onDateSelected(local: Calendar) {
        start.setYearMonthDay(local)
        end.setYearMonthDay(local)
        setHourAndMinute()
        loadEvents()
    }

    fun openEvent(pos: Int) {
        if (pos > listEvent.size) {
            throw IllegalArgumentException("Event not exist")
        }
        val fragment = EditEventFragment.newInstance(listEvent[pos].id)
        viewState.openFragment(fragment)
    }

    private fun updateCurrentDate() {
        viewState.setCurrentDate(start, end)
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
        listEvent.clear()
        listEvent.addAll(rep)
        viewState.setEvents(rep)
    }
}

