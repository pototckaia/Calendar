package com.example.calendar.repository.server

import com.example.calendar.auth.isFindCurrentUser
import com.example.calendar.helpers.convert.toLongUTC
import com.example.calendar.helpers.getEventInstances
import com.example.calendar.repository.server.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.threeten.bp.Duration
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.io.*
import java.lang.IllegalArgumentException


class EventServerRepository(val api: PlannerApi) : EventRepository {

    override fun getEventWithPatter(event_id: Long): Single<Pair<EventServer, List<EventPatternServer>>> {
        return api.getEventById(event_id)
            .zipWith(getPatterns(event_id),
                BiFunction
                { event: EventResponse, pattern: List<EventPatternServer> ->
                    if (event.count == 0 || event.data.isEmpty()) {
                        throw NotFind()
                    }
                    Pair(event.data[0], pattern)
                }
            )
    }

    private fun getFixPatterns(event_id: Long): Single<EventPatternResponse> {
        return api.getPatterns(event_id)
            .map {
                it.data = it.data.map { pattern ->
                    val newPatternRequest = PatternRequest.getPatternFromReceive(pattern.patternRequest)
                    pattern.patternRequest = newPatternRequest
                    pattern
                }
                it
            }
    }

    private fun fixPatterns(e: List<EventPatternServer>) =
        e.map {
            val request = PatternRequest.getPatternToSend(it.patternRequest)
            it.patternRequest = request
            it
        }

    private fun getFixPatternsRequest(e: List<PatternRequest>) =
        e.map {
            PatternRequest.getPatternToSend(it)
        }


    override fun fromTo(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Single<List<EventInstance>> {
        val startUTC = toLongUTC(startLocal.withZoneSameInstant(ZoneOffset.UTC))
        val endUTC = toLongUTC(endLocal.withZoneSameInstant(ZoneOffset.UTC))

        return api.getEventsFromTo(startUTC, endUTC)
            .map { it.data }
            .flattenAsFlowable { it }
            .flatMap { entity ->
                getFixPatterns(entity.id)
                    .zipWith(api.getUser(entity.owner_id, null, null),
                        BiFunction { p: EventPatternResponse, u: UserResponse ->
                            val user = u.data[0]
                            Pair(p.data, UserServer(user.id, user.username))
                        }
                    )
                    .map {
                        val list = ArrayList<EventInstance>()
                        val user = it.second
                        it.first.forEach { pattern ->
                            list.addAll(
                                getEventInstances(entity, pattern, user, startLocal, endLocal)
                            )
                        }
                        Pair(entity.id, list)
                    }
                    .toFlowable()
            }
            .toList()
            .map {
                val list = ArrayList<EventInstance>()
                it.forEach { l -> list.addAll(l.second) }
                list as List<EventInstance>

            }
            .observeOn(AndroidSchedulers.mainThread())
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
    ): Single<HashSet<ZonedDateTime>> {
        return fromTo(startLocal, endLocal)
            .flatMap {
                Observable.fromIterable(it)
                    .map { daysInEvent(it) }
                    .flatMapIterable { it }
                    .collect({ hashSetOf<ZonedDateTime>() }, { set, z -> set.add(z) })
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getEventById(eventId: Long): Single<Event> {
        return api.getEventById(eventId)
            .zipWith(
                getFixPatterns(eventId),
                BiFunction { e: EventResponse, p: EventPatternResponse ->
                    Event(e.data[0], p.data)
                }
            )
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateEvent(event: EventServer): Completable {
        return api.updateEvent(event.id, event.eventRequest)
            .toCompletable()
            .observeOn(AndroidSchedulers.mainThread())
    }


    override fun insertEvent(
        eventRequest: EventRequest, patternRequests: ArrayList<PatternRequest>
    ): Completable {
        val fixPatternRequest = getFixPatternsRequest(patternRequests as List<PatternRequest>)
        return api.createEvent(eventRequest)
            .flatMap {
                val eventId = it.data[0].id
                val singles = fixPatternRequest.map {
                    api.createPattern(eventId, it)
                        .toObservable()
                }
                Observable.merge(singles)
                    .firstOrError()
            }
            .toCompletable()
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getPatterns(event_id: Long): Single<List<EventPatternServer>> {
        return getFixPatterns(event_id)
            .map { it.data }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun createPatterns(event_id: Long, patternRequests: List<PatternRequest>): Completable {
        val fixPatterResponse = getFixPatternsRequest(patternRequests)
        val com = fixPatterResponse.map {
            api.createPattern(event_id, it)
                .toCompletable()
        }
        return Completable.merge(com)
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updatePatterns(patterns: List<EventPatternServer>): Completable {
        val fixPattern = fixPatterns(patterns)
        val com = fixPattern.map {
            api.updatePattern(it.id, it.patternRequest)
                .toCompletable()
        }
        return Completable.merge(com)
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deletePatterns(patterns_id: List<Long>): Completable {
        val com = patterns_id.map {
            api.deletePatternById(it)
                .toCompletable()
        }
        return Completable.merge(com)
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteEvent(event_id: Long): Completable {
        return api.deleteEventById(event_id)
            .toCompletable()
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun export(uri: String): Single<ResponseBody> {
        return api.exportICal()
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun import(file: File): Completable {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return api.importICal(body)
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getToken(permissions: List<PermissionRequest>): Single<String> {
        return api.getLink(permissions)
            .map { it.string() }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun activateToken(token: String): Completable {
        return api.activateLink(token)
            .toCompletable()
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getUser(user_id: String?, email: String?): Single<UserServer> {
        return api.getUser(user_id, null, email)
            .map {
                if (it.isEmpty()) {
                    throw NotFind()
                }
                val user = it.data[0]
                UserServer(user.id, user.username)
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getPermission(user_id: String, permissions: List<PermissionRequest>): Completable {
        val c = permissions.map {
            api.getGrant(it.action, it.entity_id, it.entity_type, user_id)
                .toObservable()
                .onErrorResumeNext(Observable.empty<PermissionResponse>())
                .ignoreElements()
        }
        // on exist permission throw NoContent
        return Completable.merge(c)
            .observeOn(AndroidSchedulers.mainThread())
    }


    private fun getUserIdByMine(it: PermissionServer, mine: Boolean) = if (mine) it.user_id else it.owner_id


    override fun getEventPermissions(
        mine: Boolean,
        namePermissionAll: String,
        nameUserNotFind: String
    ): Single<List<PermissionModel>> {
        val entityType = EntityType.EVENT
        return api.getPermissions(entityType, mine)
            .map { it.data }
            .flattenAsFlowable { it }
            .flatMap {
                if (!isFindCurrentUser()) {
                    throw NotAuthorized()
                }

                var single = Observable.empty<PermissionModel>().firstOrError()
                if (it.entity_id == it.user_id || it.entity_id == it.owner_id) {
                    // permission for all calendar
                    single = getUser(getUserIdByMine(it, mine), null)
                        .map { user ->
                            PermissionModel(
                                it.id, it.entity_id, mine, entityType,
                                namePermissionAll, user.id, user.username, it.action_type, true
                            )
                        }
                } else {
                    // permission for entity
                    val entity_id = it.entity_id.toLongOrNull()
                    if (entity_id == null) {
                        throw InternalError()
                    }
                    single = api.getEventById(entity_id)
                        .zipWith(
                            getUser(getUserIdByMine(it, mine), null),
                            BiFunction { event: EventResponse, user: UserServer ->
                                val e = event.data[0]
                                PermissionModel(
                                    it.id, it.entity_id, mine, entityType,
                                    e.name, user.id, user.username, it.action_type, false
                                )
                            })
                }
                single.toFlowable()
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun revokeEventPermission(event_permission: PermissionModel): Completable {
        if (event_permission.entityType != EntityType.EVENT) {
            throw IllegalArgumentException()
        }
        val start = api.revokePermissionById(event_permission.id)

        if (event_permission.isAll) {
            return start.toCompletable()
        }

        val event_id = event_permission.entity_id.toLong()

        return start
            .flatMap { getPatterns(event_id) }
            .flatMap {
                api.getPermissionsById(EntityType.PATTERN, it.map { p -> p.id })
                    .map { it.data }
                    .map { l ->
                        l.filter { p -> p.action_type == event_permission.actionType }
                    }
                    .flatMap {
                        Observable.fromIterable(it)
                            .flatMap {
                                api.revokePermissionById(it.id)
                                    .toObservable()
                            }
                            .toList()
                    }
            }
            .toCompletable()
    }

    override fun revokeEventPermission(event_permissions: List<PermissionModel>): Completable {
        val e = event_permissions.map {
            revokeEventPermission(it)
                .onErrorComplete()
        }

        return Completable.merge(e)
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getPermissionsById(entity_id: Long, entity_type: EntityType): Single<List<PermissionServer>> {
        return api.getPermissionsById(entity_type, listOf(entity_id))
            .map { it.data }
            .observeOn(AndroidSchedulers.mainThread())
    }
}

