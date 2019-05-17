package com.example.calendar.sampledata

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.arellomobile.mvp.MvpView


interface CounterView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showCount(count: Int)
}