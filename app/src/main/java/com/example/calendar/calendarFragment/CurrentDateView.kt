package com.example.calendar.calendarFragment

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.threeten.bp.ZonedDateTime

@StateStrategyType(AddToEndSingleStrategy::class)
interface CurrentDateView: MvpView {

    fun setCurrentDate(dateLocal: ZonedDateTime)
}