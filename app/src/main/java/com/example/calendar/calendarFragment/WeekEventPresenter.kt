package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.customView.EventWeekView
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.EventInstance
import com.example.calendar.repository.server.model.EventPatternServer
import com.example.calendar.repository.server.model.EventServer
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

    private val events : ArrayList<EventWeekView> = arrayListOf()

    // todo period
    private val monthsLoad = HashSet<Pair<Int, Int>>()
    private var isFirstUpdate = true;

    fun onMonthChange(month: ZonedDateTime) : List<EventWeekView> {
        val pair = Pair(month.year, month.monthValue)

        val monthStart = month.with(TemporalAdjusters.firstDayOfMonth())
            .truncatedTo(ChronoUnit.DAYS)
        // todo [end]
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

    // todo [end]
    private fun isFromPeriod(it: EventWeekView, start: ZonedDateTime, end: ZonedDateTime) : Boolean {
        // (started_at >= :start and ended_at < :end) or (started_at < :end and ended_at > :start)
        return (it.event.started_at_local >= start && it.event.ended_at_local < end) ||
                (it.event.started_at_local < end && it.event.ended_at_local > start)
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

    // todo error
    private fun onLoadingSuccess(rep: List<EventInstance>, startDuration: ZonedDateTime, endDuration: ZonedDateTime) {
        // remove all what load from with period
        events.removeAll { isFromPeriod(it, startDuration, endDuration) }

        // todo local or timezone
        val sortEvent = rep.sortedWith(compareBy({ it.started_at_zoneid }, { it.ended_at_zoneid }))

        var startIntersection = ZonedDateTime.now(ZoneId.systemDefault())
        // todo [end]
        var endIntersection = ZonedDateTime.now(ZoneId.systemDefault())
        val intersection = arrayListOf<EventInstance>()

        for (i in 0 until sortEvent.size) {
            if (i == 0) {
                startIntersection = ZonedDateTime.from(sortEvent[i].started_at_zoneid)
                endIntersection = ZonedDateTime.from(sortEvent[i].ended_at_zoneid)
                intersection.add(sortEvent[i])
            } else {
                // if intersection
                if (sortEvent[i].started_at_zoneid < endIntersection) {
                    intersection.add(sortEvent[i])
                    // max end
                    if (sortEvent[i].ended_at_zoneid > endIntersection) {
                        endIntersection = ZonedDateTime.from(sortEvent[i].ended_at_zoneid)
                    }
                }
                // not intersection
                else {
                    events.addAll( filterIntersection(intersection, startIntersection, endIntersection) )
                    // clear
                    intersection.clear()
                    intersection.add(sortEvent[i])
                    startIntersection = ZonedDateTime.from(sortEvent[i].started_at_zoneid)
                    endIntersection = ZonedDateTime.from(sortEvent[i].ended_at_zoneid)
                }
            }
        }

        if (intersection.isNotEmpty()) {
            events.addAll(filterIntersection(intersection, startIntersection, endIntersection))
        }

        viewState.notifySetChanged()
    }

    // todo [end]
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
                    if (inter[j].started_at_zoneid < ends[iMin]) {
                        isAddFake = true
                    } else {
                        ends[iMin] = ZonedDateTime.from(inter[j].ended_at_zoneid)
                        res.add(EventWeekView(inter[j], false, colorIntersection))
                    }
                } else {
                    ends.add(ZonedDateTime.from(inter[j].ended_at_zoneid))
                    res.add(EventWeekView(inter[j], false, colorIntersection))
                }
            }

            if (isAddFake) {
                val durationFake = Duration.between(startIntersection, endIntersection)
                // todo a lot of code
                val eventFake = EventInstance(
                    entity = EventServer(
                        id = -1,
                        owner_id = -1,
                        created_at = ZonedDateTime.now(),
                        updated_at = ZonedDateTime.now(),
                        name = titleFake,
                        details = "",
                        status = "",
                        location = ""
                    ),
                    pattern = EventPatternServer(
                        id = -1,
                        created_at = ZonedDateTime.now(),
                        updated_at = ZonedDateTime.now(),
                        started_at = startIntersection,
                        ended_at = endIntersection,
                        duration = durationFake,
                        exrules = emptyList(),
                        rrule = "",
                        timezone = startIntersection.zone
                    ),
                    started_at = startIntersection,
                    ended_at = endIntersection
                )
                res.add(EventWeekView(eventFake, true, colorFake))
            }
        }

        return res
    }

}
