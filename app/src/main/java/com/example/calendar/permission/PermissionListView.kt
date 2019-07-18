package com.example.calendar.permission

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.calendar.repository.server.model.PermissionModel

@StateStrategyType(AddToEndSingleStrategy::class)
interface PermissionListView : MvpView {

    @StateStrategyType(SingleStateStrategy::class)
    fun showToast(mes: String)

    @StateStrategyType(SingleStateStrategy::class)
    fun remove(pos: Int)

    fun setPermission(mine: Boolean, p: ArrayList<PermissionModel>)

    @StateStrategyType(SingleStateStrategy::class)
    fun showLoading()

    @StateStrategyType(SingleStateStrategy::class)
    fun stopLoading()
}
