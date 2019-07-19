package com.example.calendar.eventFragment

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.calendar.auth.getCurrentFirebaseUser
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.AccessDenied
import com.example.calendar.repository.server.EventRepository
import com.example.calendar.repository.server.model.*
import io.reactivex.Completable
import io.reactivex.functions.BiFunction
import ru.terrakok.cicerone.Router
import java.lang.IllegalArgumentException

@InjectViewState
class EditEventPresenter(
    private val router: Router,
    private val eventRepository: EventRepository,
    var eventInstance: EventInstance?
) : BaseMvpSubscribe<EditEventView>() {

    private var isDelete = false

    var canDelete = true
    var canUpdate = true

    val isEditMode = eventInstance != null

    var event_id = eventInstance?.entity_id ?: -1
    val pattern_ids = ArrayList<Long>()

    lateinit var entity: EventServer
    val patterns = ArrayList<EventPatternServer>()
    val delete_patterns = ArrayList<EventPatternServer>()

    init {
        if (isEditMode) {
            val e = eventInstance!!
            val u = eventRepository.getEventWithPatter(e.entity_id)
                .subscribe({
                    entity = it.first
                    patterns.addAll(it.second)
                    pattern_ids.addAll(it.second.map { p -> p.id })

                    viewState.updateEventInfo(
                        e.user,
                        entity.eventRequest,
                        patterns.map { p -> p.patternRequest } as ArrayList<PatternRequest>
                    )
                }, {
                    onLoadingFailed(it.toString())
                    router.exit()
                })
            unsubscribeOnDestroy(u)
        }
    }

    fun addPattern(p: PatternRequest) {
        if (!isEditMode) {
            return
        }
        patterns.add(EventPatternServer.getStubPatternServer(p))
    }

    fun deletePattern(pos: Int)  {
        if (!isEditMode || !canDelete) {
            viewState.showError("У вас не доступа на удаления")
            return
        }
        delete_patterns.add(patterns[pos])
        patterns.removeAt(pos)
        return
    }

    fun onUpdateAll(eventRequest: EventRequest, newPatterns: List<PatternRequest>) {
        if (!isEditMode || !canUpdate) {
            viewState.showError("У вас не доступа на обновление")
            return
        }

        entity.eventRequest = eventRequest

        if (newPatterns.size != patterns.size) {
            throw IllegalArgumentException()
        }

        val updates = patterns.zip(newPatterns).map {
            val p = it.first
            p.patternRequest = it.second
            p
        }

        val create_pattern = updates.filter { it.id < 0 }.map { it.patternRequest }
        val update_pattern = updates.filter { it.id >= 0 }
        val pattern_delete = delete_patterns.filter { it.id >= 0 }.map { it.id }

        val com = listOf(
            eventRepository.updateEvent(entity),
            eventRepository.updatePatterns(update_pattern),
            eventRepository.createPatterns(entity.id, create_pattern),
            eventRepository.deletePatterns(pattern_delete)
        )
        val u = Completable.merge(com)
            .subscribe(
                {
                    // todo forbined
                    // todo notfind
                    router.exit()
                },
                {
                    if (it is AccessDenied) {
                        canUpdate = false
                        onLoadingFailed("У вас не доступа на обновление")
                        return@subscribe
                    }
                    onLoadingFailed(it.toString())
                    router.exit()
                })
        unsubscribeOnDestroy(u)
    }

    fun onDeleteAll() {
        if (!isEditMode || !canDelete) {
            viewState.showError("У вас не доступа на удаления")
            return
        }

        isDelete = true
        val sub = eventRepository.deleteEvent(entity.id)
            .subscribe(
                {
                    onDeleteLoading()
                },
                { error ->
                    if (error is AccessDenied) {
                        canDelete = false
                        onLoadingFailed("У вас не доступа на удаление")
                        return@subscribe
                    }
                    onLoadingFailed(error.toString())
                })
        unsubscribeOnDestroy(sub)
    }

    private fun onLoadingFailed(error: String) {
        viewState.showError(error);
    }

    private fun onDeleteLoading() {
        router.exit()
    }
}