package com.example.calendar.repository

import com.example.calendar.data.Event
import java.util.*

interface EventRepository {
    fun addEvent(event: Event)
    fun eventsFromTo(begin: Calendar, end: Calendar) : List<Event>
}