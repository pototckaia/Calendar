package com.example.calendar.repository.server


data class Event(
    val event: EventServer,
    // todo make list
    val pattern: EventPatternServer
)

data class EventInstance(
    val event: EventServer,
    val pattern: EventPatternServer,

    val ended_at: Long,
    val started_at: Long
)

