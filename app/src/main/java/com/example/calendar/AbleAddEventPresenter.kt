package com.example.calendar

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.helpers.*
import com.example.calendar.view.OpenView
import java.util.Calendar


@InjectViewState
class AbleAddEventPresenter : MvpPresenter<OpenView>() {

    fun addEventButtonClick(begin: Calendar, end: Calendar) {
        val fragment = CreateEventFragment.newInstance(begin, end)
        viewState.openFragment(fragment)
    }

    fun addEventButtonClick(commonDate: Calendar) {
        val local_start = commonDate.cloneWithDefaultTimeZone()
        local_start.setHourOfDayAndMinute(0, 0)
        val local_end = local_start.clone() as Calendar
        local_end.set(Calendar.HOUR_OF_DAY, 1)
        addEventButtonClick(local_start, local_end)
    }

    fun addEventButtonClick() =
        addEventButtonClick(getCalendarWithDefaultTimeZone())


}