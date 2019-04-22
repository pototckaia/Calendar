package com.example.calendar.eventFragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import java.util.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface DateClickView : MvpView {

    fun updateDateInfo(begin: Calendar, end: Calendar)

    @StateStrategyType(SkipStrategy::class)
    fun showDatePickerDialog(c: Calendar, l: DatePickerDialog.OnDateSetListener)

    @StateStrategyType(SkipStrategy::class)
    fun showTimePickerDialog(c: Calendar, l: TimePickerDialog.OnTimeSetListener)
}