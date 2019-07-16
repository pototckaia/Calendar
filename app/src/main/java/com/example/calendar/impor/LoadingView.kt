package com.example.calendar.impor

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface LoadingView : MvpView {

    @StateStrategyType(SingleStateStrategy::class)
    fun showLoading()

    @StateStrategyType(SingleStateStrategy::class)
    fun stopLoading()

    @StateStrategyType(SingleStateStrategy::class)
    fun showToast(mes: String)
}
