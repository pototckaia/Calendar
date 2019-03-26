package com.example.calendar.view

import android.support.v4.app.Fragment
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface AddEventView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun openFragment(f: Fragment)
}


