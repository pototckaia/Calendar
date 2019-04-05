package com.example.calendar.presenter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.helpers.cloneWithDefaultTimeZone
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import com.example.calendar.helpers.setHourOfDayAndMinute
import com.example.calendar.view.DateClickView
import java.util.*


@InjectViewState
class DateClickPresenter () : MvpPresenter<DateClickView>() {

    val startEvent = getCalendarWithDefaultTimeZone()

    val endEvent = getCalendarWithDefaultTimeZone()

    init {
        updateView()
    }

    constructor(beginTime: Long, endTime: Long) : this() {
        startEvent.timeInMillis = beginTime
        endEvent.timeInMillis = endTime
        updateView()
    }

    fun setDate(start: Calendar, end: Calendar) {
        startEvent.timeInMillis = start.timeInMillis
        endEvent.timeInMillis = end.timeInMillis
        updateView()
    }

    private fun updateView() {
        viewState.updateDateInfo(
            startEvent,
            endEvent)
    }

    // todo how remove this shit
    fun onClickBeginDay() {
        viewState.showDatePickerDialog(
            startEvent,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // TODO check it work
                startEvent.set(year, monthOfYear, dayOfMonth)
                if (startEvent > endEvent) {
                    endEvent.set(year, monthOfYear, dayOfMonth)
                }
                updateView()
            })
    }

    fun onClickBeginHour() {
        viewState.showTimePickerDialog(
            startEvent.cloneWithDefaultTimeZone(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                startEvent.setHourOfDayAndMinute(hourOfDay, minute)
                if (startEvent >= endEvent) {
                    endEvent.timeInMillis = startEvent.timeInMillis
                    endEvent.add(Calendar.HOUR_OF_DAY, 1)
                }
                updateView()
            })
    }

    fun onClickEndDay() {
        viewState.showDatePickerDialog(
            endEvent.cloneWithDefaultTimeZone(),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                endEvent.set(year, monthOfYear, dayOfMonth)
                if (endEvent < startEvent) {
                    startEvent.set(year, monthOfYear, dayOfMonth)
                }
                updateView()
            })
    }


    fun onClickEndHour() {
        viewState.showTimePickerDialog(
            endEvent.cloneWithDefaultTimeZone(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                endEvent.setHourOfDayAndMinute(hourOfDay, minute)
                if (endEvent <= startEvent) {
                    startEvent.timeInMillis = endEvent.timeInMillis
                    startEvent.add(Calendar.HOUR_OF_DAY, -1)
                }
                updateView()
            })
    }

}