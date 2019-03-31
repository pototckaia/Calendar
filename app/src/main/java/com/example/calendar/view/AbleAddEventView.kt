package com.example.calendar.view

import androidx.fragment.app.Fragment
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface AbleAddEventView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun openFragment(f: androidx.fragment.app.Fragment)
}


