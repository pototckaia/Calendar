package com.example.calendar

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.view.CreateEventInfoView
import java.util.Calendar

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
    }

    fun onClickStartDateEvent() {}

    fun onClickEndDateEvent() {}

//        val c = event.beginCalendar
//        val dpd = DatePickerDialog(context,
//            DatePickerDialog.OnDateSetListener {_, year_, monthOfYear, dayOfMonth ->
//                onBeginDateSetListener(year_, monthOfYear, dayOfMonth)
//        }, c.get(Calendar.YEAR),
//           c.get(Calendar.MONTH),
//           c.get(Calendar.DAY_OF_MONTH))
//        dpd.show()
//    }
//
//    private fun onClickEndDateEvent() {
//        val c = event.endCalendar
//        val dpd = DatePickerDialog(context,
//            DatePickerDialog.OnDateSetListener {_, year_, monthOfYear, dayOfMonth ->
//                onEndDateSetListener(year_, monthOfYear, dayOfMonth)
//            }, c.get(Calendar.YEAR),
//            c.get(Calendar.MONTH),
//            c.get(Calendar.DAY_OF_MONTH))
//        dpd.show()
//    }
//
//    private fun onBeginDateSetListener(year: Int, monthOfYear: Int, dayOfMonth: Int) {
//        val c = event.beginCalendar
//        c.set(year, monthOfYear, dayOfMonth)
//        event.beginCalendar = c
//        if (event.endCalendar < c) {
//            val end = event.endCalendar
//            end.set(year, monthOfYear, dayOfMonth)
//            event.endCalendar = end
//        }
//        createEventPresenter()
//
//        val tpd = TimePickerDialog(context,
//            TimePickerDialog.OnTimeSetListener{ view: TimePicker?, hourOfDay: Int, minute: Int ->
//                onBeginTimeSetListener(hourOfDay, minute)
//            },
//            c.get(Calendar.HOUR_OF_DAY),
//            c.get(Calendar.MINUTE),
//            true)
//        tpd.show()
//    }
//
//    private fun onEndDateSetListener(year: Int, monthOfYear: Int, dayOfMonth: Int) {
//        val c = event.endCalendar
//        c.set(year, monthOfYear, dayOfMonth)
//        event.endCalendar = c
//        if (event.beginCalendar > c) {
//            val startEvent = event.beginCalendar
//            startEvent.set(year, monthOfYear, dayOfMonth)
//            event.beginCalendar = startEvent
//        }
//        createEventPresenter()
//
//        val tpd = TimePickerDialog(context,
//            TimePickerDialog.OnTimeSetListener{ _: TimePicker?, hourOfDay: Int, minute: Int ->
//                onEndTimeSetListener(hourOfDay, minute)
//            },
//            c.get(Calendar.HOUR_OF_DAY),
//            c.get(Calendar.MINUTE),
//            true)
//        tpd.show()
//    }
//
//    private fun onBeginTimeSetListener(hourOfDay: Int, minute: Int) {
//        val c = event.beginCalendar
//        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
//        c.set(Calendar.MINUTE, minute)
//        if (event.endCalendar < c) {
//            val end = c.clone() as Calendar
//            end.add(Calendar.HOUR_OF_DAY, 1)
//            event.endCalendar = end
//        }
//        event.beginCalendar = c
//        createEventPresenter()
//    }
//
//    private fun onEndTimeSetListener(hourOfDay: Int, minute: Int) {
//        val c = event.endCalendar
//        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
//        c.set(Calendar.MINUTE, minute)
//        if (event.beginCalendar > c) {
//            val startEvent = c.clone() as Calendar
//            startEvent.add(Calendar.HOUR_OF_DAY, -1)
//            event.beginCalendar = startEvent
//        }
//        event.endCalendar = c
//        createEventPresenter()
//    }

}