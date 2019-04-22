package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.EventRepository
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import com.example.calendar.helpers.setHourOfDayAndMinute
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import kotlin.collections.HashSet


@InjectViewState
class MonthDotPresenter(
    private val eventRepository: EventRepository,
    private val curMonth: Calendar = getCalendarWithDefaultTimeZone()
) :
    BaseMvpSubscribe<MonthDotView>() {

    private val dates = HashSet<Calendar>()

    init {
        loadEvents()
    }

    fun onMonthChange(month: Calendar) {
        curMonth.timeInMillis = month.timeInMillis
        loadEvents() // TODO how work unsubsribe
    }

    private fun loadEvents() {
        val monthStart = getCalendarWithDefaultTimeZone()
        monthStart.timeInMillis = curMonth.timeInMillis
        monthStart.set(Calendar.DAY_OF_MONTH, 1)
        monthStart.setHourOfDayAndMinute(0, 0)

        val monthEnd = monthStart.clone() as Calendar
        monthEnd.set(Calendar.DATE, monthStart.getActualMaximum(Calendar.DATE))
        monthEnd.setHourOfDayAndMinute(24, 0)

        val subscription = eventRepository.fromTo(monthStart, monthEnd)
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
        dates.clear()
        rep.forEach { it ->
            val c = it.started_at.clone() as Calendar
            while (c < it.ended_at) {
                dates.add(c.clone() as Calendar)
                c.add(Calendar.DAY_OF_MONTH, 1)
                c.setHourOfDayAndMinute(0, 0)
            }
        }
        viewState.setMonthDots(dates)
    }

}
