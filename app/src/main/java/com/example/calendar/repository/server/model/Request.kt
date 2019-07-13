package com.example.calendar.repository.server.model

import android.os.Parcel
import android.os.Parcelable
import com.example.calendar.helpers.toDateTimeUTC
import com.example.calendar.repository.*
import com.example.calendar.repository.db.convert.DurationConverter
import com.example.calendar.repository.db.convert.ZonedDateTimeConverter
import com.example.calendar.repository.db.convert.ZoneIdConverter
import kotlinx.android.parcel.Parcelize
import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.*
import java.util.*


@Parcelize
data class RruleStructure(
    var rrule: String
) : Parcelable


@Parcelize
data class EventRequest(
    val details: String,
    val location: String,
    val name: String,
    val status: String
) : Parcelable {

    constructor(entity: EventServer)
            : this(
        details = entity.details,
        location = entity.location,
        name = entity.name,
        status = entity.status
    )
}

@Parcelize
data class PatternRequest(
    var duration: Duration,
    var ended_at: ZonedDateTime,
    val exrules: List<RruleStructure>,
    var rrule: String,
    var started_at: ZonedDateTime,
    var timezone: ZoneId
) : Parcelable {

    val startedAtTimezone: ZonedDateTime
        get() = started_at.withZoneSameInstant(timezone)

    val endedAtAtTimezone: ZonedDateTime
        get() = started_at.plus(duration).withZoneSameInstant(timezone)

    constructor(
        started_at: ZonedDateTime,
        ended_at: ZonedDateTime,
        rrule: String,
        exrules: List<String>,
        timezone: ZoneId
    ) : this(
        // todo check duration
        started_at = started_at.withZoneSameInstant(ZoneOffset.UTC),
        duration = Duration.between(started_at, ended_at),
        exrules = exrules.map { RruleStructure(it) },
        timezone = timezone,
        rrule = rrule,
        ended_at = calculateEndedAt(started_at, Duration.between(started_at, ended_at), rrule)
    )

    fun getRecurrenceRuleWithZoneId(): RecurrenceRule? {
        if (!isRecurrence(rrule)) {
            return null
        }
        val r = RecurrenceRule(rrule)
        if (r.until != null) {
            r.until = DateTime(
                DateTime.GREGORIAN_CALENDAR_SCALE, DateTimeUtils.toTimeZone(timezone), r.until
            )
        }
        return r
    }

    fun removeRecurrence() {
        rrule = ""
        ended_at = calculateEndedAt(started_at, duration, rrule)
    }

    fun setStartedAt(s: ZonedDateTime) {
        started_at = s.withZoneSameInstant(ZoneOffset.UTC)
    }

    fun setDuration(e: ZonedDateTime) {
        duration = Duration.between(started_at, e.withZoneSameInstant(ZoneOffset.UTC))
    }

    fun setRecurrence(r: String) {
        if (r.isEmpty()) {
            removeRecurrence()
            return
        }

        val recurrence = RecurrenceRule(r)
        // until to UTF
        if (recurrence.until != null) {
            recurrence.until = DateTime(
                DateTime.GREGORIAN_CALENDAR_SCALE, TimeZone.getTimeZone("UTC"), recurrence.until
            )
        }
        rrule = recurrence.toString()
        ended_at = calculateEndedAt(started_at, duration, rrule)
    }

    fun setUntil(until: ZonedDateTime) {
        if (isRecurrence(rrule)) {
            val recurrence = RecurrenceRule(rrule)
            recurrence.until = toDateTimeUTC(until.withZoneSameInstant(ZoneOffset.UTC))

            rrule = recurrence.toString()
            ended_at = calculateEndedAt(started_at, duration, rrule)
        }
    }
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