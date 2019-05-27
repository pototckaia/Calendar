package com.example.calendar.calendarFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.threeten.bp.ZonedDateTime

@StateStrategyType(AddToEndSingleStrategy::class)
interface MonthDotView : MvpView {

    fun setMonthDots(it: HashSet<ZonedDateTime>)

    @StateStrategyType(SkipStrategy::class)
    fun showError(e: String)
}