package com.example.calendar.repository.server.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.*

@Parcelize
data class EventPatternServer(
    val id: Long,

    val created_at: ZonedDateTime,
    val updated_at: ZonedDateTime,

    var started_at: ZonedDateTime,
    var duration: Duration,
    var ended_at: ZonedDateTime,
    var exrules: List<EventPatternExruleServer>,
    var rrule: String,
    var timezone: ZoneId
) : Parcelable {

    var patternRequest: PatternRequest
        get() {
            // kotlin error
            if (rrule == null) { rrule = ""}
            return PatternRequest(
                started_at = started_at,
                duration = duration,
                ended_at = ended_at,
                exrules = exrules.map
                { RruleStructure(it.rule) },
                rrule = rrule,
                timezone = timezone
            )
        }
        set(value) {
            started_at = value.started_at
            duration = value.duration
            ended_at = value.ended_at
            timezone = value.timezone
            rrule = value.rrule
            // todo exrules
            // exrules = value.exrules
        }
}
