package com.example.calendar.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.calendar.data.EventTable
import java.util.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface MonthDotView : MvpView {

    fun setMonthEvents(it: HashSet<Calendar>)

    @StateStrategyType(SkipStrategy::class)
    fun showError(e : String)
}