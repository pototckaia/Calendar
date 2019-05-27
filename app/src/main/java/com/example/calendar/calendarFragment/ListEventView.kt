package com.example.calendar.calendarFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.calendar.data.EventInstance

@StateStrategyType(AddToEndSingleStrategy::class)
interface ListEventView : MvpView {

    fun setEvents(it: List<EventInstance>)

    @StateStrategyType(SkipStrategy::class)
    fun showError(e : String)
}