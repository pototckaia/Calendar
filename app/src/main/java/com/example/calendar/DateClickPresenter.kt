package com.example.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.helpers.cloneWithDefaultTimeZone
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import com.example.calendar.helpers.getCalendarWithUTF
import com.example.calendar.view.DateClickView
import java.util.*


@InjectViewState
class DateClickPresenter (
    beginTime: Long,
    endTime: Long
) : MvpPresenter<DateClickView>() {

    val startEvent = getCalendarWithUTF()
    val endEvent = getCalendarWithUTF()

    init {
        startEvent.timeInMillis = beginTime
        endEvent.timeInMillis = endTime
        updateView()
    }

    private fun updateView() {
        viewState.updateDateInfo(
            startEvent.cloneWithDefaultTimeZone(),
            endEvent.cloneWithDefaultTimeZone())
    }

    // todo how remove this shit
    fun onClickBeginDay() {
        viewState.showDatePickerDialog(
            startEvent.cloneWithDefaultTimeZone(),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // TODO check it work
                val local = getCalendarWithDefaultTimeZone()
                local.timeInMillis = startEvent.timeInMillis
                local.set(year, monthOfYear, dayOfMonth)
                startEvent.timeInMillis = local.timeInMillis

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
                val local = getCalendarWithDefaultTimeZone()
                local.timeInMillis = startEvent.timeInMillis
                local.set(Calendar.HOUR_OF_DAY, hourOfDay)
                local.set(Calendar.MINUTE, minute)
                startEvent.timeInMillis = local.timeInMillis

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
                val local = getCalendarWithDefaultTimeZone()
                local.timeInMillis = endEvent.timeInMillis
                local.set(year, monthOfYear, dayOfMonth)
                endEvent.timeInMillis = local.timeInMillis

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
                val local = getCalendarWithDefaultTimeZone()
                local.timeInMillis = endEvent.timeInMillis
                local.set(Calendar.HOUR_OF_DAY, hourOfDay)
                local.set(Calendar.MINUTE, minute)
                endEvent.timeInMillis = local.timeInMillis

                if (endEvent <= startEvent) {
                    startEvent.timeInMillis = endEvent.timeInMillis
                    startEvent.add(Calendar.HOUR_OF_DAY, -1)
                }
                updateView()
            })
    }

}