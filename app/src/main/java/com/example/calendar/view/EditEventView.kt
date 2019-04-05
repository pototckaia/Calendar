package com.example.calendar.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.calendar.data.EventTable
import java.util.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface EditEventView : MvpView {

    fun updateEventInfo(e: EventTable)

    @StateStrategyType(SingleStateStrategy::class)
    fun showError(e: String)
}