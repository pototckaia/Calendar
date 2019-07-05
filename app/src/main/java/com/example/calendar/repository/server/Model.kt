package com.example.calendar.repository.server

data class EventServer(
    val id: Long,
    val owner_id: Long,

    val created_at: Long,
    val updated_at: Long,

    var name: String,
    var details: String,
    var status: String,
    var location: String
)

data class EventInstanceServer(
    val event_id: Long,
    val pattern_id: Long,

    val ended_at: Long,
    val started_at: Long
)

data class EventPatternExruleServer(
    val id: Long,
    var rule: String,
    val created_at: Long,
    val updated_at: Long
)

data class EventPatternServer(
    val id: Long,

    val created_at: Long,
    val updated_at: Long,

    var started_at: Long,
    var duration: Long,
    var ended_at: Long,
    var exrules: List<EventPatternExruleServer>,
    var rrule: String,
    var timezone: String
)

data class PermissionServer(
    val id: Long,
    val owner_id: Long,
    val user_id: Long,
    val entity_id: Long,

    val name: String,
    val created_at: Long,
    val updated_at: Long
)

data class Task(
    val id: Long,
    val parent_id: Long,

    var deadline_at: Long,
    var details: String,
    var name: String,
    var status: String,

    val created_at: Long,
    val updated_at: Long
)
