package com.example.calendar.repository.server


data class RruleStructure(
    var rrule: String
)

data class EventRequest(
    val details: String,
    val location: String,
    val name: String,
    val status: String
)

data class PatternRequest(
    val duration: Long,
    val ended_at: Long,
    val exrules: List<RruleStructure>,
    val rrule: String,
    val started_at: Long,
    val timezone: String
)

data class TaskRequest(
    val deadline_at: Long,
    val details: String,
    val name: String,
    val parent_id: Long,
    val status: String
)

enum class PermissionAction {
    READ, UPDATE, DELETE;
}

enum class EntityType {
    EVENT, PATTERN, TASK;
}

data class PermissionRequest(
    val action: PermissionAction,
    val entity_id: Long,
    val entity_type: EntityType
)