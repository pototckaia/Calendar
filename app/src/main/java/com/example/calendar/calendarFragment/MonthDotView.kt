package com.example.calendar.calendarFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import java.util.Calendar

@StateStrategyType(AddToEndSingleStrategy::class)
interface MonthDotView : MvpView {

    fun setMonthDots(it: HashSet<Calendar>)

    @StateStrategyType(SkipStrategy::class)
    fun showError(e: String)
}