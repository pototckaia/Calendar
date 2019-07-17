package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.*
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.EventInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

@InjectViewState
class ListEventPresenter(
    private val eventRepository: EventRepository
) : BaseMvpSubscribe<ListEventView>() {

    private val events = ArrayList<EventInstance>()
    var start : ZonedDateTime = ZonedDateTime.now()
        private set
    var end : ZonedDateTime = ZonedDateTime.now()
        private set

    constructor(
        r: EventRepository,
        s: ZonedDateTime, e: ZonedDateTime
    ) : this(r) {
        setStartAndEnd(s, e)
        loadEvents()
    }

    private fun setStartAndEnd(s: ZonedDateTime, e: ZonedDateTime) {
        start = ZonedDateTime.from(s)
        end = ZonedDateTime.from(e)
    }

    // todo not work on two click ???
    fun onDateSelected(day: ZonedDateTime) {
        start = day.truncatedTo(ChronoUnit.DAYS)
        end = start.endOfDay()

        unsubscribeOnAll()
        loadEvents()
    }

    fun getEvent(pos: Int): EventInstance {
        if (pos >= events.size) {
            throw IllegalArgumentException("Position greater than list size ${events.size}")
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
        events.addAll(rep.sortedWith(compareBy({ it.started_at }, { it.ended_at })))
        viewState.setEvents(events)
    }
}

