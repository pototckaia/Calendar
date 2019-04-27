package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.helpers.*
import com.example.calendar.navigation.Screens
import ru.terrakok.cicerone.Router
import java.util.Calendar


@InjectViewState
class OpenNewEventPresenter(private val router: Router) : MvpPresenter<OpenNewEventView>() {

    fun openFromTo(begin: Calendar, end: Calendar) {
        router.navigateTo(Screens.NewEventScreen(begin, end))
    }

    fun openOnDay(day: Calendar) {
        val start = day.cloneWithDefaultTimeZone()
        start.setHourOfDayAndMinute(0, 0)
        val end = start.clone() as Calendar
        end.set(Calendar.HOUR_OF_DAY, 1)
        openFromTo(start, end)
    }

    // open fragment with current date
    fun openOnTodayDay() =
        openOnDay(getCalendarWithDefaultTimeZone())

    fun openOnTime(time: Calendar) {
        time.set(Calendar.MINUTE, 0)
        val end = time.clone() as Calendar
        end.add(Calendar.HOUR_OF_DAY, 1)
        openFromTo(time, end)
    }

}