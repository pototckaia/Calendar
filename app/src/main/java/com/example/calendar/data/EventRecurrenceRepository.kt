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
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import javax.security.auth.login.LoginException


class EventRecurrenceRepository(val dao: EventRecurrenceDao) {

    private val maxInstances = 1000

    private fun isFromPeriod(it: EventInstance, start: ZonedDateTime, end: ZonedDateTime): Boolean {
        // (started_at >= :start and ended_at < :end) or (started_at < :end and ended_at > :start)
        return (it.startedAtInstance >= start && it.endedAtInstance < end) ||
                (it.startedAtInstance < end && it.endedAtInstance > start)
    }

    // [startUTC, endUTC)
    private fun getEventInstances(
        event: EventRecurrence,
        startUTC: ZonedDateTime,
        endUTC: ZonedDateTime
    ): List<EventInstance> {

        val instances = arrayListOf<EventInstance>()

        val exceptionsList = arrayListOf<DateTime>()
        dao.getExceptionByEventId(event.id).forEach {
            exceptionsList.add(toDateTimeUTC(it.exceptionDate))
        }
        val exceptionsSet = exceptionsList.toSet()

        if (!event.isRecurrence()) {
            val eventInstance = EventInstance(event, event.startedAt)
            instances.add(eventInstance)
            return instances
        }

        // todo time
        val startRange = toDateTimeUTC(startUTC)
        val endRange = toDateTimeUTC(endUTC)

        val recurrence = RecurrenceRule(event.rrule)
        val startRecurrence = toDateTimeUTC(event.startedAt)
        val it = recurrence.iterator(startRecurrence)

        var counter = 0
        while (it.hasNext() && (!recurrence.isInfinite || counter > maxInstances)) {
            val startInstance = it.nextDateTime()

            if (recurrence.until != null &&
                (startInstance.after(recurrence.until) || startInstance == recurrence.until)
            ) {
                break
            }

            if (startInstance.after(endRange) || startInstance == endRange) {
                break;
            }
            if (exceptionsSet.contains(startInstance)) {
                continue
            }
            val eventInstance = EventInstance(event, fromDateTimeUTC(startInstance))
            if (isFromPeriod(eventInstance, startUTC, endUTC)) {
                instances.add(eventInstance)
                counter++
            }
        }
        return instances
    }

    private fun daysInEventLocal(event: EventInstance): List<ZonedDateTime> {
        val dates = arrayListOf<ZonedDateTime>()
        dates.add(event.startedAtLocal)
        var s = ZonedDateTime.from(event.startedAtLocal)

        for (i in 1..event.duration.toDays()) {
            s = s.plusDays(1)
                .truncatedTo(ChronoUnit.DAYS)
            dates.add(ZonedDateTime.from(s))
        }
        return dates
    }

    fun fromTo(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Flowable<List<EventInstance>> {
        val startUTC = startLocal.withZoneSameInstant(ZoneOffset.UTC)
        val endUTC = endLocal.withZoneSameInstant(ZoneOffset.UTC)
        return dao.fromToRx(startUTC, endUTC)
            .flatMap { list ->
                Flowable.fromIterable(list)
                    .map { getEventInstances(it, startUTC, endUTC) }
                    .flatMapIterable { it }
                    .toList()
                    .toFlowable()
            }
    }

    fun fromToSetLocal(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Flowable<HashSet<ZonedDateTime>> {
        return fromTo(startLocal, endLocal)
            .flatMap { list ->
                Flowable.fromIterable(list)
                    .map { daysInEventLocal(it) }
                    .flatMapIterable { it }
                    .collect({ hashSetOf<ZonedDateTime>() }, { set, z -> set.add(z) })
                    .toFlowable()
            }
    }

    fun getEventById(eventId: String): Flowable<List<EventRecurrence>> {
        return dao.getEventByIdRx(eventId)
    }

    fun insertEvent(event: EventRecurrence): Completable {
        return dao.insertRx(event)
    }

    fun updateAllEvent(event: EventInstance): Completable {
        return Completable.fromRunnable()
        {
            val eventRecList = dao.getEventById(event.idEventRecurrence)
            if (eventRecList.isEmpty()) {
                throw LoginException("Event doesn't exist")
            }
            val eventRecurrence = eventRecList[0]

            var startedAt = eventRecurrence.startedAt
            if (event.startedAtNotUpdate != event.startedAtInstance) {
                val d = Duration.between(event.startedAtNotUpdate, event.startedAtInstance)
                startedAt = eventRecurrence.startedAt.plus(d)
            }

            val eventRec = EventRecurrence(
                event.nameEventRecurrence,
                event.noteEventRecurrence,
                startedAt,
                event.duration,
                event.rrule,
                event.idEventRecurrence
            )
            dao.update(eventRec)
        }


    }

    fun deleteAllEvent(event: EventInstance) : Completable {
        return Completable.fromRunnable() {
            val eventRecList = dao.getEventById(event.idEventRecurrence)
            if (eventRecList.isEmpty()) {
                throw LoginException("Event doesn't exist")
            }
            val eventRecurrence = eventRecList[0]
            dao.delete(eventRecurrence)
        }
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

}