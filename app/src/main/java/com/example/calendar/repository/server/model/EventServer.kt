package com.example.calendar.repository.server.model

import org.threeten.bp.ZonedDateTime

data class EventServer(
    val id: Long,
    val owner_id: Long,

    val created_at: ZonedDateTime,
    val updated_at: ZonedDateTime,

    var name: String,
    var details: String,
    var status: String,
    var location: String
)
