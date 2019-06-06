package com.example.calendar.data

import com.example.calendar.helpers.fromDateTimeUTC
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

    private fun setUntil(event: EventRecurrence, until: ZonedDateTime) {
        if (event.isRecurrence()) {
            val recurrence = RecurrenceRule(event.rrule)
            recurrence.until = toDateTimeUTC(until)
            event.rrule = recurrence.toString()
        }
    }

    private fun updateAllSimple(event: EventInstance) {
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
        event.startedAtNotUpdate = event.startedAtInstance
    }

    private fun updateFutureSimple(event: EventInstance) {
        val eventRecList = dao.getEventById(event.idEventRecurrence)
        if (eventRecList.isEmpty()) {
            throw LoginException("Event doesn't exist")
        }
        val eventRecurrence = eventRecList[0]

        if (!eventRecurrence.isRecurrence() ||
            eventRecurrence.startedAt == event.startedAtNotUpdate) {
            updateAllSimple(event)
            return
        }

        val rule = RecurrenceRule(eventRecurrence.rrule)
        val newEventRecurrence = EventRecurrence(
            event.nameEventRecurrence,
            event.noteEventRecurrence,
            event.startedAtInstance,
            event.duration,
            event.rrule
        )

        if (rule.isInfinite) {
            setUntil(eventRecurrence, event.startedAtNotUpdate)
        } else if (rule.until != null) {
            setUntil(eventRecurrence, event.startedAtNotUpdate)
        } else if (rule.count != null) {
            val count = rule.count
            var countRecurrence = 0

            val it = rule.iterator(toDateTimeUTC(eventRecurrence.startedAt))
            while (it.hasNext()) {
                val startInstance = it.nextDateTime()
                if (fromDateTimeUTC(startInstance) >= event.startedAtNotUpdate) {
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
                RecurrenceRule(event.rrule).count != null) {

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
        event.startedAtNotUpdate = event.startedAtInstance
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
            eventRecurrence.startedAt == event.startedAtNotUpdate) {
            deleteAllSimple(event)
            return
        }

        val rule = RecurrenceRule(eventRecurrence.rrule)
        if (rule.isInfinite) {
            setUntil(eventRecurrence, event.startedAtNotUpdate)
        } else if (rule.until != null) {
            setUntil(eventRecurrence, event.startedAtNotUpdate)
        } else if (rule.count != null) {
            val count = rule.count
            var countRecurrence = 0

            val it = rule.iterator(toDateTimeUTC(eventRecurrence.startedAt))
            while (it.hasNext()) {
                val startInstance = it.nextDateTime()
                if (fromDateTimeUTC(startInstance) >= event.startedAtNotUpdate) {
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
}