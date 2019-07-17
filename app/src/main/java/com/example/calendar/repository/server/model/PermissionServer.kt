package com.example.calendar.repository.server.model

import org.threeten.bp.ZonedDateTime

data class PermissionServer(
    val id: Long,
    val owner_id: String,
    val user_id: String,
    val entity_id: String,

    val name: String,
    val created_at: ZonedDateTime,
    val updated_at: ZonedDateTime
)
