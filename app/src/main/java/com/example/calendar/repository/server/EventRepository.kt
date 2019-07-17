package com.example.calendar.repository.server

import com.example.calendar.repository.server.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.threeten.bp.ZonedDateTime
import retrofit2.http.Body
import java.io.File
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

    fun export(uri: String): Observable<ResponseBody>

    fun import(file: File): Completable

    fun getLink(permissions: List<PermissionRequest>) : Observable<String>
}