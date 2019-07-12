package com.example.calendar.eventFragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.threeten.bp.ZonedDateTime

@StateStrategyType(AddToEndSingleStrategy::class)
interface DateClickView : MvpView {

    fun updateDateInfo(startLocal: ZonedDateTime, endLocal: ZonedDateTime)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDatePickerDialog(local: ZonedDateTime, l: DatePickerDialog.OnDateSetListener)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showTimePickerDialog(local: ZonedDateTime, l: TimePickerDialog.OnTimeSetListener)
}