package com.example.calendar.data

import android.graphics.Color
import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewEvent
import java.util.*

class EventWeekCalendar (
    var event: EventTable,
    val color: Int
) : WeekViewDisplayable<EventWeekCalendar> {

    override fun toWeekViewEvent(): WeekViewEvent<EventWeekCalendar> {
        return WeekViewEvent(0,
            event.name, event.started_at, event.ended_at,
            null, color, false, this)
    }

}
