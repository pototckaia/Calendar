package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.R
import com.example.calendar.data.EventRepository
import com.example.calendar.data.EventTable
import com.example.calendar.customView.EventWeekView
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.helpers.cloneWithDefaultTimeZone
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import com.example.calendar.helpers.setHourOfDayAndMinute
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import javax.security.auth.login.LoginException
import kotlin.collections.ArrayList


@InjectViewState
class WeekEventPresenter(
    private val eventRepository: EventRepository,
    private val maxIntersection: Int,
    private val colorEvent: Int,
    private val colorIntersection: Int,
    private val colorFake: Int,
    private val titleFake: String
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

    private fun onLoadingSuccess(rep: List<EventTable>, startDuration: Calendar, endDuration: Calendar) {
        // remove all what load from with period
        events.removeAll { isFromPeriod(it, startDuration, endDuration) }

        val sortEvent = rep.sortedWith(compareBy({ it.started_at }, { it.ended_at }))

        val startIntersection = getCalendarWithDefaultTimeZone()
        val endIntersection = getCalendarWithDefaultTimeZone()
        val intersection = arrayListOf<EventTable>()

        for (i in 0 until sortEvent.size) {
            if (i == 0) {
                startIntersection.timeInMillis = sortEvent[i].started_at.timeInMillis
                endIntersection.timeInMillis = sortEvent[i].ended_at.timeInMillis
                intersection.add(sortEvent[i])
            } else {
                // if intersection
                if (sortEvent[i].started_at < endIntersection) {
                    intersection.add(sortEvent[i])
                    // max end
                    if (sortEvent[i].ended_at > endIntersection) {
                        endIntersection.timeInMillis = sortEvent[i].ended_at.timeInMillis
                    }
                }
                // not intersection
                else {
                    events.addAll(filterIntersection(intersection, startIntersection, endIntersection))
                    // clear
                    intersection.clear()
                    intersection.add(sortEvent[i])
                    startIntersection.timeInMillis = sortEvent[i].started_at.timeInMillis
                    endIntersection.timeInMillis = sortEvent[i].ended_at.timeInMillis
                }
            }
        }

        if (!intersection.isEmpty()) {
            events.addAll(filterIntersection(intersection, startIntersection, endIntersection))
        }

        viewState.notifySetChanged()
    }

    private fun filterIntersection(
        inter: List<EventTable>,
        startIntersection: Calendar, endIntersection: Calendar): List<EventWeekView>
    {
        val res = arrayListOf<EventWeekView>()

        if (inter.size == 1) {
            res.add(EventWeekView(inter[0], false, colorEvent))
        } else if (inter.size <= maxIntersection) {
            inter.forEach { res.add(EventWeekView(it, false, colorIntersection)) }
        } else {
            val ends = arrayListOf<Calendar>()
            var isAddFake = false;
            for (j in 0 until inter.size) {
                if (ends.size == maxIntersection - 1) {
                    val iMin = ends.withIndex().minBy { it.value }?.index
                    if (inter[j].started_at <= ends[iMin!!]) {
                        isAddFake = true
                    } else {
                        ends[iMin].timeInMillis = inter[j].ended_at.timeInMillis
                        res.add(EventWeekView(inter[j], false, colorIntersection))
                    }
                } else {
                    ends.add(inter[j].ended_at.cloneWithDefaultTimeZone())
                    res.add(EventWeekView(inter[j], false, colorIntersection))
                }
            }

            if (isAddFake) {
                val eventFake = EventTable(
                    started_at = startIntersection.cloneWithDefaultTimeZone(),
                    ended_at = endIntersection.cloneWithDefaultTimeZone(),
                    name = titleFake
                )
                res.add(EventWeekView(eventFake, true, colorFake))
            }
        }

        return res
    }

}
