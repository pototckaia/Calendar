package com.example.calendar.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import java.util.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface DateClickView : MvpView {

    fun updateDateInfo(begin: Calendar, end: Calendar)

    fun showDatePickerDialog(c: Calendar, l: DatePickerDialog.OnDateSetListener)

    fun showTimePickerDialog(c: Calendar, l: TimePickerDialog.OnTimeSetListener)
}