package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import java.util.*

@InjectViewState
class CurrentDatePresenter() : MvpPresenter<CurrentDateView>() {

    private val curDate = getCalendarWithDefaultTimeZone()

    init {
        viewState.setCurrentDate(curDate)
    }

    fun setCurrentDate(date: Calendar) {
        curDate.timeInMillis = date.timeInMillis
        viewState.setCurrentDate(curDate)
    }
}
