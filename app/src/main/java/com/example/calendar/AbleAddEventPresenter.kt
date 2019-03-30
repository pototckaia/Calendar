package com.example.calendar

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.view.AbleAddEventView
import java.util.Calendar


@InjectViewState
class AbleAddEventPresenter : MvpPresenter<AbleAddEventView>() {

    fun addEventButtonClick(begin: Calendar, end: Calendar) {
        val fragment = CreateEventFragment.newInstance(begin, end)
        viewState.openFragment(fragment)
    }

    fun addEventButtonClick(commonDate: Calendar) {
        commonDate.set(Calendar.HOUR_OF_DAY, 1)
        commonDate.set(Calendar.MINUTE, 0)
        val end = commonDate.clone() as Calendar
        end.set(Calendar.HOUR_OF_DAY, 2)
        addEventButtonClick(commonDate, end)
    }

    fun addEventButtonClick() =
        addEventButtonClick(Calendar.getInstance())


}