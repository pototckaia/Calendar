package com.example.calendar.repository.server.model

import org.threeten.bp.ZonedDateTime

data class EventInstanceServer(
    val event_id: Long,
    val pattern_id: Long,

    val ended_at: ZonedDateTime,
    val started_at: ZonedDateTime
)
