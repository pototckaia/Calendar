package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone

@InjectViewState
class WeekSaveStatePresenter: MvpPresenter<WeekSaveStateView>() {

    var firstVisibleHour : Int = 0
    val firstVisibleDay = getCalendarWithDefaultTimeZone()
    var hourHeight = 12.toFloat()

    private var isUpdateState = false

    fun onCreateView() {
        isUpdateState = true
    }

    fun onStop() {
        viewState.saveState()
    }

    fun onMonthChange() {
        if (isUpdateState) {
            isUpdateState = false
            viewState.updateState()
        }
    }

}
