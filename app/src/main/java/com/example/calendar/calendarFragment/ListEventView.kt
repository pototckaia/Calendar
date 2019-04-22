package com.example.calendar.calendarFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.calendar.data.EventTable
import androidx.fragment.app.Fragment

@StateStrategyType(AddToEndSingleStrategy::class)
interface ListEventView : MvpView {

    fun setEvents(it: List<EventTable>)

    @StateStrategyType(SkipStrategy::class)
    fun showError(e : String)

//    fun setCurrentDate(localStart: Calendar, localEnd: Calendar)

//    @StateStrategyType(OneExecutionStateStrategy::class)
//    fun openFragment(f: Fragment)
}