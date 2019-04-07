package com.example.calendar.customView

import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.CalendarDay
import android.graphics.drawable.Drawable
import com.prolificinteractive.materialcalendarview.DayViewDecorator


class TodayDecorator(private val backgroundDrawable: Drawable) : DayViewDecorator {

    private val today = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return today == day
    }

    override fun decorate(view: DayViewFacade) {
        view.setBackgroundDrawable(backgroundDrawable)
    }
}