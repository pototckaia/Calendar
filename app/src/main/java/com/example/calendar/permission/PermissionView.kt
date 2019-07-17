package com.example.calendar.permission

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface PermissionView: MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun addToClipboard(s: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showToast(mes: String)
}
