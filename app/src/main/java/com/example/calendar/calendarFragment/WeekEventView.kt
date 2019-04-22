package com.example.calendar.calendarFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


@StateStrategyType(AddToEndSingleStrategy::class)
interface WeekEventView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun notifyEventSetChanged()

    @StateStrategyType(SkipStrategy::class)
    fun showError(e : String)

    @StateStrategyType(SkipStrategy::class)
    fun showLoadingEvents()

    @StateStrategyType(SkipStrategy::class)
    fun closeLoadingEvents()
}