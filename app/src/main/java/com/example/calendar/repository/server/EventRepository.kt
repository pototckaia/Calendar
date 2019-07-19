package com.example.calendar.repository.server

import com.example.calendar.repository.server.model.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import org.threeten.bp.ZonedDateTime
import java.io.File
import kotlin.collections.HashSet


interface EventRepository {

    // from to

    fun fromTo(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Single<List<EventInstance>>

    fun fromToSet(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Single<HashSet<ZonedDateTime>>

    // event

    fun getEventWithPatter(event_id: Long): Single<Pair<EventServer, List<EventPatternServer>>>

    fun getEventById(eventId: Long): Single<Event>

    fun insertEvent(eventRequest: EventRequest, patternRequests: ArrayList<PatternRequest>): Completable

    fun updateEvent(event: EventServer): Completable

    fun deleteEvent(event_id: Long): Completable

    // pattern

    fun getPatterns(event_id: Long): Single<List<EventPatternServer>>

    fun createPatterns(event_id: Long, patternRequests: List<PatternRequest>): Completable

    fun updatePatterns(patterns: List<EventPatternServer>): Completable

    fun deletePatterns(patterns_id: List<Long>): Completable

    // import/export

    fun export(uri: String): Single<ResponseBody>

    fun import(file: File): Completable

    // permission

    fun getToken(permissions: List<PermissionRequest>): Single<String>

    fun activateToken(token: String): Completable

    fun getPermission(user_id: String, permissions: List<PermissionRequest>): Completable

    fun getEventPermissions(
        mine: Boolean, namePermissionAll: String, nameUserNotFind: String
    ): Single<List<PermissionModel>>

    fun getPermissionsById(entity_id: Long, entity_type: EntityType) : Single<List<PermissionServer>>

    fun revokeEventPermission(event_permissions: List<PermissionModel>): Completable

    // user

    fun getUser(user_id: String?, email: String?): Single<UserServer>
}