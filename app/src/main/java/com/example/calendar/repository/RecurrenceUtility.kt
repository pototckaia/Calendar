package com.example.calendar.repository

import com.example.calendar.helpers.fromDateTimeUTC
import com.example.calendar.helpers.max
import com.example.calendar.helpers.toDateTimeUTC
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

fun isRecurrence(rrule: String) = rrule.isNotEmpty()

fun calculateEndedAt(
    started_at: ZonedDateTime,
    duration: Duration,
    rrule: String
): ZonedDateTime {
    val maxDate = zonedDateTime_cn.toZonedDateTime(Long.MAX_VALUE)
    // todo check duration
    var newEndedAt = started_at.plus(duration)

    if (!isRecurrence(rrule)) {
        return newEndedAt
    }

    val recurrence = RecurrenceRule(rrule)
    if (recurrence.isInfinite) {
        newEndedAt = maxDate
    } else if (recurrence.until != null) {
        // the RRULE includes an UNTIL
        newEndedAt = max(fromDateTimeUTC(recurrence.until), newEndedAt)
    } else if (recurrence.count != null) {
        // The RRULE has a limit, so calculate
        var startedAtDateTime = toDateTimeUTC(started_at)
        val it = recurrence.iterator(startedAtDateTime)
        if (it.hasNext()) {
            it.skipAllButLast()
            startedAtDateTime = it.nextDateTime()
        }
        val startedAtZonedDate = fromDateTimeUTC(startedAtDateTime)
        newEndedAt = max(startedAtZonedDate.plus(duration), newEndedAt)
    }

    return newEndedAt
}