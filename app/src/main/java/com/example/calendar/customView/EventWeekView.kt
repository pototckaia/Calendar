package com.example.calendar.customView

import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewEvent
import com.example.calendar.repository.db.EventInstance
import com.example.calendar.helpers.toCalendar

class EventWeekView (
    var event: EventInstance,
    val isFake: Boolean,
    val color: Int
) : WeekViewDisplayable<EventWeekView> {

    override fun toWeekViewEvent(): WeekViewEvent<EventWeekView> {
        return WeekViewEvent(0,
            event.nameEventRecurrence,
            toCalendar(event.startedAtLocal), toCalendar(event.endedAtLocal),
            null, color, false, this)
    }

}
