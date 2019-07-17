package com.example.calendar.repository.server.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.Duration
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime


data class Event(
    val entity: EventServer,
    val pattern: List<EventPatternServer>
)

@Parcelize
data class EventInstance(
    val entity: EventServer,
    val pattern: EventPatternServer,

    val user: UserServer,

    // [start, end]
    var started_at: ZonedDateTime,
    var ended_at: ZonedDateTime
) : Parcelable {

    init {
        started_at = started_at.withZoneSameInstant(ZoneId.systemDefault())
        ended_at = ended_at.withZoneSameInstant(ZoneId.systemDefault())
    }

    val started_at_zoneid: ZonedDateTime
        get() = started_at.withZoneSameInstant(pattern.timezone)

    val started_at_local: ZonedDateTime
        get() = started_at.withZoneSameInstant(ZoneId.systemDefault())

    val ended_at_zoneid: ZonedDateTime
        get() = ended_at.withZoneSameInstant(pattern.timezone)

    val ended_at_local: ZonedDateTime
        get() = ended_at.withZoneSameInstant(ZoneId.systemDefault())

    fun setStartedAt(s: ZonedDateTime) {
        val newStartedAt = s.withZoneSameInstant(ZoneOffset.UTC)

        val patternRequest = pattern.patternRequest
        if (newStartedAt != started_at) {
            val d = Duration.between(started_at, newStartedAt)

            patternRequest.setStartedAt(pattern.started_at.plus(d))
            started_at = newStartedAt
            pattern.patternRequest = patternRequest
        }
    }

    fun setEndedAt(e: ZonedDateTime) {
        val newEndedAt = e.withZoneSameInstant(ZoneOffset.UTC)

        val patternRequest = pattern.patternRequest
        if (newEndedAt != ended_at) {
            val newDuration = Duration.between(started_at, newEndedAt)

            patternRequest.set_duration(newDuration)
            ended_at = newEndedAt
            pattern.patternRequest = patternRequest
        }
    }

    fun setEndedAt(e: Duration) {
        val newEndedAt = started_at.plus(e)
        setEndedAt(newEndedAt)
    }
}