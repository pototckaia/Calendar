package com.example.calendar.repository.server.model

import org.threeten.bp.ZonedDateTime

data class EventPatternExruleServer(
    val id: Long,

    var rule: String,
    val created_at: ZonedDateTime,
    val updated_at: ZonedDateTime
)
