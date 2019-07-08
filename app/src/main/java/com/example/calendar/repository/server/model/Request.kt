package com.example.calendar.repository.server.model

import org.threeten.bp.Duration
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime


data class RruleStructure(
    var rrule: String
)

data class EventRequest(
    val details: String,
    val location: String,
    val name: String,
    val status: String
) {
    constructor(entity: EventServer)
            : this(
        details = entity.details,
        location = entity.location,
        name = entity.name,
        status = entity.status
    )
}

data class PatternRequest(
    val duration: Duration,
    val ended_at: ZonedDateTime,
    val exrules: List<RruleStructure>,
    val rrule: String,
    val started_at: ZonedDateTime,
    val timezone: ZoneId
) {
    constructor(p: EventPatternServer)
            : this(
        duration = p.duration,
        ended_at = p.ended_at,
        exrules = p.exrules.map { RruleStructure(it.rule) },
        rrule = p.rrule,
        started_at = p.started_at,
        timezone = p.timezone
    )

    constructor(started_at : ZonedDateTime,
                ended_at: ZonedDateTime,
                rrule: String,
                exrules: List<String>)
    :this(
        started_at = started_at,
        ended_at = ended_at,
        duration = Duration.between(started_at, ended_at),
        exrules = exrules.map { RruleStructure(it) },
        // todo
        rrule = "",
        timezone = started_at.zone
    )

}

data class TaskRequest(
    val deadline_at: ZonedDateTime,
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