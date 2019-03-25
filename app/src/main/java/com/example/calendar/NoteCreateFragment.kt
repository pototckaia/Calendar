package com.example.calendar;

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import com.example.calendar.data.Event
import com.example.calendar.helpers.EVENT_KEY
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_note_create.view.*
import java.text.SimpleDateFormat
import java.util.*

class NoteCreateFragment  : Fragment() {
    private lateinit var event: Event
    private lateinit var notePreview: View
    private val formatter = SimpleDateFormat("EE, dd/MM/yyyy HH:mm", Locale.getDefault())

    companion object {
        fun newInstance(): NoteCreateFragment{
            return NoteCreateFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
         notePreview = inflater.inflate(
            R.layout.fragment_note_create,
            container, false
        )

        val defaultText = resources.getString(R.string.default_event_text)
        if (savedInstanceState == null) {
            val arg = arguments
            event = Event(defaultText)
            if (arg!= null && arg.containsKey(EVENT_KEY)) {
                event = arg.getParcelable(EVENT_KEY) ?: Event(defaultText)
            }
        } else {
            event = savedInstanceState.getParcelable(EVENT_KEY) ?: Event(defaultText)
        }
        updateInterface()

        notePreview.etBeginDate.setOnClickListener { onClickBeginDate() }
        notePreview.etEndDate.setOnClickListener { onClickEndDate() }

        return notePreview
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.clearFindViewByIdCache()
    }

    private fun updateInterface() {
        notePreview.etTextEvent.setText(event.text)
        notePreview.etBeginDate.setText(formatter.format(event.beginDate))
        notePreview.etEndDate.setText(formatter.format(event.endDate))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putParcelable(EVENT_KEY, event)
        }
        super.onSaveInstanceState(outState)
    }

    private fun onClickBeginDate() {
        val c = event.beginCalendar
        val dpd = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener {view, year_, monthOfYear, dayOfMonth ->
                onBeginDateSetListener(year_, monthOfYear, dayOfMonth)
        }, c.get(Calendar.YEAR),
           c.get(Calendar.MONTH),
           c.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    private fun onClickEndDate() {
        val c = event.endCalendar
        val dpd = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener {view, year_, monthOfYear, dayOfMonth ->
                onEndDateSetListener(year_, monthOfYear, dayOfMonth)
            }, c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    private fun onBeginDateSetListener(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val c = event.beginCalendar
        c.set(year, monthOfYear, dayOfMonth)
        event.beginCalendar = c
        if (event.endCalendar < c) {
            val end = event.endCalendar
            end.set(year, monthOfYear, dayOfMonth)
            event.endCalendar = end
        }
        updateInterface()

        val tpd = TimePickerDialog(context,
            TimePickerDialog.OnTimeSetListener{ view: TimePicker?, hourOfDay: Int, minute: Int ->
                onBeginTimeSetListener(hourOfDay, minute)
            },
            c.get(Calendar.HOUR_OF_DAY),
            c.get(Calendar.MINUTE),
            true)
        tpd.show()
    }

    private fun onEndDateSetListener(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val c = event.endCalendar
        c.set(year, monthOfYear, dayOfMonth)
        event.endCalendar = c
        if (event.beginCalendar > c) {
            val begin = event.beginCalendar
            begin.set(year, monthOfYear, dayOfMonth)
            event.beginCalendar = begin
        }
        updateInterface()

        val tpd = TimePickerDialog(context,
            TimePickerDialog.OnTimeSetListener{ view: TimePicker?, hourOfDay: Int, minute: Int ->
                onEndTimeSetListener(hourOfDay, minute)
            },
            c.get(Calendar.HOUR_OF_DAY),
            c.get(Calendar.MINUTE),
            true)
        tpd.show()
    }

    private fun onBeginTimeSetListener(hourOfDay: Int, minute: Int) {
        val c = event.beginCalendar
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        if (event.endCalendar < c) {
            val end = c.clone() as Calendar
            end.add(Calendar.HOUR_OF_DAY, 1)
            event.endCalendar = end
        }
        event.beginCalendar = c
        updateInterface()
    }

    private fun onEndTimeSetListener(hourOfDay: Int, minute: Int) {
        val c = event.endCalendar
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        if (event.beginCalendar > c) {
            val begin = c.clone() as Calendar
            begin.add(Calendar.HOUR_OF_DAY, -1)
            event.beginCalendar = begin
        }
        event.endCalendar = c
        updateInterface()
    }

}