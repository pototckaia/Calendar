package com.example.calendar.customView

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.calendar.R
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import com.example.calendar.helpers.*
import kotlinx.android.synthetic.main.dialog_date_picker.view.*
import org.threeten.bp.ZonedDateTime
import java.util.*

class MaterialDatePickerDialog(
    private val date: ZonedDateTime,
    private val onDateSet: DatePickerDialog.OnDateSetListener,
    context: Context,
    cancelListener: DialogInterface.OnCancelListener
) : AlertDialog(context, true, cancelListener) {

    private lateinit var v: View
    private lateinit var widget: MaterialCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        v = LayoutInflater.from(context)
            .inflate(R.layout.dialog_date_picker, null)
        widget = v.calendarView
        setView(v)

        val temp = toCalendar(date)
        widget.selectedDate = CalendarDay.from(temp)
        widget.currentDate = widget.selectedDate
        widget.addDecorators(
            TodayDecorator(
                // todo !!
                context.getDrawable(
                    R.drawable.today_circle_background
                )!!
            )
        )
        setTitle("")
        setButton(DialogInterface.BUTTON_POSITIVE, context.getString(android.R.string.ok), { _, _i -> onOkClick() })
        super.onCreate(savedInstanceState)
    }

    private fun onOkClick() {
        val c = widget.selectedDate.calendar
        onDateSet.onDateSet(
            null,
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH) + 1,
            c.get(Calendar.DAY_OF_MONTH)
        )
    }
}