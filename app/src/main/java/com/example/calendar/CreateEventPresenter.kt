package com.example.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.data.EventRepository
import com.example.calendar.data.EventTable
import com.example.calendar.helpers.cloneWithDefaultTimeZone
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import com.example.calendar.helpers.getCalendarWithUTF
import com.example.calendar.view.CreateEventInfoView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

@InjectViewState
class CreateEventPresenter(
    private val eventRepository: EventRepository
) : MvpPresenter<CreateEventInfoView>() {


    fun onSaveEvent(
        title: String, startEvent: Calendar, endEvent: Calendar,
        back: BackPressedPresenter
    ) {
        val event = EventTable(name = title, started_at = startEvent, ended_at = endEvent)

        eventRepository.insert(event)
            .subscribeOn(Schedulers.io())
            .subscribe({
                back.onBackPressed()
            })

    }
}