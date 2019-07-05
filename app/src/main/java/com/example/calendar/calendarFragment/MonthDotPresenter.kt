package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.repository.db.EventRecurrenceRepository
import com.example.calendar.helpers.BaseMvpSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.TemporalAdjusters
import kotlin.collections.HashSet


@InjectViewState
class MonthDotPresenter(
    private val eventRepository: EventRecurrenceRepository,
    private var curMonth: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
) :
    BaseMvpSubscribe<MonthDotView>() {

    private val dates = HashSet<ZonedDateTime>()

    init {
        loadEvents()
    }

    fun onMonthChange(month: ZonedDateTime) {
        unsubscribeOnAll()
        curMonth = month.withZoneSameInstant(ZoneId.systemDefault())
        loadEvents()
    }


    private fun loadEvents() {
        val monthStart = curMonth.with(TemporalAdjusters.firstDayOfMonth())
            .truncatedTo(ChronoUnit.DAYS)
        val monthEnd = curMonth.with(TemporalAdjusters.lastDayOfMonth())
            .truncatedTo(ChronoUnit.DAYS)
            .plusDays(1)

        val subscription = eventRepository.fromToSet(monthStart, monthEnd)
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
        viewState.showError(error.toString());
    }

    private fun onLoadingSuccess(rep: HashSet<ZonedDateTime>) {
        dates.clear()
        dates.addAll(rep)
        viewState.setMonthDots(dates)
    }

}
