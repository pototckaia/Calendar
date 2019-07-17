package com.example.calendar.permission

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface ActivateTokenView: MvpView {

    fun showDialog()

    fun dismissDialog()

    fun showToast(mes: String)
}

