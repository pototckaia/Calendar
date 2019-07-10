package com.example.calendar.repository.server

import com.example.calendar.helpers.toLongUTC
import com.example.calendar.repository.server.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

class EventServerRepository(val api: PlannerApi) : EventRepository {

    private fun getEventInstance(e: EventInstanceServer): Observable<EventInstance> {
        return api.getEventById(e.event_id)
            .zipWith(
                api.getPatternById(e.pattern_id),
                BiFunction { event: EventResponse, pattern: EventPatternResponse ->
                    EventInstance(
                        // todo check data
                        entity = event.data[0],
                        pattern = pattern.data[0],
                        started_at = e.started_at,
                        ended_at = e.ended_at
                    )
                }
            )
    }

    override fun fromTo(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Observable<List<EventInstance>> {
        val startUTC = toLongUTC(startLocal.withZoneSameInstant(ZoneOffset.UTC))
        val endUTC = toLongUTC(endLocal.withZoneSameInstant(ZoneOffset.UTC))

        return api.getEventsInstancesFromTo(startUTC, endUTC)
            .map { it.data }
            .flatMap { Observable.fromIterable(it) }
            .flatMap { getEventInstance(it) }
            .toList()
            .toObservable()
    }


    private fun daysInEvent(event: EventInstance): List<ZonedDateTime> {
        val dates = arrayListOf<ZonedDateTime>()

        // todo local or timezone
        dates.add(event.started_at_zoneid)
        var day = ZonedDateTime.from(event.started_at_zoneid)
        // todo check between
        val durationZoneId = Duration.between(event.started_at_zoneid, event.ended_at_zoneid)
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
        return api.getEventById(eventId)
            .zipWith(
                api.getPatterns(eventId),
                BiFunction { e: EventResponse, p: EventPatternResponse ->
                    Event(e.data[0], p.data)
                }
            )
    }

    override fun insertEvent(
        eventRequest: EventRequest, patternRequests: ArrayList<PatternRequest>
    ): Completable {
        return api.createEvent(eventRequest)
            .flatMap {
                val eventId = it.data[0].id
                val observables = patternRequests.map {
                    api.createPattern(eventId, it)
                }
                Observable.merge(observables)
            }
            .ignoreElements()
    }

    override fun updateAll(event: EventInstance): Completable {
        return api.updateEvent(event.entity.id, EventRequest(event.entity))
            .ignoreElements()
            .mergeWith(
                api.updatePattern(event.pattern.id, event.pattern.getPatternRequest())
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