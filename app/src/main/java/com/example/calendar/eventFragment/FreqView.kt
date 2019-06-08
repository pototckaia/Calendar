package com.example.calendar.eventFragment

import android.app.DatePickerDialog
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.dmfs.rfc5545.recur.Freq
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.DayOfWeek
import org.threeten.bp.ZonedDateTime

@StateStrategyType(AddToEndSingleStrategy::class)
interface RecurrenceRuleView : MvpView {

    fun setViewRule(it: RecurrenceRule)

    fun setViewNotRule()

    fun setUntil(until: ZonedDateTime)

    fun setDayOfWeek(dayOfWeek: DayOfWeek)

    @StateStrategyType(SkipStrategy::class)
    fun openCalendar(until: ZonedDateTime, l : DatePickerDialog.OnDateSetListener)

    @StateStrategyType(SkipStrategy::class)
    fun showToast(s: String)

    @StateStrategyType(SkipStrategy::class)
    fun onSave(it: RecurrenceRule)

    @StateStrategyType(SkipStrategy::class)
    fun onExit(r: String)
}