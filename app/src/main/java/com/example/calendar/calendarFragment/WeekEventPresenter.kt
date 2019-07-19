package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.customView.EventWeekView
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.helpers.endOfDay
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.EventInstance
import com.example.calendar.repository.server.model.EventPatternServer
import com.example.calendar.repository.server.model.EventServer
import com.example.calendar.repository.server.model.UserServer
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.Duration
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.TemporalAdjusters
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

    private val events: ArrayList<EventWeekView> = arrayListOf()

    // todo period
    private val monthsLoad = HashSet<Pair<Int, Int>>()
    private var isFirstUpdate = true;

    fun onMonthChange(month: ZonedDateTime): List<EventWeekView> {
        val pair = Pair(month.year, month.monthValue)

        val monthStart = month
            .with(TemporalAdjusters.firstDayOfMonth())
            .truncatedTo(ChronoUnit.DAYS)
        val monthEnd = month
            .with(TemporalAdjusters.lastDayOfMonth())
            .endOfDay()

        if (isFirstUpdate || !isLoad(pair)) {
            isFirstUpdate = false
            monthsLoad.add(pair)
            loadEvents(monthStart, monthEnd);
        }

        return events.filter { isStartFromPeriod(it, monthStart, monthEnd) }
    }

    fun onStop() {
        monthsLoad.clear()
    }

    private fun isLoad(yearAndMonth: Pair<Int, Int>): Boolean {
        return monthsLoad.contains(yearAndMonth)
    }

    // [start, end]
    private fun isStartFromPeriod(it: EventWeekView, start: ZonedDateTime, end: ZonedDateTime): Boolean {
        return it.event.started_at_local in start..end
    }

    private fun isFromPeriod(it: EventWeekView, start: ZonedDateTime, end: ZonedDateTime): Boolean {
        return (it.event.started_at_local >= start && it.event.ended_at_local <= end) ||
                (it.event.started_at_local < end && it.event.ended_at_local > start)
    }

    private fun loadEvents(monthStart: ZonedDateTime, monthEnd: ZonedDateTime) {
        onLoadingStart()
        val subscription = eventRepository.fromTo(monthStart, monthEnd)
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

    // todo error
    private fun onLoadingSuccess(rep: List<EventInstance>, startDuration: ZonedDateTime, endDuration: ZonedDateTime) {
        // remove all what load from with period
        events.removeAll { isFromPeriod(it, startDuration, endDuration) }

        val sortEvent = rep.sortedWith(compareBy({ it.started_at_local }, { it.ended_at_local }))

        var startIntersection = ZonedDateTime.now()
        var endIntersection = ZonedDateTime.now()

        val intersection = arrayListOf<EventInstance>()

        for (i in 0 until sortEvent.size) {
            if (i == 0) {
                startIntersection = ZonedDateTime.from(sortEvent[i].started_at_local)
                endIntersection = ZonedDateTime.from(sortEvent[i].ended_at_local)
                intersection.add(sortEvent[i])
            } else {
                // if intersection
                if (sortEvent[i].started_at_local < endIntersection) {
                    intersection.add(sortEvent[i])
                    // max end
                    if (sortEvent[i].ended_at_local > endIntersection) {
                        endIntersection = ZonedDateTime.from(sortEvent[i].ended_at_local)
                    }
                }
                // not intersection
                else {
                    events.addAll(filterIntersection(intersection, startIntersection, endIntersection))
                    // clear
                    intersection.clear()
                    intersection.add(sortEvent[i])
                    startIntersection = ZonedDateTime.from(sortEvent[i].started_at_local)
                    endIntersection = ZonedDateTime.from(sortEvent[i].ended_at_local)
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
        startIntersection: ZonedDateTime, endIntersection: ZonedDateTime
    ): List<EventWeekView> {
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
                    if (inter[j].started_at_local < ends[iMin]) {
                        isAddFake = true
                    } else {
                        ends[iMin] = ZonedDateTime.from(inter[j].ended_at_local)
                        res.add(EventWeekView(inter[j], false, colorIntersection))
                    }
                } else {
                    ends.add(ZonedDateTime.from(inter[j].ended_at_local))
                    res.add(EventWeekView(inter[j], false, colorIntersection))
                }
            }

            if (isAddFake) {
                val durationFake = Duration.between(startIntersection, endIntersection)
                // todo a lot of code
                val eventFake = EventInstance(
                    entity = EventServer(
                        -1, "-1",
                        ZonedDateTime.now(), ZonedDateTime.now(),
                        titleFake, "", "", ""
                    ),
                    pattern = EventPatternServer(
                        -1,
                        ZonedDateTime.now(), ZonedDateTime.now(),
                        startIntersection, durationFake, endIntersection,
                        emptyList(), null, startIntersection.zone
                    ),
                    user = UserServer("-1", "-1"),
                    started_at = startIntersection,
                    ended_at = endIntersection
                )
                res.add(EventWeekView(eventFake, true, colorFake))
            }
        }

        return res
    }

}
