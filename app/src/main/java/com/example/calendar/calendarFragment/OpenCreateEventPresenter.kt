package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.eventFragment.CreateEventFragment
import com.example.calendar.helpers.*
import com.example.calendar.remove.OpenView
import java.util.Calendar


@InjectViewState
class OpenCreateEventPresenter : MvpPresenter<OpenView>() {

    fun openEventFragment(begin: Calendar, end: Calendar) {
        val fragment = CreateEventFragment.newInstance(begin, end)
        viewState.openFragment(fragment)
    }

    fun openEventFragment(commonDate: Calendar) {
        val local_start = commonDate.cloneWithDefaultTimeZone()
        local_start.setHourOfDayAndMinute(0, 0)
        val local_end = local_start.clone() as Calendar
        local_end.set(Calendar.HOUR_OF_DAY, 1)
        openEventFragment(local_start, local_end)
    }

    fun openEventFragment() =
        openEventFragment(getCalendarWithDefaultTimeZone())


}