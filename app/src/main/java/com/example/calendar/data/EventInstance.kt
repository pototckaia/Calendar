package com.example.calendar.data

import org.threeten.bp.Duration
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime

class EventInstance(
    val idEventRecurrence: String,
    var nameEventRecurrence: String,
    var noteEventRecurrence: String,

    // UTF
    start: ZonedDateTime,
    var duration: Duration,
    var rrule: String = ""
) {
    var startedAtInstance: ZonedDateTime = start.withZoneSameInstant(ZoneOffset.UTC)
    val startAtNotUpdate: ZonedDateTime = start.withZoneSameInstant(ZoneOffset.UTC)


    constructor(e: EventRecurrence, start: ZonedDateTime) :
            this(idEventRecurrence = e.id,
                nameEventRecurrence =  e.name,
                noteEventRecurrence =  e.note,
                start = start,
                duration = e.duration,
                rrule = e.rrule)
}


