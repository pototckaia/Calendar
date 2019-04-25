package com.example.calendar.customView

import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewEvent
import com.example.calendar.data.EventTable

class EventWeekView (
    var event: EventTable,
    val isFake: Boolean,
    val color: Int
) : WeekViewDisplayable<EventWeekView> {

    override fun toWeekViewEvent(): WeekViewEvent<EventWeekView> {
        return WeekViewEvent(0,
            event.name, event.started_at, event.ended_at,
            null, color, false, this)
    }

}
