package com.example.calendar.customView

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.calendar.helpers.getCalendarWithDefaultTimeZone
import com.example.calendar.R
import android.app.DatePickerDialog
import com.example.calendar.helpers.DIALOG_DATE_KEY
import kotlinx.android.synthetic.main.dialog_date_picker.view.*
import java.util.*

class MaterialDatePickerDialog : AppCompatDialogFragment() {

    private lateinit var onDateSet: DatePickerDialog.OnDateSetListener

    companion object {
        fun newInstance(
            date: Calendar,
            l: DatePickerDialog.OnDateSetListener
        ): MaterialDatePickerDialog {
            val pickerFragment = MaterialDatePickerDialog()
            val bundle = Bundle()
            bundle.putLong(DIALOG_DATE_KEY, date.timeInMillis)
            pickerFragment.onDateSet = l
            pickerFragment.arguments = bundle
            return pickerFragment
        }
    }

    private lateinit var v: View
    private lateinit var widget: MaterialCalendarView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val inflater = activity!!.layoutInflater

        v = inflater.inflate(R.layout.dialog_date_picker, null)
        widget = v.calendarView
        retainInstance = true

        val curDate = getCalendarWithDefaultTimeZone()
        curDate.timeInMillis = arguments!!.getLong(DIALOG_DATE_KEY)
        widget.selectedDate = CalendarDay.from(curDate)
        widget.currentDate = widget.selectedDate
        widget.addDecorators(
            TodayDecorator(
                resources.getDrawable(
                    R.drawable.today_circle_background, null
                )
            )
        )

        return AlertDialog.Builder(activity)
            .setTitle("")
            .setView(v)
            .setPositiveButton(android.R.string.ok) { _, _i -> onOkClick() }
            .create()
    }

    private fun onOkClick() {
        val c = widget.selectedDate.calendar
        onDateSet.onDateSet(
            null,
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
    }
}