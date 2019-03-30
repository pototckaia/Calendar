package com.example.calendar.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface BackPressedView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun finishView()
}