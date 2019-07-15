package com.example.calendar.customView

import android.graphics.Color
import com.example.calendar.helpers.convert.toCalendar
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import org.threeten.bp.ZonedDateTime
import kotlin.collections.HashSet


class MonthDotDecorator() : DayViewDecorator {

    private val dates = HashSet<CalendarDay>()

    private val color = Color.BLUE
    private val radius = 5f

    fun setDates(dates: HashSet<ZonedDateTime>) {
        this.dates.clear()
        dates.forEach {
            this.dates.add(CalendarDay.from(toCalendar(it)))
        }
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(radius, color))
    }
}