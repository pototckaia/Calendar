package com.example.calendar.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.calendar.data.Event
import java.util.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface CreateEventInfo : MvpView {

    fun updateEventInfo(e: Event)

    fun showDatePickerDialog(c: Calendar, l: DatePickerDialog.OnDateSetListener)

    fun showTimePickerDialog(c: Calendar, l: TimePickerDialog.OnTimeSetListener)
}