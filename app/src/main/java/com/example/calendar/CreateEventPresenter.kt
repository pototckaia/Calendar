package com.example.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.view.CreateEventInfoView
import java.util.*

@InjectViewState
class CreateEventPresenter(
    var titleEvent: String,
    beginTime: Long,
    endTime: Long
) : MvpPresenter<CreateEventInfoView>() {

    private val startEvent = Calendar.getInstance()
    private val endEvent = Calendar.getInstance()

    init {
        startEvent.timeInMillis = beginTime
        endEvent.timeInMillis = endTime
        viewState.updateEventInfo(startEvent, endEvent)
    }


    fun onClickBeginDay() {
        viewState.showDatePickerDialog(
            startEvent,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                startEvent.set(year, monthOfYear, dayOfMonth)
                if (startEvent > endEvent) {
                    endEvent.set(year, monthOfYear, dayOfMonth)
                }
                viewState.updateEventInfo(startEvent, endEvent)
            })
    }

    fun onClickBeginHour() {
        viewState.showTimePickerDialog(
            startEvent,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                startEvent.set(Calendar.HOUR_OF_DAY, hourOfDay)
                startEvent.set(Calendar.MINUTE, minute)
                if (startEvent >= endEvent) {
                    endEvent.timeInMillis = startEvent.timeInMillis
                    endEvent.add(Calendar.HOUR_OF_DAY, 1)
                }
                viewState.updateEventInfo(startEvent, endEvent)
            })
    }

    fun onClickEndDay() {
        viewState.showDatePickerDialog(
            endEvent,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                endEvent.set(year, monthOfYear, dayOfMonth)
                if (endEvent < startEvent) {
                    startEvent.set(year, monthOfYear, dayOfMonth)
                }
                viewState.updateEventInfo(startEvent, endEvent)
            })
    }


    fun onClickEndHour() {
        viewState.showTimePickerDialog(
            endEvent,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                endEvent.set(Calendar.HOUR_OF_DAY, hourOfDay)
                endEvent.set(Calendar.MINUTE, minute)
                if (endEvent <= startEvent) {
                    startEvent.timeInMillis = endEvent.timeInMillis
                    startEvent.add(Calendar.HOUR_OF_DAY, -1)
                }
                viewState.updateEventInfo(startEvent, endEvent)
            })
    }

}