package com.example.calendar.eventFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.calendar.data.EventInstance

@StateStrategyType(AddToEndSingleStrategy::class)
interface EditEventView : MvpView {

    fun updateEventInfo(e: EventInstance)

    @StateStrategyType(SingleStateStrategy::class)
    fun showError(e: String)
}