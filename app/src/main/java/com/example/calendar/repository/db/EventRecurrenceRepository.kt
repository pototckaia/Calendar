package com.example.calendar.repository.db

import com.example.calendar.helpers.*
import io.reactivex.Completable
import io.reactivex.Flowable
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.Duration
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import javax.security.auth.login.LoginException
import kotlin.collections.HashSet


class EventRecurrenceRepository(val dao: EventRecurrenceDao) {

    private val maxInstances = 1000

    private fun isFromPeriod(it: EventInstance, start: ZonedDateTime, end: ZonedDateTime): Boolean {
        // (started_at >= :start and ended_at < :end) or (started_at < :end and ended_at > :start)
        return (it.startedAtLocal >= start && it.endedAtLocal < end) ||
                (it.startedAtLocal < end && it.endedAtLocal > start)
    }

    // [start, end)
    private fun getEventInstances(
        event: EventRecurrence,
        startLocal: ZonedDateTime,
        endLocal: ZonedDateTime
    ): List<EventInstance> {

        val instances = arrayListOf<EventInstance>()

//        val exceptionsList = arrayListOf<DateTime>()
//        dao.getExceptionByEventId(entity.id).forEach {
//            exceptionsList.add(toDateTimeUTC(it.exceptionDate))
//        }
//        val exceptionsSet = exceptionsList.toSet()

        if (!event.isRecurrence()) {
            val eventInstance = EventInstance(event, event.startedAtLocal)
            instances.add(eventInstance)
            return instances
        }

        // todo time
        val startRange = toDateTime(startLocal, toTimeZone(event.zoneId))
        val endRange = toDateTime(endLocal, toTimeZone(event.zoneId))

        val recurrence = event.getRecurrenceRule()!!
        val startRecurrence = toDateTime(event.startedAtLocal, toTimeZone(event.zoneId))

        val it = recurrence.iterator(startRecurrence)
        var counter = 0
        while (it.hasNext() && (!recurrence.isInfinite || counter < maxInstances)) {
            val startInstance = it.nextDateTime()

            if (recurrence.until != null &&
                (startInstance.after(recurrence.until) || startInstance == recurrence.until)
            ) {
                break
            }

            if (startInstance.after(endRange) || startInstance == endRange) {
                break;
            }
//            if (exceptionsSet.contains(startInstance)) {
//                continue
//            }
            val eventInstance = EventInstance(event, fromDateTime(startInstance))

            if (isFromPeriod(eventInstance, startLocal, endLocal)) {
                instances.add(eventInstance)
                counter++
            }
        }
        return instances
    }

    private fun daysInEvent(event: EventInstance): List<ZonedDateTime> {
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
                    .map {
                        getEventInstances(
                            it,
                            startUTC.withZoneSameInstant(it.zoneId),
                            endUTC.withZoneSameInstant(it.zoneId))
                    }
                    .flatMapIterable { it }
                    .toList()
                    .toFlowable()
            }
    }

    fun fromToSet(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Flowable<HashSet<ZonedDateTime>> {
        return fromTo(startLocal, endLocal)
            .flatMap { list ->
                Flowable.fromIterable(list)
                    .map { daysInEvent(it) }
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

    private fun setUntil(event: EventRecurrence, until: ZonedDateTime) {
        if (event.isRecurrence()) {
            val recurrence = RecurrenceRule(event.rrule)
            recurrence.until = toDateTimeUTC(until.withZoneSameInstant(ZoneOffset.UTC))
            event.rrule = recurrence.toString()
        }
    }

    private fun updateAllSimple(event: EventInstance) {
        val eventRecList = dao.getEventById(event.idEventRecurrence)
        if (eventRecList.isEmpty()) {
            throw LoginException("Event doesn't exist")
        }
        val eventRecurrence = eventRecList[0]

        var startedAt = eventRecurrence.startedAtLocal
        if (event.startedAtLocalNotUpdate != event.startedAtLocal) {
            val d = Duration.between(event.startedAtLocalNotUpdate, event.startedAtLocal)
            startedAt = eventRecurrence.startedAtLocal.plus(d)
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
        event.startedAtLocalNotUpdate = event.startedAtLocal
    }

    private fun updateFutureSimple(event: EventInstance) {
        val eventRecList = dao.getEventById(event.idEventRecurrence)
        if (eventRecList.isEmpty()) {
            throw LoginException("Event doesn't exist")
        }
        val eventRecurrence = eventRecList[0]

        if (!eventRecurrence.isRecurrence() ||
            event.startedAtLocalNotUpdate == event.startedAtLocal
        ) {
            updateAllSimple(event)
            return
        }

        val rule = RecurrenceRule(eventRecurrence.rrule)
        val newEventRecurrence = EventRecurrence(
            event.nameEventRecurrence,
            event.noteEventRecurrence,
            event.startedAtLocal,
            event.duration,
            event.rrule
        )

        if (rule.isInfinite) {
            setUntil(eventRecurrence, event.startedAtLocalNotUpdate)
        } else if (rule.until != null) {
            setUntil(eventRecurrence, event.startedAtLocalNotUpdate)
        } else if (rule.count != null) {
            val count = rule.count
            var countRecurrence = 0

            val it = rule.iterator(toDateTime(eventRecurrence.startedAtLocal))
            while (it.hasNext()) {
                val startInstance = it.nextDateTime()
                if (fromDateTime(startInstance) >= event.startedAtLocalNotUpdate) {
                    break
                }
                countRecurrence++
            }

            val countNewRecurrence = count - countRecurrence
            if (countNewRecurrence <= 0 || countRecurrence <= 0) {
                // todo replace
                throw LoginException("What's wrong")
            }

            // if count set
            if (event.isRecurrence() &&
                RecurrenceRule(event.rrule).count != null
            ) {

                val ruleEvent = RecurrenceRule(event.rrule)
                ruleEvent.count = countNewRecurrence
                event.rrule = ruleEvent.toString()
                newEventRecurrence.rrule = event.rrule
            }

            rule.count = countRecurrence
            eventRecurrence.rrule = rule.toString()
        }

        dao.update(eventRecurrence)
        dao.insert(newEventRecurrence)
        event.startedAtLocalNotUpdate = event.startedAtLocal
    }

    private fun deleteAllSimple(event: EventInstance) {
        val eventRecList = dao.getEventById(event.idEventRecurrence)
        if (eventRecList.isEmpty()) {
            throw LoginException("Event doesn't exist")
        }
        val eventRecurrence = eventRecList[0]
        dao.delete(eventRecurrence)
    }

    private fun deleteFutureSimple(event: EventInstance) {
        val eventRecList = dao.getEventById(event.idEventRecurrence)
        if (eventRecList.isEmpty()) {
            throw LoginException("Event doesn't exist")
        }
        val eventRecurrence = eventRecList[0]

        if (!eventRecurrence.isRecurrence() ||
            eventRecurrence.startedAtLocal == event.startedAtLocalNotUpdate) {
            deleteAllSimple(event)
            return
        }

        val rule = RecurrenceRule(eventRecurrence.rrule)
        if (rule.isInfinite) {
            setUntil(eventRecurrence, event.startedAtLocalNotUpdate)
        } else if (rule.until != null) {
            setUntil(eventRecurrence, event.startedAtLocalNotUpdate)
        } else if (rule.count != null) {
            val count = rule.count
            var countRecurrence = 0

            val it = rule.iterator(toDateTime(eventRecurrence.startedAtLocal))
            while (it.hasNext()) {
                val startInstance = it.nextDateTime()
                if (fromDateTime(startInstance) >= event.startedAtLocalNotUpdate) {
                    break
                }
                countRecurrence++
            }

            val countNewRecurrence = count - countRecurrence
            if (countNewRecurrence <= 0 || countRecurrence <= 0) {
                // todo replace
                throw LoginException("What's wrong")
            }
            rule.count = countRecurrence
            eventRecurrence.rrule = rule.toString()
        }

        dao.update(eventRecurrence)
    }

    fun updateAll(event: EventInstance): Completable {
        return Completable.fromRunnable {
            updateAllSimple(event)
        }
    }

    fun updateFuture(event: EventInstance): Completable {
        return Completable.fromRunnable {
            updateFutureSimple(event)
        }
    }

    fun deleteAll(event: EventInstance): Completable {
        return Completable.fromRunnable {
            deleteAllSimple(event)
        }
    }

    fun deleteFuture(event: EventInstance): Completable {
        return Completable.fromRunnable {
            deleteFutureSimple(event)
        }
    }

//
//    fun insertNotRecurrenceCopy(dao: EventRecurrenceDao, entity: EventInstance) {
//        val e = EventRecurrence(addElement
//            name = entity.nameEventRecurrence,
//            note = entity.noteEventRecurrence,
//            startedAt = entity.startedAtInstance,
//            duration = entity.duration,
//            endOutRecurrence = calculateEndOutOfRange(entity.startedAtInstance, entity.duration, "")
//        )
//        dao.insert(e)
//    }
//
//
//    fun deleteEventSingle(dao: EventRecurrenceDao, entity: EventInstance) {
//        val eventRec = dao.getEventById(entity.idEventRecurrence)
//        if (!eventRec.isRecurrence()) {
//            deleteEventAll(dao, entity)
//            return
//        }
//        dao.addException(entity.idEventRecurrence, entity.startedAtInstance)
//    }
//
//    fun deleteEventFuture(dao: EventRecurrenceDao, entity: EventInstance) {
//        var eventRec = dao.getEventById(entity.idEventRecurrence)
//        if (!eventRec.isRecurrence()) {
//            deleteEventAll(dao, entity)
//            return
//        }
//        // precision todo
//        val until = entity.startedAtInstance
//        eventRec = addUntil(eventRec, until)
//        dao.update(eventRec)
//    }
//
//    fun deleteEventAll(dao: EventRecurrenceDao, entity: EventInstance) {
//        val eventRec = dao.getEventById(entity.idEventRecurrence)
//        dao.delete(eventRec)
//    }
//
//    fun updateEventSingle(dao: EventRecurrenceDao, entity: EventInstance) {
//        val eventRec = dao.getEventById(entity.idEventRecurrence)
//        if (!eventRec.isRecurrence()) {
//            updateEventAll(dao, entity)
//            return
//        }
//        insertNotRecurrenceCopy(dao, entity)
//        dao.addException(eventRec.id, entity.startedAtInstance)
//    }
}