package com.example.calendar.repository.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.calendar.repository.db.convert.DurationConverter
import com.example.calendar.repository.db.convert.ZonedDateTimeConverter
import com.example.calendar.repository.db.convert.ZoneIdConverter
import com.example.calendar.helpers.*
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.Duration
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import java.util.UUID
import org.dmfs.rfc5545.DateTime
import org.threeten.bp.ZoneId


@Entity(tableName = "eventsRecurrence")
@TypeConverters(ZonedDateTimeConverter::class, DurationConverter::class, ZoneIdConverter::class)
data class EventRecurrence(
    @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "note") var note: String,

    // in UTF
    @ColumnInfo(name = "started_at") var startedAt: ZonedDateTime,
    // in minute
    // [start, end)
    @ColumnInfo(name = "duration") var duration: Duration,
    // for all recurrence entity or endOutRecurrence for single entity
    @ColumnInfo(name = "end_out_of_range") var endOutRecurrence: ZonedDateTime,

    @ColumnInfo(name="zone_id") var zoneId: ZoneId,

    // todo add converter
    @ColumnInfo(name = "recurrencePattern") var rrule: String = ""
) {

    val startedAtLocal : ZonedDateTime
        get() = startedAt.withZoneSameInstant(zoneId)

    init {
        startedAt = startedAt.withZoneSameInstant(ZoneOffset.UTC)
        endOutRecurrence = endOutRecurrence.withZoneSameInstant(ZoneOffset.UTC)
    }

    constructor(name: String, note: String,
                startedAt: ZonedDateTime, duration: Duration, rrule: String,
                id: String = UUID.randomUUID().toString()) :
            this(
                id = id,
                name = name,
                note = note,
                startedAt = startedAt,
                duration = duration,
                endOutRecurrence = startedAt,
                zoneId = startedAt.zone,
                rrule = rrule) {
        endOutRecurrence = calculateEndOutOfRange(this.startedAt, this.duration, this.rrule)
    }

    fun isRecurrence(): Boolean {
        return rrule.isNotEmpty()
    }

    fun getRecurrenceRule(): RecurrenceRule? {
        if (!isRecurrence()) { return null }

        val r = RecurrenceRule(rrule)
        if (r.until != null) {
            r.until =
                DateTime(DateTime.GREGORIAN_CALENDAR_SCALE, toTimeZone(zoneId), r.until)
        }
        return r
    }

    private fun calculateEndOutOfRange(startedAt: ZonedDateTime, duration: Duration, rrule: String): ZonedDateTime {
        val maxDate = ZonedDateTime.of(9999, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC)
        var endEvent = startedAt.plus(duration)

        if (!isRecurrence()) {
            return endEvent
        }


        val recurrence = RecurrenceRule(rrule)
        if (recurrence.isInfinite) {
            endEvent = maxDate
        }
        else if (recurrence.until != null) {
            // the RRULE includes an UNTIL
            endEvent = max(fromDateTimeUTC(recurrence.until), endEvent)
        }

        if (recurrence.count != null) {
            // The RRULE has a limit, so calculate
            var startRec = toDateTimeUTC(startedAt)
            val it = recurrence.iterator(startRec)
            if (it.hasNext()) {
                it.skipAllButLast()
                startRec = it.nextDateTime()
            }
            val startZone = fromDateTimeUTC(startRec)

            endEvent = max(startZone.plus(duration), endEvent)
        }

        return endEvent

    }
}
