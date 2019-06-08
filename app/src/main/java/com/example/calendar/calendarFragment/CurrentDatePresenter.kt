package com.example.calendar.calendarFragment

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime

@InjectViewState
class CurrentDatePresenter() : MvpPresenter<CurrentDateView>() {

    private var curDate = ZonedDateTime.now(ZoneId.systemDefault())

    init {
        viewState.setCurrentDate(curDate)
    }

    fun setCurrentDate(date: ZonedDateTime) {
        curDate = date.withZoneSameInstant(ZoneId.systemDefault())
        viewState.setCurrentDate(curDate)
    }
}
