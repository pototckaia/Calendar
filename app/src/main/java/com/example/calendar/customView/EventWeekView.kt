package com.example.calendar.customView

import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewEvent
import com.example.calendar.helpers.convert.toCalendar
import com.example.calendar.repository.server.model.EventInstance

class EventWeekView (
    var event: EventInstance,
    val isFake: Boolean,
    val color: Int
) : WeekViewDisplayable<EventWeekView> {

    override fun toWeekViewEvent(): WeekViewEvent<EventWeekView> {
        return WeekViewEvent(0,
            event.entity_name,
            toCalendar(event.started_at_local), toCalendar(event.ended_at_local),
            null, color, false, this)
    }
}
