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

    override fun fromTo(startLocal: ZonedDateTime, endLocal: ZonedDateTime): Single<List<EventInstance>> {
        val startUTC = toLongUTC(startLocal.withZoneSameInstant(ZoneOffset.UTC))
        val endUTC = toLongUTC(endLocal.withZoneSameInstant(ZoneOffset.UTC))

        return api.getEventsFromTo(startUTC, endUTC)
            .map { it.data }
            .flattenAsFlowable { it }
            .flatMap { entity ->
                api.getPatterns(entity.id)
                    .zipWith(api.getUser(entity.owner_id, null, null),
                        BiFunction { p: EventPatternResponse, u: UserResponse ->
                            val user = u.data[0]
                            Pair(p.data, UserServer(user.id, user.username ?: ""))
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
                api.getPatterns(eventId),
                BiFunction { e: EventResponse, p: EventPatternResponse ->
                    Event(e.data[0], p.data)
                }
            )
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun insertEvent(
        eventRequest: EventRequest, patternRequests: ArrayList<PatternRequest>
    ): Completable {
        return api.createEvent(eventRequest)
            .flatMap {
                val eventId = it.data[0].id
                val singles = patternRequests.map {
                    api.createPattern(eventId, it)
                        .toObservable()
                }
                Observable.merge(singles)
                    .firstOrError()
            }
            .toCompletable()
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateEvent(event: EventInstance): Completable {
        return api.updateEvent(event.entity.id, EventRequest(event.entity))
            .toCompletable()
            .mergeWith(
                api.updatePattern(event.pattern.id, event.pattern.patternRequest)
                    .toCompletable()
            )
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteEvent(event: EventInstance): Completable {
        return api.deletePatternById(event.pattern.id)
            .toCompletable()
            .observeOn(AndroidSchedulers.mainThread())
//            .andThen(
//                api.deleteEventById(event.entity.id)
//                    .ignoreElements()
//            )
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

    override fun getUserByEmail(email: String): Single<UserServer> {
        return api.getUser(null, null, email)
            .map {
                if (it.isEmpty()) {
                    throw NotFind()
                }
                val user = it.data[0]
                UserServer(user.id, user.username ?: "")
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getPermission(user_id: String, permissions: List<PermissionRequest>): Completable {
        if (permissions.isEmpty()) {
            return Completable.fromRunnable { }
        }
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

    // todo refactor
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
                    single = api
                        .getUser(getUserIdByMine(it, mine), null, null)
                        .map {
                            if (it.isEmpty())
                                throw NotFind()
                            it.data[0]
                        }
                        .map { user ->
                            PermissionModel(
                                it.id, it.entity_id, mine, entityType,
                                namePermissionAll, user.username, it.action_type, true
                            )
                        }
                } else {
                    // permission for entity
                    val entity_id = it.entity_id.toLongOrNull()
                    if (entity_id == null) {
                        throw InternalError()
                    }
                    single = api
                        .getEventById(entity_id)
                        .zipWith(
                            api.getUser(getUserIdByMine(it, mine), null, null)
                                .map {
                                    if (it.isEmpty())
                                        throw NotFind()
                                    it.data[0]
                                },
                            BiFunction { event: EventResponse, user: UserModel ->
                                val e = event.data[0]
                                PermissionModel(
                                    it.id, it.entity_id, mine, entityType,
                                    e.name, user.username, it.action_type, false
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
            .flatMap { api.getPatterns(event_id) }
            .map { it.data }
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
}

