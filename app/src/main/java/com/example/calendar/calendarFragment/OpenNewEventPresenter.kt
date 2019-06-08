package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.navigation.Screens
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import ru.terrakok.cicerone.Router

@InjectViewState
class OpenNewEventPresenter(private val router: Router) : MvpPresenter<OpenNewEventView>() {

    fun openFromTo(begin: ZonedDateTime, end: ZonedDateTime) {
        router.navigateTo(Screens.NewEventScreen(begin, end))
    }

    fun openOnDay(dayLocal: ZonedDateTime) {
        val start = dayLocal
            .truncatedTo(ChronoUnit.DAYS)
            .withHour(0)
        val end = start
            .plusHours(1)
        openFromTo(start, end)
    }

    // open fragment with current date
    fun openOnTodayDay() =
        openOnDay(ZonedDateTime.now(ZoneId.systemDefault()))

    fun openOnTime(timeLocal: ZonedDateTime) {
        val start = timeLocal.withMinute(0)
        val end = start.plusHours(1)
        openFromTo(start, end)
    }

}