package com.example.calendar.eventFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.calendar.repository.server.model.PatternRequest


interface PatternsSaveView: MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun updatePattern(m: PatternRequest, pos: Int)
}