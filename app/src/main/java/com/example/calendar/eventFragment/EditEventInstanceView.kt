package com.example.calendar.eventFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.calendar.repository.server.model.*

@StateStrategyType(AddToEndSingleStrategy::class)
interface EditEventInstanceView : MvpView {

    fun updateEventInfo(user: UserServer, e: EventRequest, p: PatternRequest)

    @StateStrategyType(SingleStateStrategy::class)
    fun showError(e: String)
}