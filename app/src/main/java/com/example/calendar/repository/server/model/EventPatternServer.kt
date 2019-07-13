package com.example.calendar.repository.server.model

import android.os.Parcel
import android.os.Parcelable
import com.example.calendar.helpers.fromDateTimeUTC
import com.example.calendar.helpers.fromLongUTC
import com.example.calendar.helpers.max
import com.example.calendar.helpers.toDateTimeUTC
import com.example.calendar.repository.db.convert.DurationConverter
import com.example.calendar.repository.db.convert.ZonedDateTimeConverter
import com.example.calendar.repository.db.convert.ZoneIdConverter
import com.example.calendar.repository.duration_cn
import com.example.calendar.repository.zoneId_cn
import com.example.calendar.repository.zonedDateTime_cn
import kotlinx.android.parcel.Parcelize
import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.*
import java.util.*

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
