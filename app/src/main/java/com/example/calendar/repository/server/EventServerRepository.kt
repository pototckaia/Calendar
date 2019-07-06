package com.example.calendar.repository.server

import com.example.calendar.helpers.betweenIncludeMillis
import com.example.calendar.helpers.toLongUTC
import com.example.calendar.repository.server.model.Event
import com.example.calendar.repository.server.model.EventInstance
import com.example.calendar.repository.server.model.EventRequest
import com.example.calendar.repository.server.model.PatternRequest
import io.reactivex.Completable
import io.reactivex.Observable
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

class EventServerRepository(val api: PlannerApi) : EventRepository {

    override fun fromTo(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Observable<List<EventInstance>> {
        val startUTC = toLongUTC(startLocal.withZoneSameInstant(ZoneOffset.UTC))
        val endUTC = toLongUTC(endLocal.withZoneSameInstant(ZoneOffset.UTC))

        return api.getEventsInstancesFromTo(startUTC, endUTC)
            .map {
                val list = it.data
                list.map { e ->
                    val event = api.getEventByIdConsistently(e.event_id)
                    val pattern = api.getPatternByIdConsistently(e.pattern_id)
                    EventInstance(
                        // todo check data
                        entity = event.data[0],
                        pattern = pattern.data[0],
                        started_at = e.started_at,
                        ended_at = e.ended_at
                    )

                }
            }
    }


    private fun daysInEvent(event: EventInstance): List<ZonedDateTime> {
        val dates = arrayListOf<ZonedDateTime>()

        // todo make local timezone or timezone create ????
        dates.add(event.started_at_zoneid)
        var day = ZonedDateTime.from(event.started_at_zoneid)
        val durationZoneId = betweenIncludeMillis(event.started_at_zoneid, event.ended_at_zoneid)
        for (i in 1..durationZoneId.toDays()) {
            day = day.plusDays(1)
                .truncatedTo(ChronoUnit.DAYS)
            dates.add(ZonedDateTime.from(day))
        }
        return dates
    }

    override fun fromToSet(
        startLocal: ZonedDateTime, endLocal: ZonedDateTime
    ): Observable<HashSet<ZonedDateTime>> {
        return fromTo(startLocal, endLocal)
            .flatMap {
                Observable.fromIterable(it)
                    .map { daysInEvent(it) }
                    .flatMapIterable { it }
                    .collect({ hashSetOf<ZonedDateTime>() }, { set, z -> set.add(z) })
                    .toObservable()
            }
    }

    override fun getEventById(eventId: Long): Observable<Event> {
        return Observable.fromCallable {
            // todo check data
            val e = api.getEventByIdConsistently(eventId).data[0]
            // todo make list
            val p = api.getPatternsConsistently(eventId).data[0]
            Event(e, p)
        }
    }

    override fun insertEvent(
        eventRequest: EventRequest, patternRequest: PatternRequest
    ): Completable {
        return Completable.fromCallable {
            // todo check data
            val eventId = api.createEventConsistently(eventRequest).data[0].id
            api.createPatternConsistently(eventId, patternRequest)
            true
        }
    }

    override fun updateAll(event: EventInstance): Completable {
        return api.updateEvent(event.entity.id, EventRequest(event.entity))
            .ignoreElements()
            .mergeWith(
                api.updatePattern(event.pattern.id, PatternRequest(event.pattern))
                    .ignoreElements()
            )
    }

    override fun deleteAll(event: EventInstance): Completable {
        return api.deletePatternById(event.pattern.id)
            .ignoreElements()
            .andThen(
                api.deleteEventById(event.entity.id)
                    .ignoreElements()
            )
    }
}