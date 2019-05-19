package com.example.calendar.eventFragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.helpers.withHourMinuteTruncate
import com.example.calendar.helpers.withYearMonthDay
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.*


@InjectViewState
class DateClickPresenter(
    start: ZonedDateTime,
    end: ZonedDateTime
) : MvpPresenter<DateClickView>() {

    var startLocal: ZonedDateTime = start.withZoneSameInstant(ZoneId.systemDefault())
    var endLocal: ZonedDateTime = end.withZoneSameInstant(ZoneId.systemDefault())

    init {
        updateView()
    }

    private fun updateView() {
        viewState.updateDateInfo(startLocal, endLocal)
    }

    fun setDate(s: ZonedDateTime, e: ZonedDateTime) {
        startLocal = s.withZoneSameInstant(ZoneId.systemDefault())
        endLocal= e.withZoneSameInstant(ZoneId.systemDefault())
    }

    // todo how remove this shit
    fun onClickBeginDay() {
        viewState.showDatePickerDialog(
            startLocal,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // TODO check it work
                startLocal = withYearMonthDay(startLocal, year, monthOfYear, dayOfMonth)
                if (startLocal > endLocal) {
                    endLocal = withYearMonthDay(endLocal, year, monthOfYear, dayOfMonth)
                }
                updateView()
            })
    }

    fun onClickBeginHour() {
        viewState.showTimePickerDialog(
            startLocal,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                startLocal = withHourMinuteTruncate(startLocal, hourOfDay, minute)
                if (startLocal >= endLocal) {
                    endLocal = startLocal.plusHours(1)
                }
                updateView()
            })
    }

    fun onClickEndDay() {
        viewState.showDatePickerDialog(
            endLocal,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                endLocal = withYearMonthDay(endLocal, year, monthOfYear, dayOfMonth)
                if (endLocal < startLocal) {
                    startLocal = withYearMonthDay(startLocal, year, monthOfYear, dayOfMonth)
                }
                updateView()
            })
    }


    fun onClickEndHour() {
        viewState.showTimePickerDialog(
            endLocal,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                endLocal = withHourMinuteTruncate(endLocal, hourOfDay, minute)
                if (endLocal <= startLocal) {
                    startLocal = endLocal.minusHours(1)
                }
                updateView()
            })
    }

}