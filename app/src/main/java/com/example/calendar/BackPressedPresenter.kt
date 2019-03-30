package com.example.calendar

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.view.BackPressedView


@InjectViewState
class BackPressedPresenter() : MvpPresenter<BackPressedView>() {

    fun onBackPressed() {
        viewState.finishView()
    }
}