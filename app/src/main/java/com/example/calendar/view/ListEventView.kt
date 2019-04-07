package com.example.calendar.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.calendar.data.EventTable
import java.util.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface ListEventView : MvpView {

    fun setDayEvents(it: List<EventTable>)

    @StateStrategyType(SkipStrategy::class)
    fun showError(e : String)

    fun setCurrentDate(localStart: Calendar, localEnd: Calendar)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openFragment(f: androidx.fragment.app.Fragment)
}