package com.example.calendar.data

import java.util.*
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.dmfs.rfc5545.DateTime
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime


fun max(d1: ZonedDateTime, d2: ZonedDateTime): ZonedDateTime {
    return if (d1.isAfter(d2)) d1 else d2
}

fun min(d1: ZonedDateTime, d2: ZonedDateTime): ZonedDateTime {
    return if (d1.isBefore(d2)) d1 else d2
}

fun fromDateTimeUTC(d: DateTime): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(d.timestamp), ZoneOffset.UTC)
}

fun toDateTimeUTC(z: ZonedDateTime): DateTime {
    return DateTime(TimeZone.getTimeZone("UTC"), z.toInstant().toEpochMilli())
}



class EventRecurrenceRepository {

    private val maxInstances = 1000;

    fun generateInstances(
        dao: EventRecurrenceDao,
        event: EventRecurrence,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): List<EventInstance> {

        val instances = arrayListOf<EventInstance>()

        val exceptionsList = arrayListOf<DateTime>()
        dao.getExceptionByEventId(event.id).forEach {
            exceptionsList.add(toDateTimeUTC(it.exceptionDate))
        }
        val exceptions = exceptionsList.toSet()

        if (!event.isRecurrence()) {
            val eventInstance = EventInstance(event, event.startedAt)
            instances.plus(eventInstance)
            return instances
        }

        // todo time
        val startRange = toDateTimeUTC(max(event.startedAt, start))
        val endRange = toDateTimeUTC(min(event.endOutRecurrence, end))

        val recurrence = RecurrenceRule(event.rrule)
        val startRecurrence = toDateTimeUTC(event.startedAt)
        val it = recurrence.iterator(startRecurrence)
        var counter = 0
        while (it.hasNext() && (!recurrence.isInfinite || counter > maxInstances)) {
            val startInstance = it.nextDateTime()

            if (startInstance.before(startRange)) {
                continue;
            } else if (startInstance.after(endRange)) {
                break;
            }
            if (exceptions.contains(startInstance)) {
                continue
            }
            val eventInstance = EventInstance(event, fromDateTimeUTC(startInstance))
            instances.plus(eventInstance)
            counter++
        }
        return instances
    }

    fun calculateEndOutOfRange(startedAt: ZonedDateTime, duration: Duration, rrule: String) : ZonedDateTime {
        if (rrule.isNotEmpty()) { // isRecurrence
            val recurrence = RecurrenceRule(rrule)
            // the RRULE includes an UNTIL
            if (recurrence.until != null) {
                return fromDateTimeUTC(recurrence.until)
            } else if (recurrence.count != null) {
                // The RRULE has a limit, so calculate
                var startRec = toDateTimeUTC(startedAt)
                val it = recurrence.iterator(startRec)
                if (it.hasNext()) {
                    it.skipAllButLast()
                    startRec = it.nextDateTime()
                }
                val startZone = fromDateTimeUTC(startRec)
                return startZone.plus(duration)
            } else if (recurrence.isInfinite) {
                val maxDate = ZonedDateTime.of(9999, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC)
                return maxDate
            } else {
                // todo not valid
                return startedAt.plus(duration)
            }
        } else {
            return startedAt.plus(duration)
        }
    }

    fun addUntil(event: EventRecurrence, until: ZonedDateTime) : EventRecurrence {
        val recurrence = RecurrenceRule(event.rrule)
        recurrence.until = toDateTimeUTC(until)
        return EventRecurrence(
            name = event.name,
            note = event.note,
            startedAt = event.startedAt,
            duration = event.duration,
            endOutRecurrence = until,
            rrule = recurrence.toString())
    }

    fun insertNotRecurrenceCopy(dao: EventRecurrenceDao, event: EventInstance) {
        val e = EventRecurrence(
            name = event.nameEventRecurrence,
            note = event.noteEventRecurrence,
            startedAt = event.startedAtInstance,
            duration = event.duration,
            endOutRecurrence = calculateEndOutOfRange(event.startedAtInstance, event.duration, "")
        )
        dao.insert(e)
    }

    fun insertEvent(dao: EventRecurrenceDao, event: EventInstance) {
        val e = EventRecurrence(
            name = event.nameEventRecurrence,
            note = event.noteEventRecurrence,
            startedAt = event.startedAtInstance,
            duration = event.duration,
            endOutRecurrence = calculateEndOutOfRange(event.startedAtInstance, event.duration, event.rrule),
            rrule = event.rrule
        )
        dao.insert(e)
    }

    fun deleteEventSingle(dao: EventRecurrenceDao, event: EventInstance) {
        val eventRec = dao.getEventById(event.idEventRecurrence)
        if (!eventRec.isRecurrence()) {
            deleteEventAll(dao, event)
            return
        }
        dao.addException(event.idEventRecurrence, event.startedAtInstance)
    }

    fun deleteEventFuture(dao: EventRecurrenceDao, event: EventInstance) {
        var eventRec = dao.getEventById(event.idEventRecurrence)
        if (!eventRec.isRecurrence()) {
            deleteEventAll(dao, event)
            return
        }
        // precision todo
        val until = event.startedAtInstance
        eventRec = addUntil(eventRec, until)
        dao.update(eventRec)
    }

    fun deleteEventAll(dao: EventRecurrenceDao, event: EventInstance) {
        val eventRec = dao.getEventById(event.idEventRecurrence)
        dao.delete(eventRec)
    }

    fun updateEventSingle(dao: EventRecurrenceDao, event: EventInstance) {
        val eventRec = dao.getEventById(event.idEventRecurrence)
        if (!eventRec.isRecurrence()) {
            updateEventAll(dao, event)
            return
        }
        insertNotRecurrenceCopy(dao, event)
        dao.addException(eventRec.id, event.startedAtInstance)
    }

    fun updateEventFuture(dao: EventRecurrenceDao, event: EventInstance) {
        var eventRec = dao.getEventById(event.idEventRecurrence)
        if (!eventRec.isRecurrence()) {
            updateEventAll(dao, event)
            return
        }

        // precision todo
        val until = event.startedAtInstance
        eventRec = addUntil(eventRec, until)
        dao.update(eventRec)
        insertEvent(dao, event) // originEndDAte
    }

    fun updateEventAll(dao: EventRecurrenceDao, event: EventInstance) {
        val eventRec = dao.getEventById(event.idEventRecurrence)
        if (event.startAtNotUpdate != event.startedAtInstance) {
            val d = Duration.between(event.startAtNotUpdate, event.startedAtInstance)
            eventRec.startedAt = eventRec.startedAt.plus(d)
        }
        eventRec.name = event.nameEventRecurrence
        eventRec.note = event.noteEventRecurrence
        eventRec.duration = event.duration
        eventRec.rrule = event.rrule
        eventRec.endOutRecurrence = calculateEndOutOfRange(eventRec.startedAt, eventRec.duration, eventRec.rrule)
        dao.update(eventRec)
    }
}