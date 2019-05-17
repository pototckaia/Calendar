package com.example.calendar.sampledata

import com.arellomobile.mvp.MoxyReflector.getViewState
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.InjectViewState


@InjectViewState
class CounterPresenter : MvpPresenter<CounterView>() {
    private var mCount: Int = 0

    init {
        viewState.showCount(mCount)
    }

    fun onPlusClick() {
        mCount++
        viewState.showCount(mCount)
    }
}