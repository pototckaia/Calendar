package com.example.calendar.data

import com.example.calendar.helpers.fromDateTimeUTC
import com.example.calendar.helpers.max
import com.example.calendar.helpers.min
import com.example.calendar.helpers.toDateTimeUTC
import io.reactivex.Completable
import io.reactivex.Flowable
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.dmfs.rfc5545.DateTime
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import io.reactivex.internal.operators.single.SingleInternalHelper.toObservable


class EventRecurrenceRepository(val dao: EventRecurrenceDao) {

    private val maxInstances = 1000;

    private fun getEventInstances(
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
        it.fastForward(startRange) // go to a specific date
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

    fun fromTo(start: ZonedDateTime, end: ZonedDateTime): Flowable<List<EventInstance>> {
        return dao.fromToRx(start, end)
            .flatMap { list ->
                Flowable.fromIterable(list)
                    .map { getEventInstances(it, start, end) }
                    .flatMapIterable { it }
                    .toList()
                    .toFlowable()
            }
    }

    fun fromToRec(start: ZonedDateTime, end: ZonedDateTime): Flowable<List<EventRecurrence>> {
        return dao.fromToRx(start, end)
    }

        fun getEventById(eventId: String): List<EventRecurrence> {
        return dao.getEventById(eventId)
    }

    fun insertEvent(event: EventRecurrence): Completable {
        return dao.insertRx(event)
    }

    fun insertEvents(event: List<EventRecurrence>): Completable {
        return dao.insertRx(event)
    }

//
//    fun insertNotRecurrenceCopy(dao: EventRecurrenceDao, event: EventInstance) {
//        val e = EventRecurrence(addElement
//            name = event.nameEventRecurrence,
//            note = event.noteEventRecurrence,
//            startedAt = event.startedAtInstance,
//            duration = event.duration,
//            endOutRecurrence = calculateEndOutOfRange(event.startedAtInstance, event.duration, "")
//        )
//        dao.insert(e)
//    }
//
//
//    fun deleteEventSingle(dao: EventRecurrenceDao, event: EventInstance) {
//        val eventRec = dao.getEventById(event.idEventRecurrence)
//        if (!eventRec.isRecurrence()) {
//            deleteEventAll(dao, event)
//            return
//        }
//        dao.addException(event.idEventRecurrence, event.startedAtInstance)
//    }
//
//    fun deleteEventFuture(dao: EventRecurrenceDao, event: EventInstance) {
//        var eventRec = dao.getEventById(event.idEventRecurrence)
//        if (!eventRec.isRecurrence()) {
//            deleteEventAll(dao, event)
//            return
//        }
//        // precision todo
//        val until = event.startedAtInstance
//        eventRec = addUntil(eventRec, until)
//        dao.update(eventRec)
//    }
//
//    fun deleteEventAll(dao: EventRecurrenceDao, event: EventInstance) {
//        val eventRec = dao.getEventById(event.idEventRecurrence)
//        dao.delete(eventRec)
//    }
//
//    fun updateEventSingle(dao: EventRecurrenceDao, event: EventInstance) {
//        val eventRec = dao.getEventById(event.idEventRecurrence)
//        if (!eventRec.isRecurrence()) {
//            updateEventAll(dao, event)
//            return
//        }
//        insertNotRecurrenceCopy(dao, event)
//        dao.addException(eventRec.id, event.startedAtInstance)
//    }
//
//    fun updateEventFuture(dao: EventRecurrenceDao, event: EventInstance) {
//        var eventRec = dao.getEventById(event.idEventRecurrence)
//        if (!eventRec.isRecurrence()) {
//            updateEventAll(dao, event)
//            return
//        }
//
//        // precision todo
//        val until = event.startedAtInstance
//        eventRec = addUntil(eventRec, until)
//        dao.update(eventRec)
//        insertEvent(dao, event) // originEndDAte
//    }
//
//    fun updateEventAll(dao: EventRecurrenceDao, event: EventInstance) {
//        val eventRec = dao.getEventById(event.idEventRecurrence)
//        if (event.startAtNotUpdate != event.startedAtInstance) {
//            val d = Duration.between(event.startAtNotUpdate, event.startedAtInstance)
//            eventRec.startedAt = eventRec.startedAt.plus(d)
//        }
//        eventRec.name = event.nameEventRecurrence
//        eventRec.note = event.noteEventRecurrence
//        eventRec.duration = event.duration
//        eventRec.rrule = event.rrule
//        eventRec.endOutRecurrence = calculateEndOutOfRange(eventRec.startedAt, eventRec.duration, eventRec.rrule)
//        dao.update(eventRec)
//    }
}