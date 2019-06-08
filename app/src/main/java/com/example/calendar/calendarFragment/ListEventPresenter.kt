package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.EventInstance
import com.example.calendar.data.EventRecurrenceRepository
import com.example.calendar.helpers.*
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

@InjectViewState
class ListEventPresenter(
    private val eventRepository: EventRecurrenceRepository) :
    BaseMvpSubscribe<ListEventView>() {

    private val events = ArrayList<EventInstance>()
    private var start = ZonedDateTime.now(ZoneId.systemDefault())
    private var end = ZonedDateTime.now(ZoneId.systemDefault())

    constructor(r: EventRecurrenceRepository, s: ZonedDateTime, e: ZonedDateTime):
            this(r) {
        setStartAndEnd(s, e)
        loadEvents()
    }

    private fun setStartAndEnd(s: ZonedDateTime, e: ZonedDateTime) {
        start = s.withZoneSameInstant(ZoneId.systemDefault())
        end = e.withZoneSameInstant(ZoneId.systemDefault())
    }

    // todo not work on two click ???
    fun onDateSelected(day: ZonedDateTime) {
        val dayLocal = day.withZoneSameInstant(ZoneId.systemDefault())
        start = dayLocal.truncatedTo(ChronoUnit.DAYS)
        end = start.plusDays(1)

        unsubscribeOnAll()
        loadEvents()
    }

    fun getEvent(pos: Int): EventInstance {
        if (pos >= events.size) {
            throw IllegalArgumentException("Position greater list size ${events.size}")
        }
        return events[pos]
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
                })
        unsubscribeOnDestroy(subscription)
    }

    private fun onLoadingFailed(error: Throwable) {
        viewState.showError(error.toString())
    }

    private fun onLoadingSuccess(rep: List<EventInstance>) {
        events.clear()
        events.addAll(rep)
        viewState.setEvents(rep)
    }
}

