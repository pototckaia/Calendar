package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.customView.EventWeekView
import com.example.calendar.data.EventInstance
import com.example.calendar.data.EventRecurrenceRepository
import com.example.calendar.helpers.BaseMvpSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.Duration
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.TemporalAdjusters
import kotlin.collections.ArrayList


@InjectViewState
class WeekEventPresenter(
    private val eventRepository: EventRecurrenceRepository,
    private val maxIntersection: Int,
    private val colorEvent: Int,
    private val colorIntersection: Int,
    private val colorFake: Int,
    private val titleFake: String
) : BaseMvpSubscribe<WeekEventView>() {

    private val events : ArrayList<EventWeekView> = arrayListOf()

    // todo period
    private val monthsLoad = HashSet<Pair<Int, Int>>()
    private var isFirstUpdate = true;

    fun onMonthChange(month: ZonedDateTime) : List<EventWeekView> {
        val pair = Pair(month.year, month.monthValue)

        val monthStart = month.with(TemporalAdjusters.firstDayOfMonth())
            .truncatedTo(ChronoUnit.DAYS)
        val monthEnd = month.with(TemporalAdjusters.lastDayOfMonth())
            .truncatedTo(ChronoUnit.DAYS)
            .plusDays(1)

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

    private fun isFromPeriod(it: EventWeekView, start: ZonedDateTime, end: ZonedDateTime) : Boolean {
        // (started_at >= :start and ended_at < :end) or (started_at < :end and ended_at > :start)
        return (it.event.startedAtLocal >= start && it.event.endedAtLocal < end) ||
                (it.event.startedAtLocal < end && it.event.endedAtLocal > start)
    }

    private fun loadEvents(monthStart: ZonedDateTime, monthEnd: ZonedDateTime) {
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

    private fun onLoadingSuccess(rep: List<EventInstance>, startDuration: ZonedDateTime, endDuration: ZonedDateTime) {
        // remove all what load from with period
        events.removeAll { isFromPeriod(it, startDuration, endDuration) }

        val sortEvent = rep.sortedWith(compareBy({ it.startedAtLocal }, { it.endedAtLocal }))

        var startIntersection = ZonedDateTime.now(ZoneId.systemDefault())
        var endIntersection = ZonedDateTime.now(ZoneId.systemDefault())
        val intersection = arrayListOf<EventInstance>()

        for (i in 0 until sortEvent.size) {
            if (i == 0) {
                startIntersection = ZonedDateTime.from(sortEvent[i].startedAtLocal)
                endIntersection = ZonedDateTime.from(sortEvent[i].endedAtLocal)
                intersection.add(sortEvent[i])
            } else {
                // if intersection
                if (sortEvent[i].startedAtLocal < endIntersection) {
                    intersection.add(sortEvent[i])
                    // max end
                    if (sortEvent[i].endedAtLocal > endIntersection) {
                        endIntersection = ZonedDateTime.from(sortEvent[i].endedAtLocal)
                    }
                }
                // not intersection
                else {
                    events.addAll( filterIntersection(intersection, startIntersection, endIntersection) )
                    // clear
                    intersection.clear()
                    intersection.add(sortEvent[i])
                    startIntersection = ZonedDateTime.from(sortEvent[i].startedAtLocal)
                    endIntersection = ZonedDateTime.from(sortEvent[i].endedAtLocal)
                }
            }
        }

        if (intersection.isNotEmpty()) {
            events.addAll(filterIntersection(intersection, startIntersection, endIntersection))
        }

        viewState.notifySetChanged()
    }

    private fun filterIntersection(
        inter: List<EventInstance>,
        startIntersection: ZonedDateTime, endIntersection: ZonedDateTime): List<EventWeekView>
    {
        val res = arrayListOf<EventWeekView>()

        if (inter.size == 1) {
            res.add(EventWeekView(inter[0], false, colorEvent))
        } else if (inter.size <= maxIntersection) {
            inter.forEach { res.add(EventWeekView(it, false, colorIntersection)) }
        } else {
            val ends = arrayListOf<ZonedDateTime>()
            var isAddFake = false;
            for (j in 0 until inter.size) {
                if (ends.size == maxIntersection - 1) {
                    val iMin = ends.withIndex().minBy { it.value }?.index!!
                    if (inter[j].startedAtLocal < ends[iMin]) {
                        isAddFake = true
                    } else {
                        ends[iMin] = ZonedDateTime.from(inter[j].endedAtLocal)
                        res.add(EventWeekView(inter[j], false, colorIntersection))
                    }
                } else {
                    ends.add(ZonedDateTime.from(inter[j].endedAtLocal))
                    res.add(EventWeekView(inter[j], false, colorIntersection))
                }
            }

            if (isAddFake) {
                val eventFake = EventInstance(
                    idEventRecurrence = "",
                    nameEventRecurrence= titleFake,
                    noteEventRecurrence = "",
                    startedAtInstance = startIntersection,
                    startedAtNotUpdate = startIntersection,
                    zoneId = startIntersection.zone,
                    duration = Duration.between(startIntersection, endIntersection),
                    rrule = ""
                )
                res.add(EventWeekView(eventFake, true, colorFake))
            }
        }

        return res
    }

}
