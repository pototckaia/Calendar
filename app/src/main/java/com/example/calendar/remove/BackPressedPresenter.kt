package com.example.calendar.remove

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.calendar.remove.BackPressedView


@InjectViewState
class BackPressedPresenter() : MvpPresenter<BackPressedView>() {

    fun onBackPressed() {
        viewState.finishView()
    }
}