package com.example.calendar

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.view.AddEventView
import java.util.Calendar


@InjectViewState
class AddEventPresenter : MvpPresenter<AddEventView>() {

    fun addEventButtonClick(begin: Calendar, end: Calendar) {
        val fragment = NoteCreateFragment.newInstance(begin, end)
        viewState.openFragment(fragment)
    }

    fun addEventButtonClick(commonDate: Calendar) {
        commonDate.set(Calendar.HOUR_OF_DAY, 1)
        commonDate.set(Calendar.MINUTE, 0)
        val end = commonDate.clone() as Calendar
        end.set(Calendar.HOUR_OF_DAY, 2)
        addEventButtonClick(commonDate, end)
    }

}