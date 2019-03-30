package com.example.calendar

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import com.example.calendar.helpers.getCalendarWithUTF
import com.example.calendar.view.AbleAddEventView
import java.util.Calendar


@InjectViewState
class AbleAddEventPresenter : MvpPresenter<AbleAddEventView>() {

    fun addEventButtonClick(begin: Calendar, end: Calendar) {
        val fragment = CreateEventFragment.newInstance(begin, end)
        viewState.openFragment(fragment)
    }

    fun addEventButtonClick(commonDate: Calendar) {
        val local_start = getCalendarWithDefaultTimeZone()
        local_start.timeInMillis = commonDate.timeInMillis
        local_start.set(Calendar.HOUR_OF_DAY, 0)
        local_start.set(Calendar.MINUTE, 0)
        val local_end = local_start.clone() as Calendar
        local_end.set(Calendar.HOUR_OF_DAY, 1)

        val utf_start = getCalendarWithUTF()
        utf_start.timeInMillis = local_start.timeInMillis
        val uft_end = getCalendarWithUTF()
        uft_end.timeInMillis = local_end.timeInMillis

        addEventButtonClick(utf_start, uft_end)
    }

    fun addEventButtonClick() =
        addEventButtonClick(getCalendarWithDefaultTimeZone())


}