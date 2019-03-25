package com.example.calendar.repository

import com.example.calendar.data.Event
import java.util.*

class EventMock : EventRepository {
    private val events  = mutableListOf<Event>()

    override fun addEvent(event: Event) {
        events.add(event)
    }

    override fun eventsFromTo(begin: Calendar, end: Calendar): List<Event> {
        val selectEvents = mutableListOf<Event>()
        for (e in events) {
            if (e.beginCalendar >= begin || e.endCalendar <= end) {
                selectEvents.add(e)
            }
        }
        return selectEvents
    }

}