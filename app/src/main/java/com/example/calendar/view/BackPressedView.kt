package com.example.calendar.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface BackPressedView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun finishView()
}