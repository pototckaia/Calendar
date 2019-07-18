package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.helpers.endOfDay
import com.example.calendar.repository.server.EventRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.TemporalAdjusters
import kotlin.collections.HashSet


@InjectViewState
class MonthDotPresenter(
    private val eventRepository: EventRepository,
    private var curMonth: ZonedDateTime = ZonedDateTime.now()
) : BaseMvpSubscribe<MonthDotView>() {

    private val dates = HashSet<ZonedDateTime>()

    init {
        loadEvents()
    }

    fun onMonthChange(month: ZonedDateTime) {
        unsubscribeOnAll()
        curMonth = ZonedDateTime.from(month)
        loadEvents()
    }


    private fun loadEvents() {
        val monthStart = curMonth
            .with(TemporalAdjusters.firstDayOfMonth())
            .truncatedTo(ChronoUnit.DAYS)
        val monthEnd = curMonth
            .with(TemporalAdjusters.lastDayOfMonth())
            .endOfDay()

        val subscription = eventRepository.fromToSet(monthStart, monthEnd)
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
        viewState.showError(error.toString());
    }

    private fun onLoadingSuccess(rep: HashSet<ZonedDateTime>) {
        dates.clear()
        dates.addAll(rep)
        viewState.setMonthDots(dates)
    }

}
