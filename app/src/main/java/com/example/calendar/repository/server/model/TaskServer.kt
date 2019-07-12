package com.example.calendar.repository.server.model

import org.threeten.bp.ZonedDateTime

data class TaskServer(
    val id: Long,
    val parent_id: Long,

    var deadline_at: ZonedDateTime,
    var details: String,
    var name: String,
    var status: String,

    val created_at: ZonedDateTime,
    val updated_at: ZonedDateTime
)
