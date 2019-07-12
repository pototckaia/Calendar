package com.example.calendar.repository.server.model

import org.threeten.bp.ZonedDateTime

data class PermissionServer(
    val id: Long,
    val owner_id: Long,
    val user_id: Long,
    val entity_id: Long,

    val name: String,
    val created_at: ZonedDateTime,
    val updated_at: ZonedDateTime
)
