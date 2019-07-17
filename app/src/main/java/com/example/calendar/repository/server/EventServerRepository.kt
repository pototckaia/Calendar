package com.example.calendar.repository.server

import android.os.Environment
import android.util.Log
import com.example.calendar.helpers.convert.toLongUTC
import com.example.calendar.helpers.getEventInstances
import com.example.calendar.repository.server.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.threeten.bp.Duration
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.*

class EventServerRepository(val api: PlannerApi) : EventRepository {

//    private fun getEventInstance(e: EventInstanceServer): Observable<EventInstance> {
//        return api.getEventById(e.event_id)
//            .zipWith(
//                api.getPatternById(e.pattern_id),
//                BiFunction { event: EventResponse, pattern: EventPatternResponse ->
//                    EventInstance(
//                        // todo check data
//                        entity = event.data[0],
//                        pattern = pattern.data[0],
//                        started_at = e.started_at,
//                        ended_at = e.ended_at
//                    )
//                }
//            )
//    }
//
//    override fun fromTo(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Observable<List<EventInstance>> {
//        val startUTC = toLongUTC(startLocal.withZoneSameInstant(ZoneOffset.UTC))
//        val endUTC = toLongUTC(endLocal.withZoneSameInstant(ZoneOffset.UTC))
//
//        return api.getEventsInstancesFromTo(startUTC, endUTC)
//            .map { it.data }
//            .flatMap { Observable.fromIterable(it) }
//            .flatMap { getEventInstance(it) }
//            .toList()
//            .toObservable()
//    }

    override fun fromTo(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Observable<List<EventInstance>> {
        val startUTC = toLongUTC(startLocal.withZoneSameInstant(ZoneOffset.UTC))
        val endUTC = toLongUTC(endLocal.withZoneSameInstant(ZoneOffset.UTC))

        return api.getEventsFromTo(startUTC, endUTC)
            .map { it.data }
            .flatMap { Observable.fromIterable(it) }
            .flatMap { entity ->
                api.getPatterns(entity.id)
                    .map { it.data }
                    .map { patterns ->
                        val list = ArrayList<EventInstance>()
                        patterns.forEach { pattern ->
                            list.addAll(getEventInstances(entity, pattern, startLocal, endLocal))
                        }
                        Pair(entity.id, list)
                    }
            }
            .toList()
            .map {
                val list = ArrayList<EventInstance>()
                it.forEach { l -> list.addAll(l.second) }
                list as List<EventInstance>
            }
            .toObservable()
    }


    private fun daysInEvent(event: EventInstance): List<ZonedDateTime> {
        val dates = arrayListOf<ZonedDateTime>()

        dates.add(event.started_at_local)
        var day = ZonedDateTime.from(event.started_at_local)
        val durationZoneId = Duration.between(event.started_at_local, event.ended_at_local)
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
                api.updatePattern(event.pattern.id, event.pattern.patternRequest)
                    .ignoreElements()
            )
    }

    override fun deleteAll(event: EventInstance): Completable {
        return api.deletePatternById(event.pattern.id)
            .ignoreElements()
//            .andThen(
//                api.deleteEventById(event.entity.id)
//                    .ignoreElements()
//            )
    }

    override fun export(uri: String): Observable<ResponseBody> {
        return api.exportICal()
    }

    override fun import(file: File): Completable {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return api.importICal(body)
    }

    override fun getLink(permissions: List<PermissionRequest>) : Observable<String> {
        return api.getLink(permissions)
            .map { it.string() }
    }

}
