package com.example.calendar.repository.server

import com.example.calendar.repository.server.model.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import org.threeten.bp.ZonedDateTime
import java.io.File
import kotlin.collections.HashSet


interface EventRepository {

    fun fromTo(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Single<List<EventInstance>>

    fun fromToSet(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Single<HashSet<ZonedDateTime>>

    fun getEventById(eventId: Long): Single<Event>

    fun insertEvent(eventRequest: EventRequest, patternRequests: ArrayList<PatternRequest>): Completable

    fun updateEvent(event: EventInstance): Completable

    fun deleteEvent(event: EventInstance): Completable

    fun export(uri: String): Single<ResponseBody>

    fun import(file: File): Completable

    fun getToken(permissions: List<PermissionRequest>): Single<String>

    fun getPermission(user_id: String, permissions: List<PermissionRequest>): Completable

    fun getEventPermissions(
        mine: Boolean, namePermissionAll: String, nameUserNotFind: String
    ): Single<List<PermissionModel>>

    fun revokeEventPermission(event_permissions: List<PermissionModel>): Completable

    fun getUserByEmail(email: String): Single<UserServer>

    fun activateToken(token: String): Completable
}