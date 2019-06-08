package com.example.calendar.eventFragment

import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe

@InjectViewState
class RecurrenceEventPresenter(
    private var rule: String = "") : BaseMvpSubscribe<RecurrenceEventView>() {

    init {
        viewState.postRecurrence(rule)
    }

    fun getRule() : String = rule

    fun onRuleChange(r: String) {
        rule = r
        viewState.setRecurrenceViw(r)
    }
}