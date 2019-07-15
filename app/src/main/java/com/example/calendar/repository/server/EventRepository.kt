package com.example.calendar.repository.server

import com.example.calendar.repository.server.model.Event
import com.example.calendar.repository.server.model.EventInstance
import com.example.calendar.repository.server.model.EventRequest
import com.example.calendar.repository.server.model.PatternRequest
import io.reactivex.Completable
import io.reactivex.Observable
import org.threeten.bp.ZonedDateTime
import kotlin.collections.HashSet


interface EventRepository {

    fun fromTo(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Observable<List<EventInstance>>

    fun fromToSet(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Observable<HashSet<ZonedDateTime>>

    fun getEventById(eventId: Long): Observable<Event>

    fun insertEvent(eventRequest: EventRequest, patternRequests: ArrayList<PatternRequest>): Completable

    fun updateAll(event: EventInstance): Completable

//    fun updateFuture(entity: EventInstance): Completable

    fun deleteAll(event: EventInstance): Completable

//    fun deleteFuture(entity: EventInstance): Completable
}