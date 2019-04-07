package com.example.calendar.customView

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import java.util.Calendar
import kotlin.collections.HashSet


class EventMonthDecorator() : DayViewDecorator {

    private val dateHash = HashSet<CalendarDay>()
    private val color = Color.BLUE

    fun setDates(dates: HashSet<Calendar>) {
        dateHash.clear()
        dates.forEach {it ->
            dateHash.add(CalendarDay.from(it))
        }
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dateHash.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5f, color))
    }
}