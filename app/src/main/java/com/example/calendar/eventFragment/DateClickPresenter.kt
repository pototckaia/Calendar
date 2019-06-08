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
    var start: ZonedDateTime,
    var end: ZonedDateTime,
    private val validateStart: (ZonedDateTime) -> Boolean,
    private val validateEnd: (ZonedDateTime) -> Boolean
) : MvpPresenter<DateClickView>() {

    init {
        setDate(start, end)
        updateView()
    }

    private fun updateView() {
        viewState.updateDateInfo(start, end)
    }

    fun setDate(s: ZonedDateTime, e: ZonedDateTime) {
        start = ZonedDateTime.from(s)
        end = ZonedDateTime.from(e)
    }

    // todo how remove this shit
    fun onClickBeginDay() {
        viewState.showDatePickerDialog(
            start,
            // month start from 1
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // TODO check it work
                val newStart = withYearMonthDay(start, year, monthOfYear, dayOfMonth)
                if (!validateStart(newStart)) { return@OnDateSetListener }

                start = newStart
                if (start > end) {
                    end = withYearMonthDay(end, year, monthOfYear, dayOfMonth)
                }
                updateView()
            })
    }

    fun onClickBeginHour() {
        viewState.showTimePickerDialog(
            start,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val newStart = withHourMinuteTruncate(start, hourOfDay, minute)
                if (!validateStart(newStart)) { return@OnTimeSetListener }

                start = newStart
                if (start >= end) {
                    end = start.plusHours(1)
                }
                updateView()
            })
    }

    fun onClickEndDay() {
        viewState.showDatePickerDialog(
            end,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val newEnd = withYearMonthDay(end, year, monthOfYear, dayOfMonth)
                if (!validateEnd(newEnd)) { return@OnDateSetListener }

                end = newEnd
                if (end < start) {
                    start = withYearMonthDay(start, year, monthOfYear, dayOfMonth)
                }
                updateView()
            })
    }


    fun onClickEndHour() {
        viewState.showTimePickerDialog(
            end,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val newEnd = withHourMinuteTruncate(end, hourOfDay, minute)
                if (!validateEnd(newEnd)) { return@OnTimeSetListener }

                end = newEnd
                if (end <= start) {
                    start = end.minusHours(1)
                }
                updateView()
            })
    }

}