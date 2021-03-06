package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import java.util.Calendar

@InjectViewState
class WeekSaveStatePresenter: MvpPresenter<WeekSaveStateView>() {

    var firstVisibleHour : Int = 0
    val firstVisibleDay = Calendar.getInstance()
    var hourHeight : Float = 12f

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
