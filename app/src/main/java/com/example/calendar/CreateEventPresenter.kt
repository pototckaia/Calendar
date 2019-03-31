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
    private val eventRepository: EventRepository,
    var titleEvent: String,
    beginTime: Long,
    endTime: Long
) : MvpPresenter<CreateEventInfoView>() {

    private val startEvent = getCalendarWithUTF()
    private val endEvent = getCalendarWithUTF()

    init {
        startEvent.timeInMillis = beginTime
        endEvent.timeInMillis = endTime
        updateView()
    }

    private fun updateView() {
        viewState.updateEventInfo(
            startEvent.cloneWithDefaultTimeZone(),
            endEvent.cloneWithDefaultTimeZone())
    }

    fun onSaveEvent(title: String, back: BackPressedPresenter) {
        val event = EventTable(name = title, started_at = startEvent, ended_at = endEvent)

        eventRepository.insert(event)
            .subscribeOn(Schedulers.io())

        back.onBackPressed()
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
                viewState.updateEventInfo(startEvent, endEvent)
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
                viewState.updateEventInfo(startEvent, endEvent)
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
                viewState.updateEventInfo(startEvent, endEvent)
            })
    }

}