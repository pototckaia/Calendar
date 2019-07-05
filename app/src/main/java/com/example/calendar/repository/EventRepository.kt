package com.example.calendar.repository

import com.example.calendar.repository.server.Event
import com.example.calendar.repository.server.EventInstance
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import org.threeten.bp.ZonedDateTime
import kotlin.collections.HashSet


interface EventRecurrenceRepository {

    fun fromTo(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Observable<List<EventInstance>>

    fun fromToSet(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Observable<HashSet<ZonedDateTime>>

    fun getEventById(eventId: String): Flowable<List<Event>>

    fun insertEvent(event: Event): Completable

    fun updateAll(event: EventInstance): Completable

    fun updateFuture(event: EventInstance): Completable

    fun deleteAll(event: EventInstance): Completable

    fun deleteFuture(event: EventInstance): Completable
}