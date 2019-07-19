package com.example.calendar.repository.server.model

import android.os.Parcelable
import com.example.calendar.helpers.calculateEndedAt
import com.example.calendar.helpers.convert.toDateTimeUTC
import com.example.calendar.helpers.equalMaxTime
import com.example.calendar.helpers.isRecurrence
import kotlinx.android.parcel.Parcelize
import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.*
import java.lang.IllegalArgumentException
import java.util.*


@Parcelize
data class RruleStructure(
    var rrule: String?
) : Parcelable


@Parcelize
data class EventRequest(
    val details: String?,
    val location: String?,
    val name: String?,
    val status: String?
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
    var rrule: String?,
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
        rrule: String?,
        exrules: List<String>,
        timezone: ZoneId
    ) : this(
        started_at = started_at.withZoneSameInstant(ZoneOffset.UTC),
        duration = Duration.between(
            started_at.withZoneSameInstant(ZoneOffset.UTC),
            ended_at.withZoneSameInstant(ZoneOffset.UTC)
        ),
        exrules = exrules.map { RruleStructure(it) },
        timezone = timezone,
        rrule = rrule,
        ended_at = calculateEndedAt(
            started_at.withZoneSameInstant(ZoneOffset.UTC),
            Duration.between(started_at, ended_at),
            rrule
        )
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
        rrule = null
        ended_at = calculateEndedAt(started_at, duration, rrule)
    }

    fun setStartedAt(s: ZonedDateTime) {
        started_at = s.withZoneSameInstant(ZoneOffset.UTC)
        ended_at = calculateEndedAt(started_at, duration, rrule)
    }

    fun set_duration(e: ZonedDateTime) {
        set_duration(Duration.between(started_at, e.withZoneSameInstant(ZoneOffset.UTC)))
    }

    fun set_duration(d: Duration) {
        duration = d
        ended_at = calculateEndedAt(started_at, duration, rrule)
    }

    fun setRecurrence(r: String?) {
        if (r == null) {
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

    companion object {

        fun getPatternToSend(pattern: PatternRequest) : PatternRequest {
            val rrule = pattern.rrule
            if (rrule != null && rrule.isEmpty()) {
                pattern.rrule = null
            }
            if (!isRecurrence(pattern.rrule)) {
                return pattern
            }
            val r = RecurrenceRule(pattern.rrule)
            if (r.until != null) {
                r.until = null
                pattern.rrule = r.toString()
            }
            return pattern
        }

        fun getPatternFromReceive(pattern: PatternRequest): PatternRequest {
            if (!isRecurrence(pattern.rrule)) {
                return pattern
            }
            val r = RecurrenceRule(pattern.rrule)
            if (r.count == null && !equalMaxTime(pattern.ended_at)) {
                r.until = toDateTimeUTC(pattern.ended_at)
                pattern.rrule = r.toString()
            }
            return pattern
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

enum class PermissionAction(val title: String) {
    READ("Чтение"), UPDATE("Обновление"), DELETE("Удаление");
}

enum class EntityType {
    EVENT, PATTERN, TASK;
}

@Parcelize
data class PermissionRequest(
    val action: PermissionAction,
    val entity_id: Long?,
    val entity_type: EntityType
) : Parcelable