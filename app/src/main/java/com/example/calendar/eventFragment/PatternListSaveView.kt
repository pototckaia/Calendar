package com.example.calendar.eventFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.calendar.repository.server.model.PatternRequest

@StateStrategyType(AddToEndSingleStrategy::class)
interface PatternListSaveView: MvpView {

    fun setPatterns(patterns: ArrayList<PatternRequest>)

    fun addPattern(pattern: PatternRequest)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun updatePattern(m: PatternRequest, pos: Int)
}