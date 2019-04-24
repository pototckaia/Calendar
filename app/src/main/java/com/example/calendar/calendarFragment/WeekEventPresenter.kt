package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.data.EventRepository
import com.example.calendar.data.EventTable
import com.example.calendar.customView.EventWeekView
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import com.example.calendar.helpers.setHourOfDayAndMinute
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import kotlin.collections.ArrayList


@InjectViewState
class WeekEventPresenter(
    private val eventRepository: EventRepository,
    private val colorEvent: Int,
    private val colorFake: Int
) : BaseMvpSubscribe<WeekEventView>() {

    private val events : ArrayList<EventWeekView> = arrayListOf()

    private val monthsLoad = HashSet<Pair<Int, Int>>()
    private var isFirstUpdate = true;


    fun onMonthChange(month: Calendar) : List<EventWeekView> {
        val pair = Pair(month.get(Calendar.YEAR), month.get(Calendar.MONTH))

        val monthStart = getCalendarWithDefaultTimeZone()
        monthStart.timeInMillis = month.timeInMillis
        monthStart.set(Calendar.DAY_OF_MONTH, 1)
        monthStart.setHourOfDayAndMinute(0, 0)

        val monthEnd = monthStart.clone() as Calendar
        monthEnd.set(Calendar.DATE, monthStart.getActualMaximum(Calendar.DATE))
        monthEnd.setHourOfDayAndMinute(24, 0)

        if (isFirstUpdate || !isLoad(pair)) {
            isFirstUpdate = false
            monthsLoad.add(pair)
            loadEvents(monthStart, monthEnd);
        }

        return events.filter { isFromPeriod(it, monthStart, monthEnd) }
    }

    private fun isLoad(yearAndMonth: Pair<Int, Int>) : Boolean {
        return monthsLoad.contains(yearAndMonth)
    }

    private fun isFromPeriod(it: EventWeekView, start: Calendar, end: Calendar) : Boolean {
        // (started_at >= :start and ended_at < :end) or (started_at < :end and ended_at > :start)
        return (it.event.started_at >= start && it.event.ended_at < end) ||
                (it.event.started_at < end && it.event.ended_at > start)
    }

    private fun loadEvents(monthStart: Calendar, monthEnd: Calendar) {
        onLoadingStart()
        val subscription = eventRepository.fromTo(monthStart, monthEnd)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { repositories ->
                    onLoadingStop()
                    onLoadingSuccess(repositories, monthStart, monthEnd)
                },
                { error ->
                    onLoadingStop()
                    onLoadingFailed(error)
                });
        unsubscribeOnDestroy(subscription)
    }

    private fun onLoadingStart() {
        viewState.showLoading()
    }

    private fun onLoadingStop() {
        viewState.closeLoading()
    }

    private fun onLoadingFailed(error: Throwable) {
        viewState.showError(error.toString());
    }

    private fun onLoadingSuccess(rep: List<EventTable>, start: Calendar, end: Calendar) {
        // remove all what load from with period
        events.removeAll { isFromPeriod(it, start, end) }

        rep.forEach {
            events.add(EventWeekView(it, colorEvent))
        }
        viewState.notifySetChanged()
    }

}
