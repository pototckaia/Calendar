package com.example.calendar.customView

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import com.example.calendar.R
import android.app.DatePickerDialog
import com.example.calendar.helpers.DIALOG_DATE_KEY
import kotlinx.android.synthetic.main.fragment_month_calendar.view.*
import java.util.*


class DialogDatePicker : AppCompatDialogFragment() {

    private lateinit var listener: DatePickerDialog.OnDateSetListener

    companion object {
        fun newInstance(date: Calendar, l: DatePickerDialog.OnDateSetListener): DialogDatePicker {
            val pickerFragment = DialogDatePicker()
            val bundle = Bundle()
            bundle.putLong(DIALOG_DATE_KEY, date.timeInMillis)
            pickerFragment.listener = l
            pickerFragment.arguments = bundle
            return pickerFragment
        }
    }

    private lateinit var v : View
    private lateinit var widget: MaterialCalendarView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val inflater = activity!!.layoutInflater

        v = inflater.inflate(R.layout.dialog_date_picker, null)
        widget = v.findViewById(R.id.calendarView)
        retainInstance = true

        val curDate = getCalendarWithDefaultTimeZone()
        curDate.timeInMillis = arguments!!.getLong(DIALOG_DATE_KEY)
        widget.selectedDate = CalendarDay.from(curDate)
        widget.currentDate = widget.selectedDate
        widget.addDecorators(
            TodayDecorator(resources.getDrawable(R.drawable.today_circle_background, null))
        )

        return AlertDialog.Builder(activity)
            .setTitle("")
            .setView(v)
            .setPositiveButton(android.R.string.ok) {_, _i -> onOkClick() }
            .create()
    }

    private fun onOkClick() {
        val c = widget.selectedDate.calendar
        listener.onDateSet(null,
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH))
    }
}